package geektime.tdd.model;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.Optional;

public class StudentRepository {
    private  EntityManager manager;
    public StudentRepository(EntityManager manager) {
        this.manager = manager;
    }

    public Student save(Student student) {
        manager.persist(student);
        return student;
    }

    public Optional<Student> findById(Long id) {
        return Optional.ofNullable(manager.find(Student.class, id));
    }

    public Optional<Student> findByEmail(String email) {
//        CriteriaBuilder builder = manager.getCriteriaBuilder();
//
//        CriteriaQuery<Student> query = builder.createQuery(Student.class);
//        Root<Student> student = query.from(Student.class);
//        return manager.createQuery(query.where(builder.equal(student.get("email"), email)).select(student)).getResultList().stream().findFirst();

        TypedQuery<Student> query = manager.createQuery("SELECT s from Student s where s.email = :email", Student.class);
        return query.setParameter("email", email).getResultList().stream().findFirst();
    }
}
