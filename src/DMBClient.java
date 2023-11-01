
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

    public static void main(String[] args) {
        configuration = new Configuration(propertyFile);
        try {
            server = configuration.serverAddress;
            portNumber = configuration.serverPort;
            maxSize = configuration.maxSize;
            usernames = parseCSV();
        }
        catch (NumberFormatException e) {
            System.out.println("Can't configure port number: " + e.getMessage());
        }
        try {
            Socket connection;
            OutputStream out;
            byte[] messageSize;
            String string;
            int size;
            Scanner scan = new Scanner(System.in);
            string = scan.nextLine();
            if (string.contains("%%to")) {
                String[] commands = string.split(" ", 2);
                if (commands.length != 3) {
                    System.out.println("Wrong command format! <%%to> <userName> <message>");
                    System.exit(0);
                }
                String username = commands[1].toLowerCase();
                if (!usernames.containsKey(username)) {
                    System.out.println("Invalid Username!");
                    System.exit(0);
                } else {
                    portNumber = Integer.parseInt(usernames.get(username));
                    server = username + server.substring(server.indexOf("."));
                }
                messageSize = ("%%from " + username + " " + commands[2]).getBytes();
            } else if (string.contains("%%fetch")) {
                String[] commands = string.split(" ");
                String date;
                if (commands.length != 2) {
                    date = new TimeStamp().getSimpleDateFormat();
                } else {
                    if (!commands[1].equals("^([0-9]{4}-[0-9]{2}-[0-9]{2})$")) {
                        System.out.println("Wrong date format! %%fetch <YYYY-MM-DD>");
                        System.exit(0);
                    }
                }
                messageSize = new byte[1];
            } else {
                messageSize = string.getBytes();
            }
            size = messageSize.length;
            if (size > maxSize + 7) {
                System.out.println("You entered too many bytes; shortening to " + maxSize + ".");
                size = maxSize + 7;
            }
            System.out.println("Sending " + size + " bytes");
            connection = startClient();
            if (connection == null) {
                System.out.println("Server connection closed");
                System.exit(0);
            }
            out = connection.getOutputStream();
            out.write(messageSize, 0, size);
            System.out.print("\nClosing connection...");
            connection.close();
            System.out.println("...closed.");
        } catch (IOException e) {
            System.err.println("IO Exception: " + e.getMessage());
      }
    }

    public static Socket startClient() {
        Socket connection;
        InetAddress name;
        try {
            name = InetAddress.getByName(server);
            connection = new Socket(name, portNumber);
            System.out.println("++ Connecting to " + name + ":" + portNumber + " -> " + connection);
            return connection;
        } catch (UnknownHostException e) {
            System.err.println("UnknownHost Exception: " + e.getMessage());
            return null;
        } catch (IOException e) {
            System.err.println("IO Exception: " + e.getMessage());
            return null;
        }
    }

    public static HashMap<String, String> parseCSV() {
        CSVParser parser = new CSVParser();
        parser.parse();
        return parser.getUsernames();
    }
}
