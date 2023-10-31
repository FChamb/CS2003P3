import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public class Configuration {
    public Properties properties;
    public String propertyFile = "cs2003-net2.properties";

    public String serverAddress;
    public int serverPort;
    public String directoryFile;
    public String boardDirectory;
    public int maxClients;
    public int maxSize;
    public String logFile;

    public Configuration(String propertyFile) {
        InetAddress address;
        String h = "hostname";
        if (propertyFile != null) {
            this.propertyFile = propertyFile;
        }
        try {
            properties = new Properties();
            InputStream p = getClass().getClassLoader().getResourceAsStream(this.propertyFile);
            if (p != null) {
                properties.load(p);
                String property;
                if ((property = properties.getProperty("serverAddress")) != null) {
                    System.out.println(this.propertyFile + " serverAddress: " + this.serverAddress + " -> " + property);
                    this.serverAddress = new String(property);
                }
                if ((property = properties.getProperty("serverPort")) != null) {
                    System.out.println(this.propertyFile + " serverPort: " + this.serverPort + " -> " + property);
                    this.serverPort = Integer.parseInt(property);
                }
                if ((property = properties.getProperty("directoryFile")) != null) {
                    System.out.println(this.propertyFile + " directoryFile: " + this.directoryFile + " -> " + property);
                    this.directoryFile = new String(property);
                }
                if ((property = properties.getProperty("boardDirectory")) != null) {
                    System.out.println(this.propertyFile + " boardDirectory: " + this.boardDirectory + " -> " + property);
                    this.boardDirectory = new String(property);
                }
//                if ((property = properties.getProperty("maxClients")) != null) {
//                    System.out.println(this.propertyFile + " maxClients: " + this.maxClients + " -> " + property);
//                    this.maxClients = Integer.parseInt(property);
//                }
                if ((property = properties.getProperty("maxMessages")) != null) {
                    System.out.println(this.propertyFile + " maxMessages: " + this.maxSize + " -> " + property);
                    this.maxSize = Integer.parseInt(property);
                }
                if ((property = properties.getProperty("logFile")) != null) {
                    System.out.println(this.propertyFile + " logFile: " + this.logFile + " -> " + property);
                    this.logFile = new String(property);
                }

                p.close();
            }

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
