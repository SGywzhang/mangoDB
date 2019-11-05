package massage.cs4224c.converter;

import org.junit.Test;

public class DistrictCollectionTest extends AbstractConverterTest {

    public DistrictCollectionTest() {
        super(new DistrictCollection(), "district.json");
        //this.setGodMod(true);
    }

    @Test
    public void testDistrictCollection() throws Exception {
        this.testConverter();
    }
}
