package massage.cs4224c.converter;

import org.junit.Test;

public class StockCollectionTest extends AbstractConverterTest {

    public StockCollectionTest() {
        super(new StockCollection(), "stock.json");
        //this.setGodMod(true);
    }

    @Test
    public void testStockCollection() throws Exception {
        this.testConverter();
    }
}
