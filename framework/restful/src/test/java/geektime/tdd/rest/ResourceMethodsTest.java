package geektime.tdd.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ResourceContext;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.RuntimeDelegate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceMethodsTest {

    @ParameterizedTest(name = "{3}")
    @CsvSource(textBlock = """
            GET,       /messages/hello,         Messages.hello,          GET and URT match
            POST,      /messages/hello,         Messages.postHello,      POST and URI match
            GET,       /messages/topics/1234,   Messages.topic1234,      GET with multiply choices
            GET,       /messages,               Messages.get,            GET with resource method without PATH
            PUT,       /messages/hello,         Messages.putHello,       PUT and URI match
            DELETE,    /messages/hello,         Messages.deleteHello,    DELETE and URI match
            PATCH,     /messages/hello,         Messages.patchHello,     PATCH and URI match
            HEAD,      /messages/hello,         Messages.headHello,      HEAD and URI match
            OPTIONS,   /messages/hello,         Messages.optionsHello,   OPTIONS and URI match
            """)
    public void should_match_resource_method_in_root_resource(String httpMethod, String path, String resourceMethod, String context) {
        ResourceMethods resourceMethods = new ResourceMethods(Messages.class.getMethods());
        UriTemplate.MatchResult result = new PathTemplate("/messages").match(path).get();
        String remaining = result.getRemaining() == null? "": result.getRemaining();
        ResourceRouter.ResourceMethod method = resourceMethods.findResourceMethods(remaining, httpMethod).get();

        assertEquals(resourceMethod, method.toString());
    }

    @ParameterizedTest(name = "{2}")
    @CsvSource(textBlock = """
            GET,    /missing-messages/1,      URI not matched  
            POST,   /missing-messages,        Http Method not matched  
            """)
    public void should_return_empty_if_not_matched(String httpMethod, String uri, String context) {
        ResourceMethods resourceMethods = new ResourceMethods(MissingMessages.class.getMethods());
        UriTemplate.MatchResult result = new PathTemplate("/missing-messages").match(uri).get();

        String remaining = result.getRemaining() == null? "": result.getRemaining();

        assertTrue(resourceMethods.findResourceMethods(remaining, httpMethod).isEmpty());
    }

    @Test
    public void should_convert_get_resource_method_to_head_resource_method() {
        ResourceMethods resourceMethods = new ResourceMethods(Messages.class.getMethods());
        UriTemplate.MatchResult result = new PathTemplate("/messages").match("/messages/head").get();

        ResourceRouter.ResourceMethod method = resourceMethods.findResourceMethods(result.getRemaining(), "HEAD").get();

        assertInstanceOf(HeadResourceMethod.class, method);
    }

    @Test
    public void should_get_options_for_given_uri() {
        RuntimeDelegate delegate = Mockito.mock(RuntimeDelegate.class);
        RuntimeDelegate.setInstance(delegate);
        Mockito.when(delegate.createResponseBuilder()).thenReturn(new StubResponseBuilder());
        ResourceContext context = Mockito.mock(ResourceContext.class);
        UriInfoBuilder builder = Mockito.mock(UriInfoBuilder.class);

        ResourceMethods resourceMethods = new ResourceMethods(Messages.class.getMethods());
        UriTemplate.MatchResult result = new PathTemplate("/messages").match("/messages/head").get();

        ResourceRouter.ResourceMethod method = resourceMethods.findResourceMethods(result.getRemaining(), "OPTIONS").get();
        GenericEntity<?> entity = method.call(context, builder);
        Response response = (Response) entity.getEntity();

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        assertEquals(Set.of(HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS), response.getAllowedMethods());
    }

    @Test
    public void should_not_include_head_in_options_if_given_uri_not_have_get_method() {
        RuntimeDelegate delegate = Mockito.mock(RuntimeDelegate.class);
        RuntimeDelegate.setInstance(delegate);
        Mockito.when(delegate.createResponseBuilder()).thenReturn(new StubResponseBuilder());
        ResourceContext context = Mockito.mock(ResourceContext.class);
        UriInfoBuilder builder = Mockito.mock(UriInfoBuilder.class);

        ResourceMethods resourceMethods = new ResourceMethods(Messages.class.getMethods());
        UriTemplate.MatchResult result = new PathTemplate("/messages").match("/messages/no-head").get();

        ResourceRouter.ResourceMethod method = resourceMethods.findResourceMethods(result.getRemaining(), "OPTIONS").get();
        GenericEntity<?> entity = method.call(context, builder);
        Response response = (Response) entity.getEntity();

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        assertEquals(Set.of(HttpMethod.POST, HttpMethod.OPTIONS), response.getAllowedMethods());
    }

    @Path("/missing-messages")
    static class MissingMessages {
        @GET
        @Produces(MediaType.TEXT_PLAIN)
        public String get() {
            return "messages";
        }
    }

    @Path("/messages")
    static class Messages {

        @GET
        @Produces(MediaType.TEXT_PLAIN)
        public String get() {
            return "messages";
        }

        @Path("/head")
        @GET
        @Produces(MediaType.TEXT_PLAIN)
        public String getHead() {
            return "head";
        }

        @Path("/no-head")
        @POST
        @Produces(MediaType.TEXT_PLAIN)
        public void postNoHead() {
        }

        @GET
        @Path("/ah")
        @Produces(MediaType.TEXT_PLAIN)
        public String ah() {
            return "ah";
        }

        @GET
        @Path("/hello")
        @Produces(MediaType.TEXT_PLAIN)
        public String hello() {
            return "hello";
        }

        @POST
        @Path("/hello")
        @Produces(MediaType.TEXT_PLAIN)
        public String postHello() {
            return "hello";
        }

        @PUT
        @Path("/hello")
        @Produces(MediaType.TEXT_PLAIN)
        public String putHello() {
            return "hello";
        }

        @DELETE
        @Path("/hello")
        @Produces(MediaType.TEXT_PLAIN)
        public String deleteHello() {
            return "hello";
        }

        @PATCH
        @Path("/hello")
        @Produces(MediaType.TEXT_PLAIN)
        public String patchHello() {
            return "hello";
        }

        @HEAD
        @Path("/hello")
        @Produces(MediaType.TEXT_PLAIN)
        public String headHello() {
            return "hello";
        }

        @OPTIONS
        @Path("/hello")
        @Produces(MediaType.TEXT_PLAIN)
        public String optionsHello() {
            return "hello";
        }

        @GET
        @Path("/topics/{id}")
        @Produces(MediaType.TEXT_PLAIN)
        public String topicId() {
            return "topicId";
        }

        @GET
        @Path("/topics/1234")
        @Produces(MediaType.TEXT_PLAIN)
        public String topic1234() {
            return "topic1234";
        }
    }
}
