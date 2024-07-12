package geektime.tdd.domain;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Orders {
    List<Order> findBy(User.Id user);
    Optional<Order> findBy(User.Id user, Order.Id order);

    Order create(User user, Map<Product, Double> orderItems);
}
