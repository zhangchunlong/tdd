package geektime.tdd.di;

import geektime.tdd.di.InjectionTest.ConstructorInjection.Injection.InjectorConstructor;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.internal.util.collections.Sets;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Nested
class ContextTest {
    ContextConfig config;
    TestComponent instance = new TestComponent() {
    };

    Dependency dependency = new Dependency() {
    };

    @BeforeEach
    public void setup() {
        config = new ContextConfig();
    }

    @Nested
    class TypeBinding {
        @Test
        public void should_bind_type_to_a_specific_instance() {
            config.instance(TestComponent.class, instance);

            Context context = config.getContext();
            assertSame(instance, context.get(ComponentRef.of(TestComponent.class)).get());
        }

        @ParameterizedTest(name = "supporting {0}")
        @MethodSource
        public void should_bind_type_to_a_injectable_component(Class<? extends TestComponent> componentType) {
            config.instance(Dependency.class, dependency);
            config.component(TestComponent.class, componentType);

            Optional<TestComponent> component = config.getContext().get(ComponentRef.of(TestComponent.class));
            assertTrue(component.isPresent());
            assertSame(dependency, component.get().dependency());
        }

        public static Stream<Arguments> should_bind_type_to_a_injectable_component() {
            return Stream.of(Arguments.of(Named.of("Constructor Injection", TypeBinding.ConstructorInjection.class)),
                    Arguments.of(Named.of("Field Injection", TypeBinding.FieldInjection.class)),
                    Arguments.of(Named.of("Method Injection", TypeBinding.MethodInjection.class)));
        }

        static class ConstructorInjection implements TestComponent {
            private Dependency dependency;

            @Inject
            public ConstructorInjection(Dependency dependency) {
                this.dependency = dependency;
            }

            @Override
            public Dependency dependency() {
                return dependency;
            }
        }

        static class FieldInjection implements TestComponent {
            @Inject
            Dependency dependency;

            @Override
            public Dependency dependency() {
                return dependency;
            }
        }

        static class MethodInjection implements TestComponent {
            private Dependency dependency;

            @Inject
            void install(Dependency dependency) {
                this.dependency = dependency;
            }

            @Override
            public Dependency dependency() {
                return dependency;
            }
        }

        @Test
        public void should_retrieve_empty_for_unbind_type() {
            Optional<TestComponent> component = config.getContext().get(ComponentRef.of(TestComponent.class));
            assertTrue(component.isEmpty());
        }

        @Test
        public void should_retrieve_bind_type_as_provider() {
            config.instance(TestComponent.class, instance);
            Context context = config.getContext();

//            ParameterizedType type = new TypeLiteral<Provider<Component>>(){}.getType();

//            assertEquals(Provider.class, type.getRawType());
//            assertEquals(Component.class, type.getActualTypeArguments()[0]);

            Provider<TestComponent> provider = context.get(new ComponentRef<Provider<TestComponent>>(){}).get();
            assertSame(instance, provider.get());
        }

        @Test
        public void should_not_retrieve_bind_type_as_unsupported_container() {
            config.instance(TestComponent.class, instance);
            Context context = config.getContext();

//            ParameterizedType type = new TypeLiteral<List<Component>>(){}.getType();
            assertFalse(context.get(new ComponentRef<List<TestComponent>>(){}).isPresent());
        }

//        static abstract class TypeLiteral<T> {
//            public ParameterizedType getType() {
//                return (ParameterizedType) ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
//            }
//        }
        @Nested
        public class WithQualifier {
            @Test
            public void should_bind_instance_with_multi_qualifiers() {
                config.instance(TestComponent.class, instance, new NamedLiteral("ChosenOne"), new SkywalkerLiteral());

                Context context = config.getContext();
                TestComponent chosenOne = context.get(ComponentRef.of(TestComponent.class, new NamedLiteral("ChosenOne"))).get();
                TestComponent skywalker = context.get(ComponentRef.of(TestComponent.class, new SkywalkerLiteral())).get();

                assertSame(instance, chosenOne);
                assertSame(instance, skywalker);
            }

