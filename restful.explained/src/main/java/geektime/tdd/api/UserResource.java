package geektime.tdd.api;

import geektime.tdd.domain.User;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.container.ResourceContext;
import jakarta.ws.rs.core.Context;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

public class UserResource {
    private User user;

    public UserResource(User user) {
        this.user = user;
    }

    @GET
    public EntityModel<User> get() {
        return EntityModel.of(user).add(Link.of("/users/{value}").expand(user.getId().value()));
    }

    @Path("/orders")
    public UserOrdersResource orders(@Context ResourceContext context) {
        return context.initResource(new UserOrdersResource(user));
    }
}
