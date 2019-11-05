package importtask.cs4224c.util;

import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

public class ProjectConfig {

    private final static Logger logger = LoggerFactory.getLogger(ProjectConfig.class);

    private static final String MONGODB_IP = "mongodb.ip";
    private static final String MONGODB_DB = "mongodb.db";
    private static final String DATA_JSON_FOLDER = "data.json.folder";

    private static ProjectConfig instance;

    public static ProjectConfig getInstance() {
        if (instance == null) {
            instance = new ProjectConfig();
        }
        return instance;
    }

    private Configuration configuration;

    private ProjectConfig() {
        reload();
    }

    public ProjectConfig reload() {
        CompositeConfiguration compositeConfiguration = new CompositeConfiguration();

        if (Constant.ENV_TEST.equals(System.getProperty(Constant.PROPERTY_KEY_ENV))) {
            logger.info("Minions find we are in TEST environment, try to load test configuration which will override the main one");
            try {
                compositeConfiguration.addConfiguration(new Configurations().properties(Paths.get(System.getProperty("user.dir"), "project.test.properties").toFile()));
            } catch (ConfigurationException e) {
                logger.error("Load Teset Configuration Initialization Fail! {}", e);
                throw new RuntimeException();
            }
        }

        try {
            compositeConfiguration.addConfiguration(new Configurations().properties(Paths.get(System.getProperty("user.dir"), "project.properties").toFile()));
        } catch (ConfigurationException e) {
            logger.error("Load Main Configuration Initialization Fail! {}", e);
            throw new RuntimeException();
        }

        configuration = compositeConfiguration;
        return this;
    }

    public String getProjectRoot() {
        if (System.getProperty("user.dir") != null) {
            return Paths.get(System.getProperty("user.dir"), "..").toString();
        }
        throw new RuntimeException("Cannot find user.dir");
    }

    public String getMongodbIp() { return configuration.getString(MONGODB_IP); }

    public String getMongodbDb() { return configuration.getString(MONGODB_DB); }

    public String getDataJsonFolder() {
        return configuration.getString(DATA_JSON_FOLDER);
    }

}
