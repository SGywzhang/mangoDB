package massage.cs4224c.converter;

import org.junit.Test;

public class CustomerCollectionTest extends AbstractConverterTest {

    public CustomerCollectionTest() {
        super(new CustomerCollection(), "customer.json");
        //this.setGodMod(true);
    }

    @Test
    public void testCustomerCollection() throws Exception {
        this.testConverter();
    }
}
