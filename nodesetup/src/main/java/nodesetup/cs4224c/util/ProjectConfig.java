package nodesetup.cs4224c.util;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.List;

public class ProjectConfig {

    private final static Logger logger = LoggerFactory.getLogger(ProjectConfig.class);

    private static String SERVER_IPS = "server.ips";
    private static String SSH_USER = "ssh.user";
    private static String SSH_PASSWORD = "ssh.password";
    private static String BASE_FOLDER_OVERWRITE = "base.folder.overwrite";
    private static String BASE_FOLDER = "base.folder";
    private static String SHARD_DB = "shard.db";
    private static String SHARD_COLLECTIONS = "shard.collections";
    private static String ENABLE_MAJORITY_READ_CONCERN = "mongodb.enable.majority.read.concern";

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
        try {
            Parameters params = new Parameters();
            FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                    new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                            .configure(params.properties()
                                    .setFileName("project.properties")
                                    .setListDelimiterHandler(new DefaultListDelimiterHandler(',')));
            configuration = builder.getConfiguration();
        } catch (ConfigurationException e) {
            logger.error("Load Main Configuration Initialization Fail! {}", e);
            throw new RuntimeException();
        }

        return this;
    }

    public String getProjectRoot() {
        if (System.getProperty("user.dir") != null) {
            return Paths.get(System.getProperty("user.dir"), "..").toString();
        }
        throw new RuntimeException("Cannot find user.dir");
    }

    public List<String> getServerIps() {
        return configuration.getList(String.class, SERVER_IPS);
    }

    public String getSshUser() {
        return configuration.getString(SSH_USER);
    }

    public String getSshPassword() {
        return configuration.getString(SSH_PASSWORD);
    }

    public boolean getBaseFolderOverwrite() {
        return configuration.getBoolean(BASE_FOLDER_OVERWRITE);
    }

    public String getBaseFolder() {
        return configuration.getString(BASE_FOLDER);
    }

    public String getShardDb() {
        return configuration.getString(SHARD_DB);
    }

    public List<String> getShardCollections() {
        return configuration.getList(String.class, SHARD_COLLECTIONS);
    }

    public String getEnableMajorityReadConcern() {
        return configuration.getString(ENABLE_MAJORITY_READ_CONCERN);
    }
}
