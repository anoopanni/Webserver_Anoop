import java.io.*;
import java.util.*;
import java.net.*;
import java.text.SimpleDateFormat;

public class Server
{
    public static void main(String args[]) throws IOException
    {
        if(args.length != 4)
        {
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


    private void handleRequest() throws IOException
    {
        // Get a reference to the socket's input and output streams.
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintStream o = new PrintStream(new BufferedOutputStream(socket.getOutputStream()));

        String command = in.readLine(); // command is the first Request line (get /path/file.html version of http) of the HTTP request message.
        System.out.println();
        System.out.println(command);  // printing  the first Request line.

        String file="";
        StringTokenizer tokens = new StringTokenizer(command);
        try{

            //Parse the file from the GET command
            if (tokens.hasMoreElements() && tokens.nextToken().equalsIgnoreCase("GET") && tokens.hasMoreElements())
                file = tokens.nextToken();
            else
            throw new FileNotFoundException(); // Bad request

        if (file.endsWith("/")) //Append "/" with "index.html"
            file += "index.html";

        while (file.indexOf("/") == 0) //Remove leading / from filename
            file = file.substring(1);

            if((new File(file).exists()) && !(new File(file).canRead()))
            {
                o.print("HTTP/1.0 403 Forbidden\r\n"+
                "Location: /"+file+"/\r\n\r\n");
                o.close();
                return;
            }

        FileInputStream f = new FileInputStream(file);
        SimpleDateFormat format1 = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:Ss z");

        // Determine the content type and print HTTP header
        String content = "text/plain";
        if (file.endsWith(".html") || file.endsWith(".htm"))
            content = "text/html";
        else if (file.endsWith(".jpg") || file.endsWith(".jpeg"))
            content = "image/jpeg";
        else if (file.endsWith(".gif"))
            content = "image/gif";
        else if (file.endsWith(".class"))
            content = "application/octet-stream";
        o.print("HTTP/1.0 200 OK\r\n"+
            "Content-type: "+content+"\r\n\r\n");

        o.print("HTTP/1.0 200 OK\r\n"+"Content-length:"+(new File(file)).length()+"\r\n\r\n"+"Date: "+format1.format(new Date())+"\r\n\r\n");
        System.out.println("HTTP/1.0 200 OK\r\n");

        byte buffer[]=new byte[5000];
        int i;
        while ((i=f.read(buffer))>0)
        o.write(buffer, 0, i);
        o.close();
        socket.close();
        System.out.println("connection closed");
        
        }
        catch (FileNotFoundException fnfe) 
        {
            o.print("HTTP/1.0 404 Not Found\r\n"+
                "Content-type: text/html\r\n\r\n"+
                "<html><head></head><body>HTTP/1.0 404 Not Found<br>"+file+" not found</body></html>\n");
            o.close();
            in.close();
            socket.close();
            System.out.println("connection closed");
        }  
    }
}