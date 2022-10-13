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
    public HttpRequest(Socket socket) throws IOException
    {
        this.socket = socket;
    }

    public void run()
    {
        try
        {
            handleRequest();
            System.out.println(socket);
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
}


private void handleRequest() throws IOException{
    BufferReader in = new BufferReader(new InputStreamReader(socket.getInputStream()));
    PrintStream o = new PrintStream(new BufferReader(socket.getOutputStream()));

    String command = in.readLine();
    System.out.println();
    
}