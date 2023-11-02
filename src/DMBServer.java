import java.io.*;
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
            server.close();
            System.out.println("New connection ... " + connection.getInetAddress().getHostName() + ":" + connection.getPort());
            BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String userInput = input.readLine();
            System.out.println("\nReceived data from client:\n" + userInput + "\n\n");
            if (userInput.startsWith("%%from")) {
                userInput = userInput.substring(7);
                sendToMessageBoard(userInput);
            } else if (userInput.startsWith("%%fetch")) {
                fetchData(userInput);
            } else {
                sendToMessageBoard(userInput);
            }
            System.out.print("\n++ Closing connection... ");
            connection.close();
            System.out.println("\n...closed.");
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
        args[0] = linkToBoard + "/" + time.getSimpleTimeDateFormat();
        args[1] = time.getSimpleDateFormat();
        args[2] = input;
        DirAndFile.main(args);
    }

    public static void fetchData(String userInput) {
        try {
            PrintWriter write = new PrintWriter(connection.getOutputStream(), true);
            String date = userInput.substring(8);
            File directory = new File(linkToBoard + "/" + date);
            String serverResponse;
            if (directory.exists()) {
                serverResponse = "%%messages" + date;
                for (File message : directory.listFiles()) {
                    BufferedReader read = new BufferedReader(new FileReader(directory + "/" + message));
                    serverResponse += "\n\t" + message.getName().substring(11) + " ";
                    String line = read.readLine();
                    serverResponse += line;
                }
                serverResponse += "\n%%end";
            } else if (directory.listFiles().length == 0) {
                serverResponse = "%%none";
            } else {
                serverResponse = "%%error";
            }
            write.println(serverResponse);
        } catch (IOException e) {
            System.out.println("Connection refused!");
            System.exit(1);
        }
    }
}
