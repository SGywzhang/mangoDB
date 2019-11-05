package nodesetup.cs4224c;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import nodesetup.cs4224c.util.ProjectConfig;
import nodesetup.cs4224c.util.SshUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeSetup implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(NodeSetup.class);

    private final static Map<String, List<String>> replicaSetToIps = new HashMap<>();

    private final static int[] indexToChoose = new int[] {0, 2, 4};

    static {
        // annoyed log from apache commons
        System.setProperty("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.NoOpLog");

        List<String> servers = ProjectConfig.getInstance().getServerIps();
        replicaSetToIps.put("1", Lists.newArrayList(servers.get(0), servers.get(1), servers.get(2)));
        replicaSetToIps.put("2", Lists.newArrayList(servers.get(1), servers.get(2), servers.get(3)));
        replicaSetToIps.put("3", Lists.newArrayList(servers.get(2), servers.get(3), servers.get(4)));
        replicaSetToIps.put("4", Lists.newArrayList(servers.get(0), servers.get(3), servers.get(4)));
        replicaSetToIps.put("5", Lists.newArrayList(servers.get(0), servers.get(1), servers.get(4)));
    }

    public static void main(String args[]) {
        Runnable runnable = new NodeSetup();
        runnable.run();
    }

    private String executeOnIp(String ip, String command) {
        logger.info("Execute {} on server {}", command, ip);
        ProjectConfig projectConfig = ProjectConfig.getInstance();
        String remoteShellOutput = SshUtils.shell(String.format("ssh://%s:%s@%s", projectConfig.getSshUser(), projectConfig.getSshPassword(), ip), command);
        logger.info("Output from SSH {}", remoteShellOutput);
        return remoteShellOutput;
    }

    private void upload(String ip, InputStream is, String path, String fileName) {
        logger.info("Upload {} to server {}", fileName, ip);
        ProjectConfig projectConfig = ProjectConfig.getInstance();
        SshUtils.upload(is, String.format("ssh://%s:%s@%s%s", projectConfig.getSshUser(), projectConfig.getSshPassword(), ip, path), fileName);
    }

    @Override
    public void run() {
        logger.info("NodeSetup is running!");

        validateConfig();
        bringDownMongoInstance();
        createFolder();
        downloadMongoDB();

        sendMongoDBConfig();
        startMongodInstance();
        initDataReplicationSet();

        sendConfigSvrConfig();
        startConfigSvrInstance();
        initConfigReplicationSet();

        sendMongosConfig();
        startMongos();
        initMongosSharding();

        initSharding();

        logger.info("NodeSetup complete! You may start to import now. DB are available through port 27017 in the servers");
    }

    private void validateConfig() {
        logger.info("[validateConfig] stage 0% complete");
        ProjectConfig projectConfig = ProjectConfig.getInstance();

        // validate the size
        logger.info("Validate the number of nodes.");
        if (projectConfig.getServerIps().size() != 5) {
            logger.error("We need five nodes to set-up the mongoDB cluster, currently only ({}): {}", projectConfig.getServerIps().size(), projectConfig.getServerIps());
            System.exit(1);
        }
        logger.info("Validate the number of nodes. -- OK");

        // validate can connect or not
        logger.info("Validate the availability of nodes.");
        for (String ip : projectConfig.getServerIps()) {
            executeOnIp(ip, "uname -a");
        }
        logger.info("Validate the availability of nodes. -- OK");
        logger.info("[validateConfig] stage 8% complete");
    }

    public void bringDownMongoInstance() {
        logger.info("[bringDownMongoInstance] stage 8% complete");
        ProjectConfig projectConfig = ProjectConfig.getInstance();

        for (String ip : projectConfig.getServerIps()) {
            logger.info("Bring down running mongod/mongos instance on {}", ip);
            executeOnIp(ip, "(pkill 'mongod' && pkill 'mongos') || true");
        }

        logger.info("[bringDownMongoInstance] stage 10% complete");
    }

    private void createFolder() {
        logger.info("[createFolder] stage 10% complete");
        ProjectConfig projectConfig = ProjectConfig.getInstance();

        if (projectConfig.getBaseFolderOverwrite()) {
            logger.warn("Detect overwriting options, will remove all content in {}", projectConfig.getBaseFolder());
            logger.warn("You have 10 second to confirm the operations", projectConfig.getBaseFolder());
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                logger.error("Encounter interrupt exception {}", e);
                System.exit(1);
            }
        }

        for (String ip : projectConfig.getServerIps()) {
            logger.info("Creating data folder {}", ip);
            if (projectConfig.getBaseFolderOverwrite()) {
                executeOnIp(ip, String.format("rm -r -f %s/* || true", projectConfig.getBaseFolder()));
            }
            String mkdirCommand = String.format("mkdir -p " +
                    "%1$s/main/instance1 " +
                    "%1$s/main/instance2 " +
                    "%1$s/main/instance3 " +
                    "%1$s/main/confinstance " +
                    "%1$s/main/mongos " +
                    "%1$s/data/data1 " +
                    "%1$s/data/data2 " +
                    "%1$s/data/data3 " +
                    "%1$s/data/confdata " +
                    "%1$s/log/mongos " +
                    "%1$s/log/log1 " +
                    "%1$s/log/log2 " +
                    "%1$s/log/log3 " +
                    "%1$s/log/conf ", projectConfig.getBaseFolder());
            executeOnIp(ip, mkdirCommand);
            logger.info("Creating data folder {} -- OK", ip);
        }
        logger.info("[createFolder] stage 16% complete");
    }

    private void downloadMongoDB() {
        logger.info("[downloadMongoDB] stage 16% complete");
        List<String> servers = ProjectConfig.getInstance().getServerIps();

        String downloadUrl = "https://fastdl.mongodb.org/linux/mongodb-linux-x86_64-rhel70-3.4.10.tgz";

        for (String ip : servers) {
            logger.info("Prepare to download mongodb to {} from {}", ip, downloadUrl);
            executeOnIp(ip, String.format("wget %s -O %s/mongodb.tgz", downloadUrl, ProjectConfig.getInstance().getBaseFolder()));
            logger.info("Finish downloading files");
            logger.info("Prepare to unzip all");
            executeOnIp(ip, String.format("tar -xvzf %1$s/mongodb.tgz -C %1$s/main/instance1/", ProjectConfig.getInstance().getBaseFolder()));
            executeOnIp(ip, String.format("tar -xvzf %1$s/mongodb.tgz -C %1$s/main/instance2/", ProjectConfig.getInstance().getBaseFolder()));
            executeOnIp(ip, String.format("tar -xvzf %1$s/mongodb.tgz -C %1$s/main/instance3/", ProjectConfig.getInstance().getBaseFolder()));
            executeOnIp(ip, String.format("tar -xvzf %1$s/mongodb.tgz -C %1$s/main/confinstance/", ProjectConfig.getInstance().getBaseFolder()));
            executeOnIp(ip, String.format("tar -xvzf %1$s/mongodb.tgz -C %1$s/main/mongos/", ProjectConfig.getInstance().getBaseFolder()));
            logger.info("Prepare to unzip all -- Completed");
        }

        logger.info("[downloadMongoDB] stage 25% complete");
    }

    private void sendMongoDBConfig() {
        logger.info("[sendMongoDBConfig] stage 25% complete");
        Map<String, List<String>> ipToConfigMap = new HashMap<>();
        List<String> servers = ProjectConfig.getInstance().getServerIps();
        ipToConfigMap.put(servers.get(0), Lists.newArrayList("27031", "27034", "27035"));
        ipToConfigMap.put(servers.get(1), Lists.newArrayList("27031", "27032", "27035"));
        ipToConfigMap.put(servers.get(2), Lists.newArrayList("27031", "27032", "27033"));
        ipToConfigMap.put(servers.get(3), Lists.newArrayList("27032", "27033", "27034"));
        ipToConfigMap.put(servers.get(4), Lists.newArrayList("27033", "27034", "27035"));

        String rawConfig = "";
        try {
            rawConfig = IOUtils.toString(this.getClass().getResourceAsStream("datareplication/mongod.yaml"));
        } catch (IOException e) {
            logger.error("Cannot get template config from datareplication/mongod.yaml");
            System.exit(1);
        }

        logger.info("Upload config file to servers");
        for (String ip : ipToConfigMap.keySet()) {
            List<String> ports = ipToConfigMap.get(ip);
            logger.info("Upload config file to servers {}", ip);
            for (int i = 1; i <= 3; i++) {
                String port = ports.get(i - 1);
                Preconditions.checkState(port.length() == 5,
                        "We assume the port is five digit and use the last digit to identify sharding replica set");
                String sharding = port.substring(port.length() - 1, port.length());
                String config = MessageFormat.format(rawConfig, i, ip, port, sharding, ProjectConfig.getInstance().getEnableMajorityReadConcern());
                upload(ip , new ByteArrayInputStream(config.getBytes()), String.format("%s/main/instance%s/", ProjectConfig.getInstance().getBaseFolder(), i), "mongodb.yaml");
            }
            logger.info("Upload config file to servers {} -- OK", ip);
        }
        logger.info("Upload config file to servers -- OK");
        logger.info("[sendMongoDBConfig] stage 35% complete");
    }

    private void startMongodInstance() {
        logger.info("[startMongodInstance] stage 35% complete");
        List<String> servers = ProjectConfig.getInstance().getServerIps();

        for (String ip : servers) {
            executeOnIp(ip, String.format("%1$s/main/instance1/mongodb-linux-x86_64-rhel70-3.4.10/bin/mongod --config %1$s/main/instance1/mongodb.yaml", ProjectConfig.getInstance().getBaseFolder()));
            executeOnIp(ip, String.format("%1$s/main/instance2/mongodb-linux-x86_64-rhel70-3.4.10/bin/mongod --config %1$s/main/instance2/mongodb.yaml", ProjectConfig.getInstance().getBaseFolder()));
            executeOnIp(ip, String.format("%1$s/main/instance3/mongodb-linux-x86_64-rhel70-3.4.10/bin/mongod --config %1$s/main/instance3/mongodb.yaml", ProjectConfig.getInstance().getBaseFolder()));
        }

        logger.info("[startMongodInstance] stage 47% complete");
    }

    private void initDataReplicationSet() {
        logger.info("[initDataReplicationSet] stage 47% complete");
        List<String> servers = ProjectConfig.getInstance().getServerIps();

        String rawInitScript = "";
        try {
            rawInitScript = IOUtils.toString(this.getClass().getResourceAsStream("datareplication/initreplicaset.data"));
        } catch (IOException e) {
            logger.error("Cannot get template rawInitScript from datareplication/initreplicaset.data");
            System.exit(1);
        }

        for (String replica : replicaSetToIps.keySet()) {
            List<String> ips = replicaSetToIps.get(replica);
            String script = MessageFormat.format(rawInitScript, replica, ips.get(0), ips.get(1), ips.get(2));
            script = script.replaceAll("\n", "");
            executeOnIp(ips.get(0),
                    String.format("%s/main/mongos/mongodb-linux-x86_64-rhel70-3.4.10/bin/mongo --host %s --port 2703%s --eval '%s'",
                            ProjectConfig.getInstance().getBaseFolder(), ips.get(0), replica, script));
        }
        logger.info("[initDataReplicationSet] stage 55% complete");
    }

    private void sendConfigSvrConfig() {
        logger.info("[sendConfigSvrConfig] stage 55% complete");
        List<String> servers = ProjectConfig.getInstance().getServerIps();
        String rawConfig = "";
        try {
            rawConfig = IOUtils.toString(this.getClass().getResourceAsStream("confreplication/mongod.yaml"));
        } catch (IOException e) {
            logger.error("Cannot get template config from confreplication/mongod.yaml");
            System.exit(1);
        }
        for (int index : indexToChoose) {
            logger.info("Send conf svr configuration to {}", servers.get(index));
            String config = MessageFormat.format(rawConfig, servers.get(index));
            upload(servers.get(index) , new ByteArrayInputStream(config.getBytes()), String.format("%s/main/confinstance/", ProjectConfig.getInstance().getBaseFolder()), "mongodb.yaml");
        }
        logger.info("[sendConfigSvrConfig] stage 65% complete");
    }

    private void startConfigSvrInstance() {
        logger.info("[startConfigSvrInstance] stage 65% complete");
        List<String> servers = ProjectConfig.getInstance().getServerIps();

        for (int index : indexToChoose) {
            executeOnIp(servers.get(index), String.format("%1$s/main/confinstance/mongodb-linux-x86_64-rhel70-3.4.10/bin/mongod --config %1$s/main/confinstance/mongodb.yaml",
                    ProjectConfig.getInstance().getBaseFolder()));
        }
        logger.info("[startConfigSvrInstance] stage 75% complete");
    }

    private void initConfigReplicationSet() {
        logger.info("[initConfigReplicationSet] stage 75% complete");
        List<String> servers = ProjectConfig.getInstance().getServerIps();
        String ip = servers.get(indexToChoose[0]); // any index works

        String rawInitScript = "";
        try {
            rawInitScript = IOUtils.toString(this.getClass().getResourceAsStream("confreplication/initreplicaset.data"));
        } catch (IOException e) {
            logger.error("Cannot get template rawInitScript from confreplication/initreplicaset.data");
            System.exit(1);
        }
        String script = MessageFormat.format(rawInitScript, servers.get(indexToChoose[0]), servers.get(indexToChoose[1]), servers.get(indexToChoose[2]));

        executeOnIp(ip,
                String.format("%s/main/mongos/mongodb-linux-x86_64-rhel70-3.4.10/bin/mongo --host %s --port 27001 --eval '%s'",
                        ProjectConfig.getInstance().getBaseFolder(), ip, script));
        logger.info("[initConfigReplicationSet] stage 85% complete");
    }

    private void sendMongosConfig() {
        logger.info("[sendMongosConfig] stage 85% complete");
        List<String> servers = ProjectConfig.getInstance().getServerIps();

        String rawConfig = "";
        try {
            rawConfig = IOUtils.toString(this.getClass().getResourceAsStream("mongos/mongos.yaml"));
        } catch (IOException e) {
            logger.error("Cannot get template config from mongos/mongod.yaml");
            System.exit(1);
        }

        for (String server : servers) {
            logger.info("Send mongos configuration to {}", server);
            String config = MessageFormat.format(rawConfig, server, servers.get(indexToChoose[0]), servers.get(indexToChoose[1]), servers.get(indexToChoose[2]));
            upload(server, new ByteArrayInputStream(config.getBytes()), String.format("%s/main/mongos/", ProjectConfig.getInstance().getBaseFolder()), "mongos.yaml");
        }
        logger.info("[sendMongosConfig] stage 90% complete");
    }

    private void startMongos() {
        logger.info("[startMongos] stage 90% complete");
        List<String> servers = ProjectConfig.getInstance().getServerIps();

        for (String server : servers) {
            executeOnIp(server, String.format("%1$s/main/mongos/mongodb-linux-x86_64-rhel70-3.4.10/bin/mongos --config %1$s/main/mongos/mongos.yaml",
                    ProjectConfig.getInstance().getBaseFolder()));
        }
        logger.info("[startMongos] stage 95% complete");
    }

    private void initMongosSharding() {
        logger.info("[initMongosSharding] stage 95% complete");

        String rawInitScript = "";
        try {
            rawInitScript = IOUtils.toString(this.getClass().getResourceAsStream("mongos/initsharding.data"));
        } catch (IOException e) {
            logger.error("Cannot get template init script from mongos/initsharding.data");
            System.exit(1);
        }
        String ip = ProjectConfig.getInstance().getServerIps().get(0); // any server works

        for (String replica : replicaSetToIps.keySet()) {
            List<String> ips = replicaSetToIps.get(replica);
            String script = MessageFormat.format(rawInitScript, replica, ips.get(0), ips.get(1), ips.get(2));
            executeOnIp(ip,
                    String.format("%s/main/mongos/mongodb-linux-x86_64-rhel70-3.4.10/bin/mongo --host %s --port 27017 --eval '%s'",
                            ProjectConfig.getInstance().getBaseFolder(), ip, script));
        }
        logger.info("[initMongosSharding] stage 98% complete");
    }

    private void initSharding() {
        logger.info("[initSharding] stage 98% complete");
        String ip = ProjectConfig.getInstance().getServerIps().get(0); // any server works

        String rawInitDatabaseScript = "";
        String rawInitCollectionScript = "";
        try {
            rawInitDatabaseScript = IOUtils.toString(this.getClass().getResourceAsStream("mongos/initdatabase.data"));
            rawInitCollectionScript = IOUtils.toString(this.getClass().getResourceAsStream("mongos/initcollection.data"));
        } catch (IOException e) {
            logger.error("Cannot get template init script from mongos/initdatabase.data or mongos/initdatabase.data", e);
            System.exit(1);
        }

        String databaseForSharding = ProjectConfig.getInstance().getShardDb();

        String initDatabaseScript = MessageFormat.format(rawInitDatabaseScript, databaseForSharding);
        executeOnIp(ip,
                String.format("%s/main/mongos/mongodb-linux-x86_64-rhel70-3.4.10/bin/mongo --host %s --port 27017 --eval '%s'",
                        ProjectConfig.getInstance().getBaseFolder(), ip, initDatabaseScript));

        List<String> collectionForSharding = ProjectConfig.getInstance().getShardCollections();
        for (String collection : collectionForSharding) {
            String initCollectionScript = MessageFormat.format(rawInitCollectionScript, databaseForSharding, collection);
            executeOnIp(ip,
                    String.format("%s/main/mongos/mongodb-linux-x86_64-rhel70-3.4.10/bin/mongo --host %s --port 27017 --eval '%s'",
                            ProjectConfig.getInstance().getBaseFolder(), ip, initCollectionScript));
        }
        logger.info("[initSharding] stage 100% complete");
    }
}
