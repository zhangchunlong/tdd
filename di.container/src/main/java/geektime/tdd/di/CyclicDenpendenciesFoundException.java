package geektime.tdd.di;

import java.util.HashSet;
import java.util.Set;

public class CyclicDenpendenciesFoundException extends RuntimeException {
    private Set<Class<?>> components = new HashSet<>();

    public CyclicDenpendenciesFoundException(Class<?> component) {
        components.add(component);
    }

    public CyclicDenpendenciesFoundException(Class<?> componentType, CyclicDenpendenciesFoundException e) {
        components.add(componentType);
        components.addAll(e.components);
    }

    public Class<?>[] getComponents() {
        return components.toArray(Class<?>[]::new);
    }
}
