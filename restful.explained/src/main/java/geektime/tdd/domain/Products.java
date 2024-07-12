package geektime.tdd.domain;

import java.util.List;

public interface Products {
    List<Product> find(List<Product.Id> toList);
}
