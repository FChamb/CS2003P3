import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class DMBServer {
    /**
     * The private integer, port, exists to enable easy access in the class. This is the same for
     * the ServerSocket object, server. The protocol array stores the valid arguments for the
     * specification defined communication protocol. This array also stores two extra values, "VALID".
     * These values are to ensure that the final message is valid before cutting the connection.
     */
    private static int port;
    private static ServerSocket server;
    private static String nginxPath = "/cs/home/fc84/nginx_default/cs2003-net2";

    /**
     * The main method has two actions. It first grabs the command line argument and ensures that there
     * is only the appropriate item, port number. An array index out of bounds exception is
     * thrown if the arguments are not provided and a helpful hint message is supplied to the user.
     * The second action of the main method is to start the client connection using a socket with the user
     * designated port.
     * @param args the command line arguments - port number
     */
    public static void main(String[] args) {
        try {
            if (args.length != 1) {
                throw new ArrayIndexOutOfBoundsException("\n SimpleServer <port> \n");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        port = Integer.parseInt(args[0]);
        startServer();

        try {
            Socket connection = server.accept();
            server.close();

            System.out.println("New connection ... " + connection.getInetAddress().getHostName() + ":" + connection.getPort());

        } catch (IOException e) {
            System.out.println("Connection refused");
            System.exit(1);
        }
    }

    /**
     * Start server takes the user defined port number to create a server socket
     * connection point. An appropriate message is printed if the connection
     * is made and also if it is refused.
     */
    public static void startServer() {
        try {
            server = new ServerSocket(port);
            System.out.println("--> Starting Server " + server + " <--");
        } catch (IOException e) {
            System.out.println("Connection refused");
            System.exit(1);
        }
    }
}
