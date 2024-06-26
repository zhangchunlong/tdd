package geektime.tdd.model;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Optional;

public class TestApplication {
    public static void main(String[] args) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("student");
        EntityManager entityManager = factory.createEntityManager();

        entityManager.getTransaction().begin();

        StudentRepository studentRepository = new StudentRepository(entityManager);
        Student john = studentRepository.save(new Student("john", "smith", "john.smith@email.com"));

        entityManager.getTransaction().commit();

        System.out.println(john.getId());

        Optional<Student> loaded = studentRepository.findById(john.getId());

        System.out.println(loaded);

        System.out.println(studentRepository.findByEmail("john.smith@email.com"));
        System.out.println(studentRepository.findByEmail("john.smith@email1.com"));

    }
}
