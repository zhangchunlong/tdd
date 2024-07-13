package geektime.tdd.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.container.ResourceContext;

interface ResourceRouter {
    OutBoundResponse dispatch(HttpServletRequest request, ResourceContext resourceContext);
}
