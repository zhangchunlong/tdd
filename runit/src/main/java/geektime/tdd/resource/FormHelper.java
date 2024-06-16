package geektime.tdd.resource;

import geektime.tdd.model.Student;
import jakarta.ws.rs.core.MultivaluedMap;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FormHelper {
    public static Stream<Student> toStudents(MultivaluedMap<String, String> form) {
        List<String> firstNames = form.get("students[first_name]");
        List<String> lastNames = form.get("students[last_name]");
        List<String> emails = form.get("students[email]");
        return IntStream.range(0, firstNames.size()).mapToObj(it -> new Student(firstNames.get(it), lastNames.get(it), emails.get(it)));
    }
}
