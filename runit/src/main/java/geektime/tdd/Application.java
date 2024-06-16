package geektime.tdd;

import geektime.tdd.model.Student;
import geektime.tdd.model.StudentRepository;
import geektime.tdd.resource.StudentResource;
import org.glassfish.jersey.server.ResourceConfig;

public class Application extends ResourceConfig {
    public Application() {
        StudentRepository repository = new StudentRepository(
                new Student(1, "john", "smith", "john.smith@email.com"),
                new Student(2, "tralon", "zhang", "tralon.zhang@email.com"),
                new Student(3, "jmases", "test", "jmases.test@email.com")
        );
        StudentResource studentResource = new StudentResource(repository);
        super.registerInstances(studentResource);
    }
}
