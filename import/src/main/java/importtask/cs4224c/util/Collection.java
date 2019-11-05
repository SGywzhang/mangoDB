package importtask.cs4224c.util;

public enum Collection {

    Customer("customer"),
    District("district"),
    OrderItem("orderItem"),
    Stock("stock");

    private String collectionNameInDb;

    Collection(String name) {
        this.collectionNameInDb = name;
    }

    public String getCollectionNameInDb() {
        return collectionNameInDb;
    }
}