            @Test
            public void should_bind_component_with_multi_qualifiers() {
                config.instance(Dependency.class, dependency);
                config.component(InjectorConstructor.class,
                        InjectorConstructor.class,
                        new NamedLiteral("ChosenOne"), new SkywalkerLiteral());

                Context context = config.getContext();
                InjectorConstructor chosenOne = context.get(ComponentRef.of(InjectorConstructor.class, new NamedLiteral("ChosenOne"))).get();
                InjectorConstructor skywalker = context.get(ComponentRef.of(InjectorConstructor.class, new SkywalkerLiteral())).get();

                assertSame(dependency, chosenOne.dependency);
                assertSame(dependency, skywalker.dependency);
            }

            @Test
            public void should_throw_exception_if_illegal_qualifier_given_to_instance() {
                assertThrows(ContextConfigException.class, () -> config.instance(TestComponent.class, instance, new TestLiteral()));
            }

            @Test
            public void should_throw_exception_if_illegal_qualifier_given_to_component() {
                assertThrows(ContextConfigException.class, () -> config.component(InjectorConstructor.class, InjectorConstructor.class, new TestLiteral()));
            }

            @Test
            public void should_retrieve_bind_type_as_provider() {
                config.instance(TestComponent.class, instance, new NamedLiteral("ChosenOne"), new SkywalkerLiteral());

                Optional<Provider<TestComponent>> provider = config.getContext().get(new ComponentRef<Provider<TestComponent>>(new SkywalkerLiteral()) {
                });
                assertTrue(provider.isPresent());
            }

        }

        @Nested
        public class WithScope {
            static class NotSingleton {
            }

            @Test
            public void should_not_be_singleton_scope_by_default() {
                config.component(NotSingleton.class, NotSingleton.class);
                Context context = config.getContext();

                assertNotSame(context.get(ComponentRef.of(NotSingleton.class)).get(), context.get(ComponentRef.of(NotSingleton.class)).get());
            }

            @Test
            public void should_bind_component_as_singleton_scoped() {
                config.component(NotSingleton.class, NotSingleton.class, new SingletonLiteral());
                Context context = config.getContext();

                assertSame(context.get(ComponentRef.of(NotSingleton.class)).get(), context.get(ComponentRef.of(NotSingleton.class)).get());
            }

            @Singleton
            static class SingletonAnnotated implements Dependency {
            }

            @Test
            public void should_retrieve_scope_annotation_from_component() {
                config.component(Dependency.class, SingletonAnnotated.class);
                Context context = config.getContext();

                assertSame(context.get(ComponentRef.of(Dependency.class)).get(), context.get(ComponentRef.of(Dependency.class)).get());
            }

            @Test
            public void should_bind_component_as_customized_scope() {
                config.scope(Pooled.class, PooledProvider::new);
                config.component(NotSingleton.class, NotSingleton.class, new PooledLiteral());
                Context context = config.getContext();

                List<NotSingleton> instances = IntStream.range(0, 5).mapToObj(i -> context.get(ComponentRef.of(NotSingleton.class)).get()).toList();

                assertEquals(PooledProvider.MAX, new HashSet<>(instances).size());
            }

            @Test
            public void should_throw_exception_if_multi_scope_provided() {
                assertThrows(ContextConfigException.class, () -> config.component(NotSingleton.class, NotSingleton.class, new SingletonLiteral(), new PooledLiteral()));
            }

            @Singleton
            @Pooled
            static class MultiScopeAnnotated {
            }

            @Test
            public void should_throw_exception_if_multi_scope_annotated() {
                assertThrows(ContextConfigException.class, () -> config.component(MultiScopeAnnotated.class, MultiScopeAnnotated.class));
            }

