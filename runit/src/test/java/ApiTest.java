import geektime.tdd.model.Student;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ApiTest extends JerseyTest {
    @Override
    protected Application configure() {
        return new geektime.tdd.Application();
    }

    @Test
    public void should_fetch_all_students_from_api() {
        Student[] students = target("/students").request().get(Student[].class);
        assertEquals(3, students.length);
        assertEquals("john", students[0].getFirstName());
    }

    @Test
    public void should_be_able_fetch_student_by_id() {
        Student student = target("/students/1").request().get(Student.class);
        assertEquals("john", student.getFirstName());
    }

    @Test
    public void should_return_404_if_no_student_found() {
        Response response = target("/students/4").request().get(Response.class);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void should_create_students_via_api() {
        Student[] before = target("students").request().get(Student[].class);
        assertEquals(3, before.length);

        Form form = new Form();
        form.param("students[first_name]", "Hannah")
                .param("students[last_name]", "Abbort")
                .param("students[email]", "hannah.abbort@email.com");
        form.param("students[first_name]", "Cuthbert")
                .param("students[last_name]", "Binns")
                .param("students[email]", "cuthbert.binns@email.com");

        Response response = target("students").request().post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        Student[] after = target("students").request().get(Student[].class);
        assertEquals(5, after.length);

        assertEquals("Hannah", after[3].getFirstName());
        assertEquals("Abbort", after[3].getLastName());
        assertEquals("hannah.abbort@email.com", after[3].getEmail());
        assertNotEquals(0, after[3].getId());

        assertEquals("Cuthbert", after[4].getFirstName());
        assertEquals("Binns", after[4].getLastName());
        assertEquals("cuthbert.binns@email.com", after[4].getEmail());
        assertNotEquals(0, after[4].getId());
    }
}
