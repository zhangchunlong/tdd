import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import jakarta.inject.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.jupiter.api.Assertions.*;

public class ComponentConstructionTest {
    interface Car {
        Engine getEngine();
    }
    interface Engine {
        String getName();
    }

    static class V8Engine implements Engine {
        @Override
        public String getName() {
            return "V8";
        }
    }

    static class V6Engine implements Engine {
        @Override
        public String getName() {
            return "V6";
        }
    }

    @Nested
    public class ConstructorInjection {
         static class CarInjectConstructor implements Car {
            private Engine engine;

            @Inject
            public CarInjectConstructor(Engine engine) {
                this.engine = engine;
            }
            @Override
            public Engine getEngine() {
                return engine;
            }
        }
        @Test
        public void constructor_injection() {
            Injector injector = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(Engine.class).to(V8Engine.class);
                    bind(Car.class).to(CarInjectMethod.class);
                }
            });
            Car car = injector.getInstance(Car.class);
            assertEquals("V8", car.getEngine().getName());
        }
        static class CarInjectField implements Car {
             @Inject
             private Engine engine;

             @Override
             public Engine getEngine() {
                 return engine;
             }
        }
        static class CarInjectMethod implements Car {
            private Engine engine;
            @Override
            public Engine getEngine() {
                return engine;
            }
            @Inject
            private void install(Engine engine) {
                this.engine = engine;
            }
        }

    }

    @Nested
    public class DependencySelection {
        static class A {
            private Provider<B> b;
            @Inject
            public A(Provider<B> b) {
                this.b = b;
            }
        }
        static class B {
            private A a;
            @Inject
            public B(A a) {
                this.a = a;
            }
        }

        @Test
        public void cyclic_dependencies() {
            Injector injector = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(A.class);
                    bind(B.class);
                }
            });
            A a = injector.getInstance(A.class);
            assertNotNull(a);

            B b = injector.getInstance(B.class);
            assertNotNull(b);
        }

        static class V8Car implements Car {
            private @Inject @Named("V8") Engine engine;

            @Override
            public Engine getEngine() {
                return engine;
            }
        }
        record NameLiteral(String value) implements Named {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Named.class;
            }
        }
        @Test
        public void selection() {
            Injector injector = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(Engine.class).annotatedWith(new NameLiteral("V8")).to(V8Engine.class);
                    bind(Engine.class).annotatedWith(new NameLiteral("V6")).to(V6Engine.class);
                    bind(Engine.class).annotatedWith(new LuxuryLiteral()).to(V8Engine.class);
                    bind(Car.class).to(LuxuryCar.class);

                }
            });
            Car car = injector.getInstance(Car.class);
            assertEquals("V8", car.getEngine().getName());
        }

        @Qualifier
        @Target({FIELD, PARAMETER, METHOD})
        @Retention(RUNTIME)
        public @interface Luxury {
        }
        record LuxuryLiteral() implements Luxury{
            @Override
            public Class<? extends Annotation> annotationType() {
                return Luxury.class;
            }
        }
        static class LuxuryCar implements Car {
            @Inject
            @Luxury
            private Engine engine;
            @Override
            public Engine getEngine() {
                return engine;
            }
        }
    }
    @Nested
    public class ContextInScope {
        @Test
        public void singleton() {
            Injector injector = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(Engine.class).annotatedWith(new DependencySelection.NameLiteral("V8")).to(V8Engine.class).in(Singleton.class);
                    bind(Engine.class).annotatedWith(new DependencySelection.NameLiteral("V6")).to(V6Engine.class);
                    bind(Engine.class).annotatedWith(new DependencySelection.LuxuryLiteral()).to(V8Engine.class);
                    bind(Car.class).to(DependencySelection.V8Car.class);
                }
            });

            Car car1 = injector.getInstance(Car.class);
            Car car2 = injector.getInstance(Car.class);
            assertNotSame(car1, car2);
            assertSame(car1.getEngine(), car2.getEngine());
        }
        @Target({TYPE, METHOD})
        @Retention(RUNTIME)
        @Scope
        public @interface BatchScoped {
        }
    }
}
