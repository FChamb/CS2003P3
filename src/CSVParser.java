import java.io.*;
import java.util.HashMap;


public class CSVParser {
    private final String propertyFile = "cs2003-net2.properties";
    private Configuration configuration;
    private HashMap<String, String> usernames;
    private String csvLocation;
    public CSVParser() {
        this.configuration = new Configuration(this.propertyFile);
        this.usernames = new HashMap<>();
        this.csvLocation = this.configuration.csvUsernameFile;
    }

    public void parse() {
        try {
            BufferedReader read = new BufferedReader(new FileReader(this.csvLocation));
            String line;
            while ((line = read.readLine()) != null) {
                String[] values = line.split(",");
                this.usernames.put(values[0], values[1]);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("File is empty!");
            System.exit(1);
        }
    }

    public HashMap<String, String> getUsernames() {
        return this.usernames;
    }

}
