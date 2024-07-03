package geektime.tdd.di;

import org.junit.jupiter.api.Nested;

@Nested
public class ContainerTest {
    @Nested
    public class DependenciesSelection {
        @Nested
        public class ProviderType {

        }

        @Nested
        public class Qualifier {

        }

    }

    @Nested
    public class LifecycleManagement {

    }
}
interface Component {
    default Dependency dependency() {return  null;}
}

interface Dependency {
}

interface AnotherDependency {
}

