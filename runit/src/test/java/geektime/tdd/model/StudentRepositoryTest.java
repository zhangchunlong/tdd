package geektime.tdd.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class StudentRepositoryTest {
    @Test
    public void should_save_students() {
        StudentRepository repository = new StudentRepository();
        assertEquals(0, repository.all().size());

        repository.save(new Student("john", "smith", "john.smith@email.com"));
        assertEquals(1, repository.all().size());

        assertNotEquals(0, repository.all().get(0).getId());
    }
}
