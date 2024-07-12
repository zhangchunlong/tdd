package geektime.tdd.domain;

public class Product {
    public static record Id(long value){
    }

    private Product.Id id;

    public Product(Id id) {
        this.id = id;
    }

    public Id getId() {
        return id;
    }
}
