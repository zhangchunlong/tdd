package geektime.tdd.model;

import java.util.*;

public class StudentRepository {
    private List<Student> students;

    public StudentRepository(Student... students) {
        this.students = new ArrayList<>(Arrays.asList(students));
    }

    public List<Student> all() {
        return Collections.unmodifiableList(students);
    }

    public Optional<Student> findById(long id) {
        return students.stream().filter(it -> it.getId() == id).findFirst();
    }

    public void save(Student student) {
        if(student.getId() == 0) {
            student = new Student(students.size() == 0? 1L: students.get(students.size()-1).getId() + 1, student.getFirstName(),
                    student.getLastName(), student.getEmail());
        }
        students.add(student);
    }
}