            @Test
            public void should_throw_exception_if_scope_undefined() {
                assertThrows(ContextConfigException.class, () -> config.component(NotSingleton.class, NotSingleton.class, new PooledLiteral()));
            }

            @Nested
            public class WithQualifier {
                @Test
                public void should_not_be_singleton_scope_by_default() {
                    config.component(NotSingleton.class, NotSingleton.class, new SkywalkerLiteral());
                    Context context = config.getContext();

                    assertNotSame(context.get(ComponentRef.of(NotSingleton.class, new SkywalkerLiteral())).get(), context.get(ComponentRef.of(NotSingleton.class, new SkywalkerLiteral())).get());
                }

                @Test
                public void should_bind_component_as_singleton_scoped() {
                    config.component(NotSingleton.class, NotSingleton.class, new SingletonLiteral(), new SkywalkerLiteral());
                    Context context = config.getContext();

                    assertSame(context.get(ComponentRef.of(NotSingleton.class, new SkywalkerLiteral())).get(), context.get(ComponentRef.of(NotSingleton.class, new SkywalkerLiteral())).get());
                }

                @Test
                public void should_retrieve_scope_annotation_from_component() {
                    config.component(Dependency.class, SingletonAnnotated.class, new SkywalkerLiteral());
                    Context context = config.getContext();

                    assertSame(context.get(ComponentRef.of(Dependency.class, new SkywalkerLiteral())).get(), context.get(ComponentRef.of(Dependency.class, new SkywalkerLiteral())).get());
                }
            }
        }
    }

    @Nested
    public class DependencyCheck {

        @ParameterizedTest
        @MethodSource
        public void should_throw_exception_if_dependency_not_found(Class<? extends TestComponent> component) {
            config.component(TestComponent.class, component);

            assertThrows(ContextConfigError.class, () -> config.getContext());
        }

        public static Stream<Arguments> should_throw_exception_if_dependency_not_found() {
            return Stream.of(Arguments.of(Named.of("Inject Constructor", DependencyCheck.MissingDependencyConstructor.class)),
                    Arguments.of(Named.of("Inject Field", DependencyCheck.MissingDependencyField.class)),
                    Arguments.of(Named.of("Inject Method", DependencyCheck.MissingDependencyMethod.class)),
                    Arguments.of(Named.of("Provider in Inject Constructor", MissingDependencyProviderConstructor.class)),
                    Arguments.of(Named.of("Provider in Inject Field", MissingDependencyProviderField.class)),
                    Arguments.of(Named.of("Provider in Inject Method", MissingDependencyProviderMethod.class)),
                    Arguments.of(Named.of("Scoped", MissingDependencyScoped.class)),
                    Arguments.of(Named.of("Scoped Provider", MissingDependencyProviderScoped.class))
            );
        }

        static class MissingDependencyConstructor implements TestComponent {
            @Inject
            public MissingDependencyConstructor(Dependency dependency) {
            }
        }

        static class MissingDependencyField implements TestComponent {
            @Inject
            Dependency dependency;
        }

        static class MissingDependencyMethod implements TestComponent {
            @Inject
            void install(Dependency dependency) {
            }
        }

        static class MissingDependencyProviderConstructor implements TestComponent {
            @Inject
            public MissingDependencyProviderConstructor(Provider<Dependency> dependency) {
            }
        }

        static class MissingDependencyProviderField implements TestComponent {
            @Inject
            Provider<Dependency> dependency;
        }

        static class MissingDependencyProviderMethod implements TestComponent {
            @Inject
            void install(Provider<Dependency> dependency) {
            }
        }

        @Singleton
        static class MissingDependencyScoped implements TestComponent {
            @Inject
            Dependency dependency;
        }

        @Singleton
        static class MissingDependencyProviderScoped implements TestComponent {
            @Inject
            Provider<Dependency> dependency;
        }


