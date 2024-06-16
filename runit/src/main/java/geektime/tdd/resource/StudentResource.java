package geektime.tdd.resource;

import geektime.tdd.model.Student;
import geektime.tdd.model.StudentRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import java.util.List;


@Path("/students")
public class StudentResource {
    private StudentRepository repository;

    @Inject
    public StudentResource(StudentRepository repository) {
        this.repository = repository;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Student> all() {
        return repository.all();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") long id) {
        return repository.findById(id).map(Response::ok).orElse(Response.status(Response.Status.NOT_FOUND)).build();

    }
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response save(MultivaluedMap<String, String> form) {

        FormHelper.toStudents(form).forEach(repository::save);
        return Response.created(null).build();
    }

}
