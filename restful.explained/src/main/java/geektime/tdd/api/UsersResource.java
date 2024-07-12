package geektime.tdd.api;

import geektime.tdd.domain.User;
import geektime.tdd.domain.Users;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.hateoas.Link;

@Path("/users")
public class UsersResource {

    @Context
    private Users users;

    @Path("{id}")
    public UserResource findById(@PathParam("id")User.Id id) {
        return users.findById(id).map(UserResource::new).orElse(null);
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response register(@FormParam("name") String name, @FormParam("email") String email) {
        User user = users.create(name, email);
        return Response.created(Link.of("/users/{id}").expand(user.getId().value()).toUri()).build();
    }
}