        @ParameterizedTest(name = "cyclic dependency between {0} and {1}")
        @MethodSource
        public void should_throw_exception_if_cyclic_dependencies_found(Class<? extends TestComponent> component,
                                                                        Class<? extends Dependency> dependency) {
            config.component(TestComponent.class, component);
            config.component(Dependency.class, dependency);

            assertThrows(ContextConfigError.class, () -> config.getContext());
        }

        public static Stream<Arguments> should_throw_exception_if_cyclic_dependencies_found() {
            List<Arguments> arguments = new ArrayList<>();
            for (Named component : List.of(Named.of("Inject Constructor", CyclicTestComponentInjectConstructor.class),
                    Named.of("Inject Field", CyclicTestComponentInjectField.class),
                    Named.of("Inject Method", DependencyCheck.CyclicComponentInjectMethod.class)))
                for (Named dependency : List.of(Named.of("Inject Constructor", DependencyCheck.CyclicDependencyInjectConstructor.class),
                        Named.of("Inject Field", DependencyCheck.CyclicDependencyInjectField.class),
                        Named.of("Inject Method", DependencyCheck.CyclicDependencyInjectMethod.class)))
                    arguments.add((Arguments.of(component, dependency)));
            return arguments.stream();
        }

        static class CyclicTestComponentInjectConstructor implements TestComponent {
            @Inject
            public CyclicTestComponentInjectConstructor(Dependency dependency) {
            }
        }

        static class CyclicTestComponentInjectField implements TestComponent {
            @Inject
            Dependency dependency;
        }

        static class CyclicComponentInjectMethod {
            @Inject
            void install(Dependency dependency) {
            }
        }

        static class CyclicDependencyInjectConstructor implements Dependency {
            @Inject
            public CyclicDependencyInjectConstructor(TestComponent component) {
            }
        }

        static class CyclicDependencyInjectField implements Dependency {
            @Inject
            TestComponent component;
        }

        static class CyclicDependencyInjectMethod implements Dependency {
            @Inject
            void install(TestComponent component) {
            }
        }

        @ParameterizedTest //A->B->C->A
        @MethodSource
        public void should_throw_exception_if_transitive_cyclic_dependencies_found(Class<? extends TestComponent> component,
                                                                                   Class<? extends Dependency> dependency,
                                                                                   Class<? extends AnotherDependency> anotherDependency) {
            config.component(TestComponent.class, component);
            config.component(Dependency.class, dependency);
            config.component(AnotherDependency.class, anotherDependency);

            assertThrows(ContextConfigError.class, () -> config.getContext());
        }

        public static Stream<Arguments> should_throw_exception_if_transitive_cyclic_dependencies_found() {
            List<Arguments> arguments = new ArrayList<>();
            for (Named component : List.of(Named.of("Inject Constructor", CyclicTestComponentInjectConstructor.class),
                    Named.of("Inject Field", CyclicTestComponentInjectField.class),
                    Named.of("Inject Method", DependencyCheck.CyclicComponentInjectMethod.class)))
                for (Named dependency : List.of(Named.of("Inject Constructor", DependencyCheck.IndirectCyclicDependencyInjectConstructor.class),
                        Named.of("Inject Field", DependencyCheck.IndirectCyclicDependencyInjectField.class),
                        Named.of("Inject Method", DependencyCheck.IndirectCyclicDependencyInjectMethod.class)))
                    for (Named anotherDependency : List.of(Named.of("Inject Constructor", DependencyCheck.IndirectCyclicAnotherDependencyInjectConstructor.class),
                            Named.of("Inject Field", DependencyCheck.IndirectCyclicAnotherInjectField.class),
                            Named.of("Inject Method", DependencyCheck.IndirectCyclicAnotherInjectMethod.class)))
                        arguments.add((Arguments.of(component, dependency, anotherDependency)));
            return arguments.stream();
        }

        static class IndirectCyclicDependencyInjectConstructor implements Dependency {
            @Inject
            public IndirectCyclicDependencyInjectConstructor(AnotherDependency dependency) {
            }
        }

