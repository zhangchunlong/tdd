package geektime.tdd.api.providers;

import geektime.tdd.domain.User;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class UserIdParamConverterProvider implements ParamConverterProvider {
    @Override
    public <T> ParamConverter<T> getConverter(Class<T> aClass, Type type, Annotation[] annotations) {
        if(aClass != User.Id.class) return null;
        return new ParamConverter<T>() {
            @Override
            public T fromString(String s) {
                return (T) new User.Id(Long.parseLong(s));
            }

            @Override
            public String toString(T t) {
                return String.valueOf(((User.Id)t).value());
            }
        };
    }
}
