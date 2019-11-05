package client.cs4224c.util;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CollectionPool {

    private final Logger logger = LoggerFactory.getLogger(CollectionPool.class);

    private static CollectionPool instance;

    private MongoDatabase database;

    private Map<Collection, MongoCollection<Document>> collectionMap = new HashMap<>();

    public synchronized static CollectionPool getInstance() {
        if (instance == null) {
            instance = new CollectionPool();
        }
        return instance;
    }

    private CollectionPool() {
        try {
            MongoClientURI connectionString = new MongoClientURI(
                    String.format("mongodb://%s/?readPreference=%s&readConcernLevel=%s&w=%s&journal=%s",
                            ProjectConfig.getInstance().getMongodbIp(),
                            ProjectConfig.getInstance().getMongodbReadPref(),
                            ProjectConfig.getInstance().getMongodbReadConcern(),
                            ProjectConfig.getInstance().getMongodbWriteConcern(),
                            ProjectConfig.getInstance().getMongodbWriteConcernJournal())
            );
            MongoClient mongoClient = new MongoClient(connectionString);
            database = mongoClient.getDatabase(ProjectConfig.getInstance().getMongodbDb());
        } catch (Exception e) {
            logger.error("Initialize drive ends with exception {}", e);
            throw new RuntimeException();
        }
    }

    public synchronized MongoCollection<Document> getCollection(Collection collection) {
        if (!collectionMap.containsKey(collection)) {
            collectionMap.put(collection, database.getCollection(collection.getCollectionNameInDb()));
        }
        return collectionMap.get(collection);
    }

    public void close() {
        instance.close();
    }

}
