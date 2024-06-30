package geektime.tdd.di;

import org.junit.jupiter.api.Nested;

@Nested
public class ContainerTest {
}
interface Component {
    default Dependency dependency() {return  null;}
}

interface Dependency {
}

interface AnotherDependency {
}

