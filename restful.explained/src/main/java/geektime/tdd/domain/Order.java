package geektime.tdd.domain;

import java.util.List;

public class Order {
    public static class Item {
        private Product.Id product;
        private double quantity;
        private double price;

        public Item(Product.Id product, double quantity, double price) {
            this.product = product;
            this.quantity = quantity;
            this.price = price;
        }

        public Product.Id getProduct() {
            return product;
        }

        public double getQuantity() {
            return quantity;
        }

        public double getPrice() {
            return price;
        }
    }
    public static record Id(long value){
    }
    private Id id;
    private List<Item> items;

    public Order(Id id, List<Item> items) {
        this.id = id;
        this.items = items;
    }

    public Id getId() {
        return id;
    }

    public List<Item> getItems() {
        return items;
    }
}
