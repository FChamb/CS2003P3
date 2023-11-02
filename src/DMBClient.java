
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
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
        runProtocol();
    }

    public static void runProtocol() {
        byte[] messageSize = null;
        while (messageSize == null) {
            messageSize = getUserCommand();
        }
        int size = messageSize.length;
        System.out.println("Sending " + size + " bytes: " + new String(messageSize, StandardCharsets.UTF_8));
        try {
            Socket connection = startClient();
            if (connection == null) {
                System.out.println("Server connection closed");
                System.exit(0);
            }
            boolean receieved = false;
            while (!receieved) {
                OutputStream out = connection.getOutputStream();
                out.write(messageSize, 0, size);
//                System.out.println("1");
                InputStream input = connection.getInputStream();
//                System.out.println("2");
                String serverInput = new String(input.readAllBytes(), StandardCharsets.UTF_8);
                if (serverInput != null) {
                    receieved = true;
                }
//                System.out.println("3");
                System.out.println(serverInput);
            }
            System.out.print("\nClosing connection...");
            connection.close();
            System.out.println("...closed.");
        } catch (IOException e) {
            System.out.println("Connection refused!");
            System.exit(1);
        }
    }

    public static byte[] getUserCommand() {
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
            string = myUser + string;
            return string.getBytes();
        }
    }

    public static byte[] to(String string) {
        String[] commands = string.split(" ", 3);
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
        return ("%%from " + username + " " + commands[2]).getBytes();
    }

    public static byte[] fetch(String string) {
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
        return ("%%fetch " + date).getBytes();
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
            System.out.println("UnknownHost Exception: " + e.getMessage());
            return null;
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
            return null;
        }
    }

    public static HashMap<String, String> parseCSV() {
        CSVParser parser = new CSVParser();
        parser.parse();
        return parser.getUsernames();
    }
}
