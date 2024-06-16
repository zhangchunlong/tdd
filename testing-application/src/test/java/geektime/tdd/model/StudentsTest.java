package geektime.tdd.model;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StudentsTest {
    private EntityManagerFactory factory;
    private EntityManager manager;
    private Students students;

    @BeforeEach
    public void before() {
        factory = Persistence.createEntityManagerFactory("student");
        manager = factory.createEntityManager();
        students = new Students(manager);
    }

    static class Students {

        private EntityManager manager;

        public Students(EntityManager manager) {
            this.manager = manager;
        }

        public Student save(Student student) {
            manager.persist(student);
            return student;
        }

        public Optional<Student> findById(Long id) {
            return Optional.ofNullable(manager.find(Student.class, id));
        }
    }
    @AfterEach
    public void after() {
        manager.clear();
        manager.close();
        factory.close();
    }

    @Test
    public void should_save_students_to_db() {
        int before = manager.createNativeQuery("SELECT id, first_name, last_name, email from STUDENTS s").getResultList().size();

        //exercise
        manager.getTransaction().begin();
        Student saved = students.save(new Student("john", "smith", "john.smith@email.com"));
        manager.getTransaction().commit();

        //verify
        List result = manager.createNativeQuery("SELECT id, first_name, last_name, email from STUDENTS s").getResultList();

        assertEquals(before + 1, result.size());

        Object[] john = (Object[]) result.get(result.size()-1);
        assertEquals(saved.getId(), john[0]);
        assertEquals(saved.getFirstName(), john[1]);
        assertEquals(saved.getLastName(), john[2]);
        assertEquals(saved.getEmail(), john[3]);
    }
    //find by id
    @Test
    public void should_be_able_to_load_saved_student_by_id() {
        manager.getTransaction().begin();
        Student john = students.save(new Student("john", "smith", "john.smith@email.com"));
        manager.getTransaction().commit();

        Optional<Student> loaded = students.findById(john.getId());
        assertTrue(loaded.isPresent());
    }

}
