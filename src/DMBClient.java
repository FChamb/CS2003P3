import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Scanner;

public class DMBClient {
    static final String propertyFile = "cs2003-net2.properties";
    static int maxSize;
    static Configuration configuration;
    static String server;
    static int portNumber;
    static HashMap<String, String> usernames;

    /**
     * The main method has two main functions. It first calls setUpConfiguration
     * and runProtocol.
     * @param args the command line arguments - not used in this method
     */
    public static void main(String[] args) {
        setUpConfiguration();
        runProtocol();
    }

    /**
     * Set up configuration creates a new configuration object, given the properties file.
     * Then each of the corresponding variables are set to their defined value from the properties
     * list. The server address, port number, and max size are all set to their property given data.
     * Lastly, usernames calls parseCSV.
     */
    public static void setUpConfiguration() {
        configuration = new Configuration(propertyFile);
        server = configuration.serverAddress;
        portNumber = configuration.serverPort;
        maxSize = configuration.maxSize;
        usernames = parseCSV();
    }

    /**
     * Run protocol enables the client to ask the user for input and process this information
     * to properly send data to the server. A while loop checks that the message value is
     * null, and continuously sets the value to getUserCommand until updated. This value is
     * then printed to the terminal to show what is being sent. A try catch statement creates
     * a socket from startClient method. If the connection is null the proper message is printed.
     * A print writer is created with the connections output stream. This message is then sent to
     * the server. Lastly, a conditional statement looks to see if the message sent was a %%fetch
     * command. If it is, a while loops continuously checks the input stream and prints the
     * output. Then the connection is closed.
     */
    public static void runProtocol() {
        String message = null;
        while (message == null) {
            message = getUserCommand();
        }
        System.out.println("Sending " + message.getBytes().length + " bytes: " + message);
        try {
            Socket connection = startClient();
            if (connection == null) {
                System.out.println("Server connection closed");
                System.exit(0);
            }
            PrintWriter write = new PrintWriter(connection.getOutputStream(), true);
            write.println(message);
            BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            if (message.startsWith("%%fetch")) {
                String response;
                System.out.println("\n++ Waiting for server response...");
                System.out.println("++ Received response from server:\n");
                while ((response = input.readLine()) != null) {
                    System.out.println(response);
                }
            }
            System.out.println("\n++ Closing connection... ");
            connection.close();
            System.out.println("...closed.");
        } catch (IOException e) {
            System.out.println("Connection refused!");
            System.exit(1);
        }
    }

    /**
     * Get user command creates a scanner to grab user information. Then conditional statements
     * determine what needs to be processed. If the input starts with %%, then the program
     * knows that a command is being called, and calls either to() or fetch(). If neither of these match,
     * a message is printed to help the user and null is returned. If the input does not stat with %%,
     * the program checks that the string entered is not longer than the max allowed size. If it is a message
     * is printed. Otherwise, this value has adds the default user identifier from properties to the string
     * and returns it.
     * @return string value with the user provided data
     */
    public static String getUserCommand() {
        System.out.println("Type below:");
        String string;
        Scanner scan = new Scanner(System.in);
        string = scan.nextLine();
        if (string.startsWith("%%")) {
            if (string.startsWith("%%to")) {
                return to(string);
            } else if (string.startsWith("%%fetch")) {
                return fetch(string);
            } else {
                System.out.println("Invalid command! Try: <%%to> <userName> <message> or <%%fetch> <YYYY-MM-DD>");
                return null;
            }
        } else {
            if (string.length() > maxSize) {
                System.out.println("You entered too many bytes! Max allowance: " + maxSize + ".");
                return null;
            }
            String myUser = server.substring(0, server.indexOf("."));
            string = myUser + " " + string;
            return string;
        }
    }

    /**
     * To takes the user input and splits the command into three parts using a split.
     * If the commands are not equal to 3, a proper message is printed. Next username
     * is grabbed from the second command argument. If the hashmap of valid users does
     * not contain this username, a message is printed. Otherwise, the port number is
     * set using the provided users port number. And the server address is set using
     * the properties file and username. Lastly the message is checked to ensure that
     * its size is not larger than maxSize. If everything is valid, the string is returned.
     * @param string value with the user provided input
     * @return string value with the user provided data
     */
    public static String to(String string) {
        String[] commands = string.split(" ", 3);
        String myUser = server.substring(0, server.indexOf("."));
        if (commands.length != 3) {
            System.out.println("Wrong command format! <%%to> <userName> <message>");
            return null;
        }
        String username = commands[1].toLowerCase();
        if (!usernames.containsKey(username)) {
            System.out.println("Invalid Username!");
            return null;
        } else {
            portNumber = Integer.parseInt(usernames.get(username));
            server = username + server.substring(server.indexOf("."));
        }
        if (username.length() + 1 + commands[2].length() >= maxSize) {
            System.out.println("You entered too many bytes! Max allowance: " + maxSize + ".");
            return null;
        }
        return ("%%from " + myUser + " " + commands[2]);
    }

    /**
     * Fetch takes the user input and splits the command into parts. If the length
     * of commands is greater than two a message is printed for the user. Otherwise,
     * a check sees if the user provided a date or not. If they provided a date, several
     * conditional statements use regex and logic to ensure the proper format is provided
     * and a month, day is not larger than valid options. If the date is not provided,
     * a time stamp is grabbed and this is returned with the message.
     * @param string value with the user provided input
     * @return string value with the user provided data
     */
    public static String fetch(String string) {
        String[] commands = string.split(" ");
        String date;
        if (commands.length > 2) {
            System.out.println("Wrong command format! <%%fetch> <YYYY-MM-DD(Optional)>");
            return null;
        } else if (commands.length == 1) {
            date = new TimeStamp().getSimpleTimeDateFormat();
        } else {
            if (!commands[1].matches("^([0-9]{4}-[0-9]{2}-[0-9]{2})$")) {
                System.out.println("Wrong date format! <%%fetch> <YYYY-MM-DD>");
                return null;
            }
            int month = Integer.parseInt(commands[1].split("-")[1]);
            int day = Integer.parseInt(commands[1].split("-")[2]);
            if (month > 12) {
                System.out.println("Invalid month input! <%%fetch> <YYYY-MM-DD>");
                return null;
            } else if (day > 31) {
                System.out.println("Invalid day input! <%%fetch> <YYYY-MM-DD>");
                return null;
            }
            date = commands[1];
        }
        return ("%%fetch " + date);
    }

    /**
     * Start client creates a socket connection and InetAddress. Then a try, tries
     * to connect to the server with the port number and address. If this is a valid
     * connection that is printed, otherwise the connection is refused.
     * @return string value with the user provided data
     */
    public static Socket startClient() {
        Socket connection = null;
        InetAddress name;
        try {
            name = InetAddress.getByName(server);
            connection = new Socket(name, portNumber);
            System.out.println("++ Connecting to " + name + ":" + portNumber + " -> " + connection);
        } catch (IOException e) {
            System.out.println("Connection refused!");
            System.exit(1);
        }
        return connection;
    }

    /**
     * Parse CSV creates a new CSVParser object and parses the CSV file of
     * username data. The hashmap of usernames is returned.
     * @return hashmap of usernames from the CSV parser
     */
    public static HashMap<String, String> parseCSV() {
        CSVParser parser = new CSVParser(configuration);
        parser.parse();
        return parser.getUsernames();
    }
}
