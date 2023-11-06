import java.io.*;
import java.util.HashMap;


public class CSVParser {
    private Configuration configuration;
    private HashMap<String, String> usernames;
    private String csvLocation;

    /**
     * This constructor method takes a configuration object and then
     * sets the usernames and csvLocation to proper values.
     * @param configuration object for the configuration of the properties file
     */
    public CSVParser(Configuration configuration) {
        this.configuration = configuration;
        this.usernames = new HashMap<>();
        this.csvLocation = this.configuration.csvUsernameFile;
    }

    /**
     * Parse uses a buffered reader and while loop to grab all the data from the csv file.
     * A string array splits the line at a "," and then each value is passed into the hashmap.
     */
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

    /**
     * Get usernames returns the hashmap of usernames.
     * @return a hashmap of username and port number strings
     */
    public HashMap<String, String> getUsernames() {
        return this.usernames;
    }

}
