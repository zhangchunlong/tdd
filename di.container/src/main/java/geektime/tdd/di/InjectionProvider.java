package geektime.tdd.di;

import jakarta.inject.Inject;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.stream.Stream.concat;

class InjectionProvider<T> implements ContextConfig.ComponentProvider<T> {
    private Constructor<T> injectConstructor;
    private List<Field> injectFields;
    private List<Method> injectMethods;

    public InjectionProvider(Class<T> component) {
        if(Modifier.isAbstract(component.getModifiers())) throw new IllegalComponentException();

        this.injectConstructor = getInjectConstructor(component);
        this.injectFields = getInjectFields(component);
        this.injectMethods = getInjectMethods(component);

        if(injectFields.stream().anyMatch(f -> Modifier.isFinal(f.getModifiers()))) throw new IllegalComponentException();
        if(injectMethods.stream().anyMatch(m -> m.getTypeParameters().length != 0)) throw new IllegalComponentException();
    }



    @Override
    public T get(Context context) {
        try {
            T instance = injectConstructor.newInstance(toDependencies(context, this.injectConstructor));
            for(Field field: injectFields)
                field.set(instance, toDependency(context, field));
            for(Method method: injectMethods) {
                method.invoke(instance, toDependencies(context, method));
            }
            return instance;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Class<?>> getDependencies() {
        return concat(concat(stream(injectConstructor.getParameterTypes()),
                injectFields.stream().map(Field::getType)),
                injectMethods.stream().flatMap(m -> stream(m.getParameterTypes()))
        ).toList();
    }

    private static <T> List<Method> getInjectMethods(Class<T> component) {

        List<Method> injectMethods = traverse(component, (methods, current) -> injectable(current.getDeclaredMethods())
                                .filter(m -> isOverrideByInjectMethod(methods, m))
                                .filter(m -> isOverrideByNoInjectMethod(component, m)).toList());
        Collections.reverse(injectMethods);
        return injectMethods;
    }

    private static <T> List<Field> getInjectFields(Class<T> component) {

        return traverse(component, (fields, current) -> injectable(current.getDeclaredFields()).toList());
    }

    private static <Type> Constructor<Type> getInjectConstructor(Class<Type> implementation) {

        List<Constructor<?>> injectConstructors = injectable(implementation.getConstructors()).toList();
        if (injectConstructors.size() > 1) throw new IllegalComponentException();

        return (Constructor<Type>) injectConstructors.stream().findFirst().orElseGet(() -> defaultConstructor(implementation));
    }

    private static <Type> Constructor<Type> defaultConstructor(Class<Type> implementation) {
        try {
            return implementation.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalComponentException();
        }
    }

    private static <T extends AnnotatedElement> Stream<T> injectable(T[] declaredFields) {
        return stream(declaredFields).filter(f -> f.isAnnotationPresent(Inject.class));
    }

    private static boolean isOverride(Method m, Method o) {
        return o.getName().equals(m.getName()) &&
                Arrays.equals(o.getParameterTypes(), m.getParameterTypes());
    }

    private static <T> boolean isOverrideByNoInjectMethod(Class<T> component, Method m) {
        return stream(component.getDeclaredMethods()).filter(m1 -> !m1.isAnnotationPresent(Inject.class))
                .noneMatch(o -> isOverride(m, o));
    }

    private static boolean isOverrideByInjectMethod(List<Method> injectMethods, Method m) {
        return injectMethods.stream().noneMatch(o -> isOverride(m, o));
    }

    private static Object[] toDependencies(Context context, Executable executable) {
        return stream(executable.getParameterTypes()).map(t -> context.get(t).get()).toArray(Object[]::new);
    }

    private static Object toDependency(Context context, Field field) {
        return context.get(field.getType()).get();
    }

    private static <T> List<T> traverse(Class<?> component, BiFunction<List<T>, Class<?>, List<T>> finder) {
        List<T> members = new ArrayList<>();
        Class<?> current = component;
        while (current != Object.class) {
            members.addAll(finder.apply(members, current));
            current = current.getSuperclass();
        }
        return members;
    }
}
