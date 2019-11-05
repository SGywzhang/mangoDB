package importtask.cs4224c.task;

import importtask.cs4224c.util.Collection;
import importtask.cs4224c.util.CollectionPool;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClearCollectionTask implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(ClearCollectionTask.class);

    public static void main(String[] args) {
        Runnable task = new ClearCollectionTask();
        task.run();
    }


    @Override
    public void run() {
        CollectionPool.getInstance().getCollection(Collection.Customer).deleteMany(new Document());
        CollectionPool.getInstance().getCollection(Collection.District).deleteMany(new Document());
        CollectionPool.getInstance().getCollection(Collection.OrderItem).deleteMany(new Document());
        CollectionPool.getInstance().getCollection(Collection.Stock).deleteMany(new Document());
    }
}
