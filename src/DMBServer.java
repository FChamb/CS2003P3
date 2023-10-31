import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class DMBServer {
    private static final String propertyFile = "cs2003-net2.properties";
    private static Configuration configuration;
    private static int portNumber;
    private static ServerSocket server;
    private static Socket connection;
    private static int serverTimeOut;
    private static String linkToBoard;

    /**
     * The main method has two actions. It first grabs the command line argument and ensures that there
     * is only the appropriate item, port number. An array index out of bounds exception is
     * thrown if the arguments are not provided and a helpful hint message is supplied to the user.
     * The second action of the main method is to start the client connection using a socket with the user
     * designated port.
     * @param args the command line arguments - port number
     */
    public static void main(String[] args) {
        setUpConfiguration();
        startServer();
        while(true) {
            runProtocol();
        }
    }

    /**
     * Start server takes the user defined port number to create a server socket
     * connection point. An appropriate message is printed if the connection
     * is made and also if it is refused.
     */
    public static void startServer() {
        try {
            server = new ServerSocket(portNumber);
            System.out.println("--> Starting Server " + server + " <--");
            server.setSoTimeout(serverTimeOut);
        } catch (IOException e) {
            System.out.println("Connection refused");
            System.exit(1);
        }
    }

    public static void setUpConfiguration() {
        configuration = new Configuration(propertyFile);
        portNumber = configuration.serverPort;
        serverTimeOut = configuration.serverTimeOut;
        linkToBoard = configuration.boardDirectory;
    }

    public static void runProtocol() {
        try {
            connection = server.accept();
            System.out.println("New connection ... " + connection.getInetAddress().getHostName() + ":" + connection.getPort());
            InputStream input = connection.getInputStream();
            String userInput = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            sendToMessageBoard(userInput);
            System.out.println("Received data from client: " + userInput);
            connection.close();
            System.out.println("\n\nConnection closed");

        } catch (SocketTimeoutException e) {
            System.out.println("Socket timeout");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Connection refused");
            System.exit(1);
        }
    }

    public static void sendToMessageBoard(String input) {
        String[] args = new String[3];
        TimeStamp time = new TimeStamp();
        args[0] = time.getSimpleTimeDateFormat();
        args[1] = linkToBoard + "/" + time.getSimpleDateFormat();
        args[2] = input;
        DirAndFile.main(args);
    }
}
