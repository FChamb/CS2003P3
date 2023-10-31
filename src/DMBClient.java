
import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
  * Daily Message Board Client
  *
  * based on code by Saleem Bhatti, 28 Aug 2019
  *
  */
public class DMBClient {

  static int maxTextLen_;
  static Configuration c_;

  // from configuration file
  static String server; // FQDN
  static int port; //server port

  public static void main(String[] args)
  {

    c_ = new Configuration("cs2003-net2.properties");

    try {
        server = c_.serverAddress;
        port = c_.serverPort;
        maxTextLen_ = c_.maxSize;
    }
    catch (NumberFormatException e) {
        System.out.println("can't configure port: " + e.getMessage());
    }

//    if (args.length != 1) { // user has not provided arguments
//      System.out.println("\n DMBClient <string>\n");
//      System.exit(0);
//    }

    try {
      Socket       connection;
      OutputStream tx;
      InputStream  rx;
      byte[]       buffer;
      String       s = new String("");
      String       quit = new String("quit");
      int          r;

      connection = startClient();
      if (connection == null) {
        System.out.println("Server connection closed");
        System.exit(0);
      }
      tx = connection.getOutputStream();
      rx = connection.getInputStream();

//      buffer = args[0].getBytes();
      Scanner scan = new Scanner(System.in);
      buffer = scan.next().getBytes();
      r = buffer.length;
      if (r > maxTextLen_) {
        System.out.println("++ You entered more than " + maxTextLen_ + "bytes ... truncating.");
        r = maxTextLen_;
      }
      System.out.println("Sending " + r + " bytes");
      tx.write(buffer, 0, r); // to server

      System.out.print("\n++ Closing connection ... ");
      connection.close();
      System.out.println("... closed.");
    }

    catch (IOException e) {
      System.err.println("IO Exception: " + e.getMessage());
    }
  } // main

  static Socket startClient() {
    Socket connection;
    InetAddress name;

    try {
      name = InetAddress.getByName(server);
      connection = new Socket(name, port); // make a socket

      System.out.println("++ Connecting to " + name + ":" + port
      + " -> " + connection);
      return connection;
    }

    catch (UnknownHostException e) {
      System.err.println("UnknownHost Exception: " + e.getMessage());
      return null;
    }
    catch (IOException e) {
      System.err.println("IO Exception: " + e.getMessage());
      return null;
    }
  } // startClient

} // DMBClient
