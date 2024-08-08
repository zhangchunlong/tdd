package geektime.tdd.rest;

import jakarta.ws.rs.core.UriInfo;

import java.util.Map;

interface UriInfoBuilder {

    Object getLastMatchedResource();

    void addMatchedResource(Object resource);
    void addMatchedParameters(Map<String, String> pathParameters);

    UriInfo createUriInfo();
}
