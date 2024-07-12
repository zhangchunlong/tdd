package geektime.tdd.domain;

import java.util.Optional;

public interface Users {
    Optional<User> findById(User.Id id);

    User create(String name, String email);
}
