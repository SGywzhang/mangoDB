package importtask.cs4224c;

import com.fasterxml.jackson.databind.ObjectMapper;
import importtask.cs4224c.task.ClearCollectionTask;
import importtask.cs4224c.task.ImportDataTask;
import importtask.cs4224c.util.Collection;
import importtask.cs4224c.util.CollectionPool;
import importtask.cs4224c.util.Constant;
import importtask.cs4224c.util.ProjectConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Map;

public class ImportTest {

    private final Logger logger = LoggerFactory.getLogger(ImportTest.class);

    private ProjectConfig config;

    @Before
    public void before() {
        System.setProperty(Constant.PROPERTY_KEY_ENV, Constant.ENV_TEST);
        config = ProjectConfig.getInstance().reload();
    }

    @Test
    public void testImportData() throws Exception {
        logger.info("Begin to import data.");

        logger.info("Run drop collection task");
        new ClearCollectionTask().run();

        logger.info("Run Import Data Task");
        new ImportDataTask().run();

        validateDatabase();
    }

    private void validateDatabase() throws Exception {
        logger.info("Verifying collection size in DB");

        validateCollection("customer.json", Collection.Customer);
        validateCollection("district.json", Collection.District);
        validateCollection("stock.json", Collection.Stock);
        validateCollection("orderItem.json", Collection.OrderItem);
    }

    public void validateCollection(String collectionFile, Collection collection) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map[] collections = objectMapper.readValue(Paths.get(config.getProjectRoot(), config.getDataJsonFolder(), collectionFile).toFile(), Map[].class);
        // only validate number of records
        Assert.assertEquals(collections.length, CollectionPool.getInstance().getCollection(collection).count());
    }

}
