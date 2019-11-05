package importtask.cs4224c.task;

import com.mongodb.client.model.Indexes;
import importtask.cs4224c.util.Collection;
import importtask.cs4224c.util.CollectionPool;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateIndexTask implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(CreateIndexTask.class);

    public static void main(String[] args) {
        Runnable task = new CreateIndexTask();
        task.run();
    }


    @Override
    public void run() {
        logger.info("Create index on customer balance.");
        CollectionPool.getInstance().getCollection(Collection.Customer).createIndex(Indexes.descending("c_balance"));
    }
}
