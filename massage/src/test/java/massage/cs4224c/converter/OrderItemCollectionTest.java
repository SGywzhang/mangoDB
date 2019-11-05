package massage.cs4224c.converter;

import org.junit.Test;

public class OrderItemCollectionTest extends AbstractConverterTest {

    public OrderItemCollectionTest() {
        super(new OrderItemCollection(), "orderItem.json");
        //this.setGodMod(true);
    }

    @Test
    public void testOrderItemCollection() throws Exception {
        this.testConverter();
    }
}
