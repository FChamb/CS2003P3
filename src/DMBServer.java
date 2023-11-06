import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class DMBServer {
    private static final String propertyFile = "cs2003-net2.properties";
    private static Configuration configuration;
    private static int portNumber;
    private static ServerSocket server;
    private static Queue queue;
    private static int serverTimeOut;
    private static String linkToBoard;

    /**
     * The main method has four main functions to begin the server program. It calls to setUpConfiguration
     * and startServer. This method also creates a new thread, accept clients which has a runnable with a
     * while true loop to continuously try enqueueing clients to the queue. This thread is then started, and
     * another while true loop uses a conditional statement to check the queue and see if any connections have
     * been added. If there are clients in the queue, a socket is set to the first enqueued client, and passed
     * into getMessage. If there are no clients in the queue, the current thread is slept for 100ms to refresh
     * the system.
     * @param args the command line arguments - not used in this method
     */
    public static void main(String[] args) {
        setUpConfiguration();
        startServer();
        Thread acceptClients = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        queue.enqueue(server.accept());
                    } catch (SocketTimeoutException e) {
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        acceptClients.start();
        while (true) {
            Socket connection;
            if (!queue.isEmpty()) {
                connection = (Socket) queue.dequeue();
                getMessage(connection);
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Set up configuration creates a new configuration object, given the properties file.
     * Then each of the corresponding variables are set to their defined value from the properties
     * list. The port number, server timeout, and link to board directory are all defined and a new
     * Queue object is created and linked up.
     */
    public static void setUpConfiguration() {
        configuration = new Configuration(propertyFile);
        portNumber = configuration.serverPort;
        serverTimeOut = configuration.serverTimeOut;
        linkToBoard = configuration.boardDirectory;
        queue = new Queue();
    }

    /**
     * Start server uses the values grabbed from the properties file in setUpConfiguration
     * to create a new server socket and set the timeout. An appropriate message is printed
     * if the connection is made and also if it is refused.
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

    /**
     * Get message takes a specific client connection socket as input and begins the
     * process of receiving data. A try catch statement prints out if the connection
     * is properly made or if it was refused. Then a BufferedReader grabs the clients input
     * stream. A string value is set to the information sent by the client. This is printed
     * to the server terminal. Then three conditional statements determine what was sent by
     * the client. If the command is %%from, then the input cuts the command portion off and
     * calls sendToMessageBoard. If the command is %%fetch, fetchData is called with the connection
     * and input. Otherwise, there is no command and the data is sent to sendToMessageBoard. Lastly
     * the connection is closed.
     * @param connection socket for the clients connection to the server
     */
    public static void getMessage(Socket connection) {
        try {
            System.out.println("New connection ... " + connection.getInetAddress().getHostName() + ":" + connection.getPort());
            BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String userInput = input.readLine();
            System.out.println("\nReceived data from client:\n" + userInput + "\n\n");
            if (userInput.startsWith("%%from")) {
                userInput = userInput.substring(7);
                sendToMessageBoard(userInput);
            } else if (userInput.startsWith("%%fetch")) {
                fetchData(userInput, connection);
            } else {
                sendToMessageBoard(userInput);
            }
            System.out.print("\n++ Closing connection... ");
            connection.close();
            System.out.println("\n...closed.");
        } catch (IOException e) {
            System.out.println("Connection refused");
            System.exit(1);
        }
    }

    /**
     * Send to message board takes the clients provided data. A new args array is created
     * which will be used to parse into the main method of DirAndFile. A TimeStamp object is
     * created. The first argument is the link to the message board direction + the current date
     * for which day to add the information to. The second argument is the name of the file, which
     * is the specific time stamp when the message was sent. Lastly, the third argument is the input
     * the user sent. These are all passed to the main method of DirAndFile.
     * @param input a string value with what the client sent to the server
     */
    public static void sendToMessageBoard(String input) {
        String[] args = new String[3];
        TimeStamp time = new TimeStamp();
        args[0] = linkToBoard + "/" + time.getSimpleTimeDateFormat();
        args[1] = time.getSimpleDateFormat();
        args[2] = input;
        DirAndFile.main(args);
    }

    /**
     * Fetch data takes two inputs, the client provided data and the socket connection.
     * A try catch statement attempts to send a response to the client. A print writer
     * is created with the output stream of the connection. The current date is grabbed by
     * cutting the input provided by the client. A new file is created which determines if
     * the directory for a given date exists. If it does, the server response string builds
     * the response by creating the first line, and using a buffered reader to add every file's
     * contents to the string with the time stamp. If the directory does not exist, or it is empty
     * the response is %%none. Lastly, %%error is returned if the program runs into some other error.
     * This message is written to the output stream which passes it to the client.
     * @param userInput a string value with what the client sent to the server
     * @param connection socket for the clients connection to the server
     */
    public static void fetchData(String userInput, Socket connection) {
        try {
            PrintWriter write = new PrintWriter(connection.getOutputStream(), true);
            String date = userInput.substring(8);
            File directory = new File(linkToBoard + "/" + date);
            String serverResponse;
            if (directory.exists()) {
                serverResponse = "%%messages " + date;
                for (File message : directory.listFiles()) {
                    BufferedReader read = new BufferedReader(new FileReader(message));
                    serverResponse += "\n\t" + message.getName().substring(11) + " ";
                    String line = read.readLine();
                    serverResponse += line;
                }
                serverResponse += "\n%%end";
            } else if (!directory.exists()) {
                serverResponse = "%%none";
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
