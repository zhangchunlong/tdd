package geektime.tdd.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.container.ResourceContext;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.mockito.Mockito;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

public class DefaultResourceMethodTest {

    CallableResourceMethods resource;
    ResourceContext context;
    UriInfoBuilder builder;
    UriInfo uriInfo;
    MultivaluedHashMap<String, String> parameters;

    private LastCall lastCall;
    record LastCall(String name, List<Object> arguments){
    }

    @BeforeEach
    public void before() {
        lastCall = null;
        resource = (CallableResourceMethods) Proxy.newProxyInstance(this.getClass().getClassLoader(),
                new Class[]{
                        CallableResourceMethods.class}, (proxy, method, args) -> {
                    lastCall = new LastCall(getMethodName(method.getName(),
                            Arrays.stream(method.getParameters()).map(p -> p.getType()).toList()),
                                    args !=null?List.of(args):List.of());
                            return "getList".equals(method.getName())? new ArrayList<String>(): null;
                        });

        context = Mockito.mock(ResourceContext.class);
        builder = Mockito.mock(UriInfoBuilder.class);
        uriInfo = Mockito.mock(UriInfo.class);
        parameters = new MultivaluedHashMap<>();

        when(builder.getLastMatchedResource()).thenReturn(resource);
        when(builder.createUriInfo()).thenReturn(uriInfo);
        when(uriInfo.getPathParameters()).thenReturn(parameters);
        when(uriInfo.getQueryParameters()).thenReturn(parameters);
    }

    private String getMethodName(String name, List<? extends Class<?>> classes) {
        return name +
                "(" +
                classes.stream().map(Class::getSimpleName).collect(Collectors.joining(",")) +
                ")";
    }

    @Test
    public void should_call_resource_method() throws NoSuchMethodException {
        DefaultResourceMethod resourceMethod = getResourceMethod("get");
        resourceMethod.call(context, builder);
        assertEquals("get()", lastCall.name);
    }

    @Test
    public void should_call_resource_method_with_void_return_type() throws NoSuchMethodException {

        DefaultResourceMethod resourceMethod = getResourceMethod("post");

        assertNull(resourceMethod.call(context, builder));
    }

    @Test
    public void should_use_resource_method_generic_return_type() throws NoSuchMethodException {
        DefaultResourceMethod resourceMethod = getResourceMethod("getList");

        assertEquals(new GenericEntity<>(List.of(), CallableResourceMethods.class.getMethod("getList").getGenericReturnType()),
                resourceMethod.call(context, builder));
    }

    private DefaultResourceMethod getResourceMethod(String methodName, Class... types) throws NoSuchMethodException {
        return new DefaultResourceMethod(CallableResourceMethods.class.getMethod(methodName, types));
    }

    private void verifyResourceMethodCalled(String method, Class<?> type, String paramString, Object paramValue) throws NoSuchMethodException {
        DefaultResourceMethod resourceMethod = getResourceMethod(method, type);

        parameters.put("param", List.of(paramString));
        resourceMethod.call(context, builder);

        assertEquals(getMethodName(method, List.of(type)), lastCall.name);
        assertEquals(List.of(paramValue), lastCall.arguments);
    }

    record InjectableTypeTestCase(Class<?> type, String string, Object value){
    }

    @TestFactory
    public List<DynamicTest> injectableTypes() {
        List<DynamicTest> tests = new ArrayList<>();
        List<InjectableTypeTestCase> typeCases = List.of(
                new InjectableTypeTestCase(String.class, "string", "string"),
                new InjectableTypeTestCase(int.class, "1", 1),
                new InjectableTypeTestCase(double.class, "3.25", 3.25)
        );

        List<String> paramTypes = List.of("getPathParam", "getQueryParam");
        for(String type: paramTypes)
            for(InjectableTypeTestCase testCase: typeCases) {
                tests.add(DynamicTest.dynamicTest("should inject " + testCase.type.getSimpleName()
                + " to " + type, () -> {
                    verifyResourceMethodCalled(type, testCase.type, testCase.string, testCase.value);
                }));
            }

        return tests;
    }

    //TODO using default converters for path,query, matrix(uri) form, header, cookie(request)
    //TODO default converters for int, short, float, double, byte, char, String and boolean
    //TODO default converters for class with converter constructor
    //TODO default converters for class with converter factory
    //TODO default converters for List, Set, SortSet
    //TODO injection - get injectable from resource context
    //TODO injection - can inject resource context itself
    //TODO injection - can inject uri info built from uri info builder

    interface CallableResourceMethods {
        @GET
        String get();

        @POST
        void post();

        @GET
        Object getList();

        @GET
        String getPathParam(@PathParam("param") String value);

        @GET
        String getPathParam(@PathParam("param") int value);

        @GET
        String getPathParam(@PathParam("param") double value);

        @GET
        String getQueryParam(@QueryParam("param") String value);

        @GET
        String getQueryParam(@QueryParam("param") int value);

        @GET
        String getQueryParam(@QueryParam("param") double value);
    }
}
