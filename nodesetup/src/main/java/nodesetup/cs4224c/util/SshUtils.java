package nodesetup.cs4224c.util;

import com.google.common.collect.ImmutableMap;
import com.jcraft.jsch.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.util.Map;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.Thread.sleep;
import static org.apache.commons.lang3.StringUtils.trim;

public final class SshUtils {

    private static final int RETRY_TIME = 3;
    private static final Logger LOG = LoggerFactory.getLogger(SshUtils.class);
    private static final String SSH = "ssh";
    private static final String FILE = "file";

    private SshUtils() {
    }

    public static void upload(InputStream fis, String connectUri, String fileName) {
        try {
            SessionHolder<ChannelSftp> session = new SessionHolder<>("sftp", URI.create(connectUri));
            LOG.info("Uploading {} --> {}", fileName, session.getMaskedUri());
            ChannelSftp channel = session.getChannel();
            channel.connect();
            channel.cd(URI.create(connectUri).getPath());
            channel.put(fis, fileName);
        } catch (Exception e) {
            throw new RuntimeException("Cannot upload file", e);
        }
    }

    /**
     * <pre>
     * <code>
     * shell("ssh://user:pass@host", System.in, System.out);
     * </code>
     * </pre>
     */
    public static void shell(String connectUri, InputStream is, OutputStream os) {
        try (SessionHolder<ChannelShell> session = new SessionHolder<>("shell", URI.create(connectUri))) {
            shell(session, is, os);
        }
    }

