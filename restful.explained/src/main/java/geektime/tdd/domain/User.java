package geektime.tdd.domain;

public class User {
    public static record Id(long value){
    }
    protected Id id;
    protected String email;
    protected String name;

    public Id getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
