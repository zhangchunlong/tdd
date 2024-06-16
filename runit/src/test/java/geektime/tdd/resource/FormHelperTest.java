package geektime.tdd.resource;

import geektime.tdd.model.Student;
import jakarta.ws.rs.core.Form;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FormHelperTest {
    @Test
    public void should_read_students_from_form() {
        Form form = new Form();
        form.param("students[first_name]", "Hannah")
                .param("students[last_name]", "Abbort")
                .param("students[email]", "hannah.abbort@email.com");
        form.param("students[first_name]", "Cuthbert")
                .param("students[last_name]", "Binns")
                .param("students[email]", "cuthbert.binns@email.com");
        Stream<Student> studentStream = FormHelper.toStudents(form.asMap());
        Student[] students = studentStream.toArray(Student[]::new);

        assertEquals(2, students.length);
        assertEquals("Hannah", students[0].getFirstName());
        assertEquals("Abbort", students[0].getLastName());
        assertEquals("hannah.abbort@email.com", students[0].getEmail());
    }
}
