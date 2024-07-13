package geektime.tdd.rest;

import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Response;

import java.lang.annotation.Annotation;

abstract class OutBoundResponse extends Response {
    abstract GenericEntity genericEntity();

    abstract Annotation[] getAnnotations();
}
