import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Properties;

public class Configuration {
    public Properties properties;
    public String propertyFile = "cs2003-net2.properties";

    public String serverAddress;
    public int serverPort;
    public String directoryFile;
    public String boardDirectory;
    public int serverTimeOut;
    public int maxSize;
    public String logFile;
    public String csvUsernameFile;

    /**
     * Configuration checks that the property file is not null. If it is not, a try catch loop
     * reads through the file and parses every value to the correct value. This class is based off
     * of the W05 exercises provided in the CS2003 Exercise class.
     * @param propertyFile string containing the path to the property file
     */
    public Configuration(String propertyFile) {
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
                    this.serverAddress = property;
                }
                if ((property = properties.getProperty("serverPort")) != null) {
                    System.out.println(this.propertyFile + " serverPort: " + this.serverPort + " -> " + property);
                    this.serverPort = Integer.parseInt(property);
                }
                if ((property = properties.getProperty("directoryFile")) != null) {
                    System.out.println(this.propertyFile + " directoryFile: " + this.directoryFile + " -> " + property);
                    this.directoryFile = property;
                }
                if ((property = properties.getProperty("boardDirectory")) != null) {
                    System.out.println(this.propertyFile + " boardDirectory: " + this.boardDirectory + " -> " + property);
                    this.boardDirectory = property;
                }
                if ((property = properties.getProperty("serverTimeOut")) != null) {
                    System.out.println(this.propertyFile + " serverTimeOut: " + this.serverTimeOut + " -> " + property);
                    this.serverTimeOut = Integer.parseInt(property);
                }
                if ((property = properties.getProperty("maxSize")) != null) {
                    System.out.println(this.propertyFile + " maxSize: " + this.maxSize + " -> " + property);
                    this.maxSize = Integer.parseInt(property);
                }
                if ((property = properties.getProperty("logFile")) != null) {
                    System.out.println(this.propertyFile + " logFile: " + this.logFile + " -> " + property);
                    this.logFile = property;
                }
                if ((property = properties.getProperty("csvUsernameFile")) != null) {
                    System.out.println(this.propertyFile + " csvUsernameFile: " + this.csvUsernameFile + " -> " + property);
                    this.csvUsernameFile = property;
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
