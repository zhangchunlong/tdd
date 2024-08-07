package geektime.tdd.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ResourceContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubResourceLocatorTest extends InjectableCallerTest{
    UriTemplate.MatchResult result;

    @BeforeEach
    public void before() {
        super.before();
        result = Mockito.mock(UriTemplate.MatchResult.class);
    }
    @Override
    protected Object initResource() {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(),
                new Class[]{
                        SubResourceMethods.class}, (proxy, method, args) -> {
                    lastCall = new LastCall(getMethodName(method.getName(),
                            Arrays.stream(method.getParameters()).map(p -> p.getType()).toList()),
                            args !=null?List.of(args):List.of());
                    return new Message();
                });
    }

    @Test
    public void should_inject_path_param_to_sub_resource_method() throws NoSuchMethodException {
        Class<String> type = String.class;
        String method = "getPathParam";
        String paramString = "path";
        String paramValue = "path";

        parameters.put("param", List.of(paramString));

        callInjectable(method, type);

        assertEquals(getMethodName(method, List.of(type)), lastCall.name());
        assertEquals(List.of(paramValue), lastCall.arguments());
    }

    @Override
    protected void callInjectable(String method, Class<?> type) throws NoSuchMethodException {
        SubResourceLocators.SubResourceLocator locator = new SubResourceLocators.SubResourceLocator(SubResourceMethods.class.getMethod(method, type));
        locator.match(result, "GET", new String[0], context, builder);
    }

    interface SubResourceMethods {
        @Path("/message/{param}")
        Message getPathParam(@PathParam("param") String path);

        @Path("/message/{param}")
        Message getPathParam(@PathParam("param") int value);

        @Path("/message/{param}")
        Message getPathParam(@PathParam("param") double value);

        @Path("/message/{param}")
        Message getPathParam(@PathParam("param") float value);

        @Path("/message/{param}")
        Message getPathParam(@PathParam("param") short value);

        @Path("/message/{param}")
        Message getPathParam(@PathParam("param") byte value);

        @Path("/message/{param}")
        Message getPathParam(@PathParam("param") boolean value);

        @Path("/message/{param}")
        Message getPathParam(@PathParam("param") BigDecimal value);

        @Path("/message/{param}")
        Message getPathParam(@PathParam("param") Converter value);

        @Path("/message/")
        Message getQueryParam(@QueryParam("param") String path);

        @Path("/message/")
        Message getQueryParam(@QueryParam("param") int value);

        @Path("/message/")
        Message getQueryParam(@QueryParam("param") double value);

        @Path("/message/")
        Message getQueryParam(@QueryParam("param") float value);

        @Path("/message/")
        Message getQueryParam(@QueryParam("param") short value);

        @Path("/message/")
        Message getQueryParam(@QueryParam("param") byte value);

        @Path("/message/")
        Message getQueryParam(@QueryParam("param") boolean value);

        @Path("/message/")
        Message getQueryParam(@QueryParam("param") BigDecimal value);

        @Path("/message/")
        Message getQueryParam(@QueryParam("param") Converter value);

        @Path("/message/")
        Message getContext(@Context SomeServiceInContext service);

        @Path("/message/")
        Message getContext(@Context ResourceContext context);

        @Path("/message/")
        Message getContext(@Context UriInfo uriInfo);
    }

    static class Message {
        @GET
        @Produces(MediaType.TEXT_PLAIN)
        public String content() {
            return "content";
        }


    }
}
