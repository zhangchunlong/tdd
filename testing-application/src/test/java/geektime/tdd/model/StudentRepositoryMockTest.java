package geektime.tdd.model;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class StudentRepositoryMockTest {
    private EntityManager manager;
    private StudentRepository repository;
    private Student john = new Student("john", "smith", "john.smith@email.com");

    @BeforeEach
    public void before() {
        manager = mock(EntityManager.class);
        repository = new StudentRepository(manager);
    }

    @Test
    public void should_generate_id_for_saved_entity() {
        repository.save(john);
        verify(manager).persist(john);
    }

    @Test
    public void should_be_able_to_loaded_saved_student_by_id() {
        when(manager.find(any(), any())).thenReturn(john);

        assertEquals(john, repository.findById(1L).get());

        verify(manager).find(Student.class, 1L);
    }

    @Test
    public void should_be_able_to_loaded_saved_student_by_email() {
        TypedQuery query = mock(TypedQuery.class);

        when(manager.createQuery(any(), any())).thenReturn(query);
        when(query.setParameter(any(String.class), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(asList(john));

        assertEquals(john, repository.findByEmail("john.smith@email.com").get());

        verify(manager).createQuery("SELECT s from Student s where s.email = :email", Student.class);
        verify(query).setParameter("email","john.smith@email.com");
    }
}
