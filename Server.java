import java.io.*;
import java.util.*;
import java.net.*;

public class Server
{
    public static void main(String args[]) throws IOException
    {
        if(args.length != 4){
            System.out.println("Required 4 arguments");
            System.exit(0);
        }

        // arg[3] has port number
        ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[3]));   //Establish the listen socket
        
        // process the HTTP requests in an infinite loop
        while(true)
        {
            Socket connectionSocket = serverSocket.accept(); // accept() Waits for incoming client/TCP connection request
            HttpRequest req = new HttpRequest(connectionSocket);    // Object to process the HTTP request message 
            Thread th = new Thread(req);  // Initiating new threads to handle requests
            th.start();   // Start the thread
        }

    }
}

class HttpRequest implements Runnable
{
    Socket socket;
    public HttpRequest
}