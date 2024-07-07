package geektime.tdd.di;

public class DependencyNotFoundException extends RuntimeException {
    private Component dependency;
    private Component component;

    public DependencyNotFoundException(Component component, Component dependency) {
        this.component = component;
        this.dependency = dependency;
    }

    public Component getDependency() {
        return dependency;
    }

    public Component getComponent() {
        return component;
    }
}