        static class IndirectCyclicDependencyInjectField implements Dependency {
            @Inject
            AnotherDependency anotherDependency;
        }

        static class IndirectCyclicDependencyInjectMethod implements Dependency {
            @Inject
            void install(AnotherDependency dependency) {
            }
        }

        static class IndirectCyclicAnotherDependencyInjectConstructor implements AnotherDependency {
            @Inject
            public IndirectCyclicAnotherDependencyInjectConstructor(TestComponent component) {
            }
        }

        static class IndirectCyclicAnotherInjectField implements AnotherDependency {
            @Inject
            TestComponent component;
        }

        static class IndirectCyclicAnotherInjectMethod implements AnotherDependency {
            @Inject
            void install(TestComponent component) {
            }
        }

        static class CyclicDependencyProviderConstructor implements Dependency {
            @Inject
            public CyclicDependencyProviderConstructor(Provider<TestComponent> component) {
            }
        }

        @Test
        public void should_not_throw_exception_if_cyclic_dependency_via_provider() {
            config.component(TestComponent.class, CyclicTestComponentInjectConstructor.class);
            config.component(Dependency.class, CyclicDependencyProviderConstructor.class);

            Context context = config.getContext();
            assertTrue(context.get(ComponentRef.of(TestComponent.class)).isPresent());
        }

        @Nested
        public class WithQualifier {
            @ParameterizedTest
            @MethodSource
            public void should_throw_exception_if_dependency_with_qualifier_not_found(Class<? extends TestComponent> component) {
                config.instance(Dependency.class, dependency);
                config.component(TestComponent.class, component, new NamedLiteral("Owner"));

                assertThrows(ContextConfigError.class, () -> config.getContext());
            }

            public static Stream<Arguments> should_throw_exception_if_dependency_with_qualifier_not_found() {
                return Stream.of(
                        Named.of("Inject Constructor with Qualifier", InjectConstructor.class),
                        Named.of("Inject Field with Qualifier", InjectField.class),
                        Named.of("Inject Method with Qualifier", InjectMethod.class),
                        Named.of("Provider in Inject Constructor with Qualifier", InjectConstructorProvider.class),
                        Named.of("Provider in Inject Field with Qualifier", InjectFieldProvider.class),
                        Named.of("Provider in Inject Method with Qualifier", InjectMethodProvider.class))
                        .map(Arguments::of);
            }

            static class InjectConstructor implements TestComponent {
                @Inject
                public InjectConstructor(@Skywalker Dependency dependency) {
                }
            }

            static class InjectField implements TestComponent {
                @Inject
                @Skywalker
                Dependency dependency;
            }

            static class InjectMethod implements TestComponent {
                @Inject
                void install(@Skywalker Dependency dependency){
                }
            }

            static class InjectConstructorProvider implements TestComponent {
                @Inject
                public InjectConstructorProvider(@Skywalker Provider<Dependency> dependency) {
                }
            }

            static class InjectFieldProvider implements TestComponent {
                @Inject
                @Skywalker
                Provider<Dependency> dependency;
            }

            static class InjectMethodProvider implements TestComponent {
                @Inject
                void install(@Skywalker Provider<Dependency> dependency) {
                }
            }

            //A -> @Skywalker A -> @Named A(instance)
            static class SkywalkerInjectConstructor implements Dependency {
                @Inject
                public SkywalkerInjectConstructor(@jakarta.inject.Named("ChosenOne") Dependency dependency) {
                }
            }

            static class SkywalkerInjectField implements Dependency {
                @Inject
                @jakarta.inject.Named("ChosenOne")
                Dependency dependency;
            }

            static class SkywalkerInjectMethod implements Dependency {
                @Inject
                void install(@jakarta.inject.Named("ChosenOne") Dependency dependency) {
                }
            }

