
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class DMBClient {
    static int maxSize;
    static Configuration configuration;
    static String server;
    static int portNumber;

    public static void main(String[] args) {
        configuration = new Configuration("cs2003-net2.properties");
        try {
            server = configuration.serverAddress;
            portNumber = configuration.serverPort;
            maxSize = configuration.maxSize;
        }
        catch (NumberFormatException e) {
            System.out.println("Can't configure port number: " + e.getMessage());
        }
        try {
            Socket connection;
            OutputStream out;
            InputStream in;
            byte[] messageSize;
            String string;
            int size;
            connection = startClient();
            if (connection == null) {
              System.out.println("Server connection closed");
              System.exit(0);
            }
            out = connection.getOutputStream();
            in = connection.getInputStream();
            Scanner scan = new Scanner(System.in);
            string = scan.nextLine();
            while (true) {
                messageSize = string.getBytes();
                size = messageSize.length;
                if (size > maxSize) {
                    System.out.println("You entered too many bytes; shortening to " + maxSize + ".");
                    size = maxSize;
                }
                System.out.println("Sending " + size + " bytes");
                out.write(messageSize, 0, size);
                string = scan.nextLine();
            }
//            System.out.print("\nClosing connection...");
//            connection.close();
//            System.out.println("...closed.");
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
}
