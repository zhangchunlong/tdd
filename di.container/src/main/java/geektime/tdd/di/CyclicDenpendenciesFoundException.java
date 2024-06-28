package geektime.tdd.di;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CyclicDenpendenciesFoundException extends RuntimeException {
    private Set<Class<?>> components = new HashSet<>();

    public CyclicDenpendenciesFoundException(List<Class<?>> visiting) {
        components.addAll(visiting);
    }

    public Class<?>[] getComponents() {
        return components.toArray(Class<?>[]::new);
    }
}