    /**
     * <pre>
     * <code>
     * String remoteOutput = shell("ssh://user:pass@host/work/dir/path", "ls")
     * </code>
     * </pre>
     */
    public static String shell(String connectUri, String command) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            shell(connectUri, command, baos);
            return baos.toString();
        } catch (RuntimeException e) {
            LOG.warn(baos.toString());
            throw e;
        }
    }

    /**
     * <pre>
     * <code>
     * shell("ssh://user:pass@host/work/dir/path", "ls", System.out)
     * </code>
     * </pre>
     */
    public static void shell(String connectUri, String script, OutputStream out) {
        try (SessionHolder<ChannelShell> session = new SessionHolder<>("shell", URI.create(connectUri));
             PipedOutputStream pipe = new PipedOutputStream();
             PipedInputStream in = new PipedInputStream(pipe);
             PrintWriter pw = new PrintWriter(pipe)) {

            if (session.getWorkDir() != null)
                pw.println("cd " + session.getWorkDir());
            pw.println(script);
            pw.println("exit");
            pw.flush();

            shell(session, in, out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void shell(SessionHolder<ChannelShell> session, InputStream is, OutputStream os) {
        try {
            ChannelShell channel = session.getChannel();
            channel.setInputStream(is, true);
            channel.setOutputStream(os, true);

            LOG.info("Starting shell for " + session.getMaskedUri());
            session.execute();
            session.assertExitStatus("Check shell output for error details.");
        } catch (InterruptedException | JSchException e) {
            throw new RuntimeException("Cannot execute script", e);
        }
    }

    /**
     * <pre>
     * <code>
     * System.out.println(exec("ssh://user:pass@host/work/dir/path", "ls -t | head -n1"));
     * </code>
     *
     * <pre>
     *
     * @param connectUri
     * @param command
     * @return
     */
    public static String exec(String connectUri, String command) {
        try (SessionHolder<ChannelExec> session = new SessionHolder<>("exec", URI.create(connectUri))) {
            String scriptToExecute = session.getWorkDir() == null
                    ? command
                    : "cd " + session.getWorkDir() + "\n" + command;
            return exec(session, scriptToExecute);
        }
    }

    private static String exec(SessionHolder<ChannelExec> session, String command) {
        try (PipedOutputStream errPipe = new PipedOutputStream();
             PipedInputStream errIs = new PipedInputStream(errPipe);
             InputStream is = session.getChannel().getInputStream()) {

            ChannelExec channel = session.getChannel();
            channel.setInputStream(null);
            channel.setErrStream(errPipe);
            channel.setCommand(command);

            LOG.info("Starting exec for " + session.getMaskedUri());
            session.execute();
            String output = IOUtils.toString(is);
            session.assertExitStatus(IOUtils.toString(errIs));

            return trim(output);
        } catch (InterruptedException | JSchException | IOException e) {
            throw new RuntimeException("Cannot execute command", e);
        }
    }

    public static class SessionHolder<C extends Channel> implements Closeable {

        private static final int DEFAULT_CONNECT_TIMEOUT = 5000;
        private static final int DEFAULT_PORT = 22;
        private static final int TERMINAL_HEIGHT = 1000;
        private static final int TERMINAL_WIDTH = 1000;
        private static final int TERMINAL_WIDTH_IN_PIXELS = 1000;
        private static final int TERMINAL_HEIGHT_IN_PIXELS = 1000;
        private static final int DEFAULT_WAIT_TIMEOUT = 100;

        private String channelType;
        private URI uri;
        private Session session;
        private C channel;

        public SessionHolder(String channelType, URI uri) {
            this(channelType, uri, ImmutableMap.of("StrictHostKeyChecking", "no"));
        }

        public SessionHolder(String channelType, URI uri, Map<String, String> props) {
            this.channelType = channelType;
            this.uri = uri;
            this.session = newSession(props);
            this.channel = newChannel(session);
        }

        private Session newSession(Map<String, String> props) {
            int currTime = 1;
            while (true) {
                try {
                    Properties config = new Properties();
                    config.putAll(props);

                    JSch jsch = new JSch();
                    Session newSession = jsch.getSession(getUser(), uri.getHost(), getPort());
                    newSession.setPassword(getPass());
                    newSession.setUserInfo(new User(getUser(), getPass()));
                    newSession.setDaemonThread(true);
                    newSession.setConfig(config);
                    newSession.connect(DEFAULT_CONNECT_TIMEOUT);
                    return newSession;
                } catch (JSchException e) {
                    if (currTime > RETRY_TIME) {
                        LOG.error("Minions tried but could not create session, retried {} times :(", RETRY_TIME);
                        throw new RuntimeException("Cannot create session for " + getMaskedUri(), e);
                    }
                    currTime++;
                }
            }
        }

        @SuppressWarnings("unchecked")
        private C newChannel(Session session) {
            try {
                Channel newChannel = session.openChannel(channelType);
                if (newChannel instanceof ChannelShell) {
                    ChannelShell channelShell = (ChannelShell) newChannel;
                    channelShell.setPtyType("ANSI", TERMINAL_WIDTH, TERMINAL_HEIGHT, TERMINAL_WIDTH_IN_PIXELS, TERMINAL_HEIGHT_IN_PIXELS);
                }
                return (C) newChannel;
            } catch (JSchException e) {
                throw new RuntimeException("Cannot create " + channelType + " channel for " + getMaskedUri(), e);
            }
        }

        public void assertExitStatus(String failMessage) {
            checkState(channel.getExitStatus() == 0, "Exit status %s for %s\n%s", channel.getExitStatus(), getMaskedUri(), failMessage);
        }

        public void execute() throws JSchException, InterruptedException {
            channel.connect();
            channel.start();
            while (!channel.isEOF())
                sleep(DEFAULT_WAIT_TIMEOUT);
        }

        public Session getSession() {
            return session;
        }

        public C getChannel() {
            return channel;
        }

        @Override
        public void close() {
            if (channel != null)
                channel.disconnect();
            if (session != null)
                session.disconnect();
        }

        public String getMaskedUri() {
            return uri.toString().replaceFirst(":[^:]*?@", "@");
        }

        public int getPort() {
            return uri.getPort() < 0 ? DEFAULT_PORT : uri.getPort();
        }

        public String getUser() {
            return uri.getUserInfo().split(":")[0];
        }

        public String getPass() {
            return uri.getUserInfo().split(":")[1];
        }

        public String getWorkDir() {
            return uri.getPath();
        }
    }

    private static class User implements UserInfo, UIKeyboardInteractive {

        private String user;
        private String pass;

        public User(String user, String pass) {
            this.user = user;
            this.pass = pass;
        }

        @Override
        public String getPassword() {
            return pass;
        }

        @Override
        public boolean promptYesNo(String str) {
            return false;
        }

        @Override
        public String getPassphrase() {
            return user;
        }

        @Override
        public boolean promptPassphrase(String message) {
            return true;
        }

        @Override
        public boolean promptPassword(String message) {
            return true;
        }

        @Override
        public void showMessage(String message) {
            // do nothing
        }

        @Override
        public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt, boolean[] echo) {
            return null;
        }
    }
}
