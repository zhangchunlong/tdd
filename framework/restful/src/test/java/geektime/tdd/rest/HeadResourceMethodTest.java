package geektime.tdd.rest;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.container.ResourceContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.same;

public class HeadResourceMethodTest {

    @Test
    public void should_call_method_and_ignore_return_value() {
        ResourceRouter.ResourceMethod method = Mockito.mock(ResourceRouter.ResourceMethod.class);

        ResourceContext context = Mockito.mock(ResourceContext.class);
        UriInfoBuilder builder = Mockito.mock(UriInfoBuilder.class);

        HeadResourceMethod headResourceMethod = new HeadResourceMethod(method);

        assertNull(headResourceMethod.call(context, builder));
        Mockito.verify(method).call(same(context), same(builder));
    }

    @Test
    public void should_delegate_to_method_for_uri_template() {
        ResourceRouter.ResourceMethod method = Mockito.mock(ResourceRouter.ResourceMethod.class);

        HeadResourceMethod headResourceMethod = new HeadResourceMethod(method);
        UriTemplate uriTemplate = Mockito.mock(UriTemplate.class);
        Mockito.when(method.getUriTemplate()).thenReturn(uriTemplate);

        assertEquals(uriTemplate, headResourceMethod.getUriTemplate());
    }

    @Test
    public void should_delegate_to_method_for_http_method() {
        ResourceRouter.ResourceMethod method = Mockito.mock(ResourceRouter.ResourceMethod.class);

        HeadResourceMethod headResourceMethod = new HeadResourceMethod(method);
        Mockito.when(method.getHttpMethod()).thenReturn("GET");

        assertEquals(HttpMethod.HEAD, headResourceMethod.getHttpMethod());
    }
}
