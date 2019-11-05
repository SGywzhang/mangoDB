package importtask.cs4224c.task;

import com.google.common.collect.Lists;
import importtask.cs4224c.util.Constant;
import importtask.cs4224c.util.ProjectConfig;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class ImportDataTask implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(ImportDataTask.class);

    public static void main(String[] args) {
        Runnable task = new ImportDataTask();
        task.run();
    }

    @Override
    public void run() {
        logger.info("ImportDataTask begins.");
        ProjectConfig config = ProjectConfig.getInstance();

        String importInstructions;
        try {
            importInstructions = IOUtils.toString(this.getClass().getResourceAsStream("importData.data"));
        } catch (IOException e) {
            logger.error("Cannot get template importData");
            throw new RuntimeException();
        }
        logger.info("importData instructions {}", importInstructions);

        String importInstructionsResolved = importInstructions.replace("_DATA_JSON_FOLDER_",
                Paths.get(config.getProjectRoot(), config.getDataJsonFolder()).toString() + File.separator);
        importInstructionsResolved = importInstructionsResolved.replaceAll("\n", ""); // without line breaker

        for (String instruction : importInstructionsResolved.split(Constant.STATEMENT_DECIMETER)) {
            logger.info("Import instruction:{}", instruction);

            String[] instructionArray = instruction.split(Constant.COLLECTION_SEPARATOR);

            String collectionName = instructionArray[0];
            String dataPath = instructionArray[1];

            String[] cmd = new String[] {
                    getMongoImportFile(),
                    "--host",
                    config.getMongodbIp(),
                    "--db",
                    config.getMongodbDb(),
                    "--collection",
                    collectionName,
                    "--file",
                    dataPath,
                    "--jsonArray"
            };
            logger.info("Command will be executed {}", Lists.newArrayList(cmd));

            Process process = null;
            try {
                ProcessBuilder processBuilder = new ProcessBuilder()
                        .inheritIO()
                        .command(cmd);
                process = processBuilder.start();

                process.waitFor();
                String error = IOUtils.toString(process.getErrorStream());

                if (StringUtils.isNotEmpty(error)) {
                    logger.warn("Output from mongoimport (can ignore if it doesn't cause problems)\n {}", error);
                }
            } catch (Exception e) {
                logger.error("Cannot execute command and get result {}", e);
                throw new RuntimeException();
            }
        }

        logger.info("ImportDataTask ends");
    }

    private String getMongoImportFile() {
        String OS = System.getProperty("os.name");
        if (OS == null) {
            logger.error("Unsupported OS");
            throw new RuntimeException();
        }
        if (OS.startsWith("Mac")) {
            logger.info("Current OS is {}, use mongoimportmac", OS);
            return Paths.get(System.getProperty("user.dir"), "mongoshell", "mongoimportmac").toString();
        }
        if (OS.startsWith("Linux")) {
            logger.info("Current OS is {}, use mongoimportlinux", OS);
            return Paths.get(System.getProperty("user.dir"), "mongoshell", "mongoimportlinux").toString();
        }
        if (OS.startsWith("Windows")) {
            logger.info("Current OS is {}, use mongoimport.exe", OS);
            return Paths.get(System.getProperty("user.dir"), "mongoshell", "mongoimport.exe").toString();
        }
        logger.error("Unsupported OS {}", OS);
        throw new RuntimeException();
    }
}