            static class NotCyclicInjectConstructor implements Dependency {
                @Inject
                public NotCyclicInjectConstructor(@Skywalker Dependency dependency) {
                }
            }

            static class NotCyclicInjectField implements Dependency {
                @Inject
                @Skywalker
                Dependency dependency;
            }

            static class NotCyclicInjectMethod implements Dependency {
                @Inject
                void install(@Skywalker Dependency dependency) {
                }
            }

            @ParameterizedTest(name = "{1} -> @Skywalker({0}) -> @Named(\"ChosenOne\") not cyclic dependencies")
            @MethodSource
            public void should_not_throw_cyclic_exception_if_component_with_same_type_tagged_with_different_qualifier(
                    Class<? extends Dependency> skywalker, Class<? extends  Dependency> notCyclic) {
                config.instance(Dependency.class, dependency, new NamedLiteral("ChosenOne"));
                config.component(Dependency.class, skywalker, new SkywalkerLiteral());
                config.component(Dependency.class, notCyclic);

                assertDoesNotThrow(() -> config.getContext());
            }

            public static Stream<Arguments> should_not_throw_cyclic_exception_if_component_with_same_type_tagged_with_different_qualifier() {
                List<Arguments> arguments = new ArrayList<>();
                for(Named skywalker: List.of(Named.of("Inject Constructor", SkywalkerInjectConstructor.class),
                        Named.of("Inject Field", SkywalkerInjectField.class),
                        Named.of("Inject Method", SkywalkerInjectMethod.class)))
                    for(Named notCyclic: List.of(Named.of("Inject Constructor", NotCyclicInjectConstructor.class),
                            Named.of("Inject Field", NotCyclicInjectField.class),
                            Named.of("Inject Field", NotCyclicInjectMethod.class)))
                        arguments.add(Arguments.of(skywalker, notCyclic));
                return arguments.stream();
            }
        }
    }

    @Nested
    public class DSL {
        interface API {
        }
        static class Implementation implements API {
        }

        @Test
        public void should_bind_instance_as_its_declaration_type() {
            Implementation instance = new Implementation();
            config.from(new Config() {
                Implementation implementation = instance;
            });

            Context context = config.getContext();
            Implementation actual = context.get(ComponentRef.of(Implementation.class, null)).get();
            assertSame(instance, actual);
        }

        @Test
        public void should_bind_component_as_its_own_type() {
            Implementation instance = new Implementation();
            config.from(new Config() {
                Implementation implementation;
            });

            Context context = config.getContext();
            Implementation actual = context.get(ComponentRef.of(Implementation.class)).get();
            assertNotNull(actual);
        }

        @Test
        public void should_bind_instance_using_export_type() {
            Implementation instance = new Implementation();
            config.from(new Config() {
                @Export(API.class)
                Implementation implementation = instance;
            });

            Context context = config.getContext();
            API actual = context.get(ComponentRef.of(API.class)).get();
            assertSame(instance, actual);
        }

        @Test
        public void should_bind_component_using_export_type() {
            Implementation instance = new Implementation();
            config.from(new Config() {
                @Export(API.class)
                Implementation implementation;
            });

            Context context = config.getContext();
            API actual = context.get(ComponentRef.of(API.class)).get();
            assertNotNull(actual);
        }

        @Test
        public void should_bind_instance_with_qualifier() {
            Implementation instance = new Implementation();
            config.from(new Config() {
                @Skywalker
                API implementation = instance;
            });

            Context context = config.getContext();
            API actual = context.get(ComponentRef.of(API.class, new SkywalkerLiteral())).get();
            assertSame(instance, actual);
        }

        @Test
        public void should_bind_component_with_qualifier() {
            config.from(new Config() {
                @Skywalker
                Implementation implementation;
            });

            Context context = config.getContext();
            Implementation actual = context.get(ComponentRef.of(Implementation.class, new SkywalkerLiteral())).get();
            assertNotNull(actual);
        }

    }
}

