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
            System.out.println("Format: Java Server -document_root <path_to_root_folder> -port <port_number>");
            System.exit(0);
        }

        // arg[3] has port number
        ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[3]));   //Establish the listen socket
        System.out.println("");
        System.out.println("***********************************************************************************************************");
        System.out.println("");
        System.out.println("Server socket opened on port: " + args[3]);
        System.out.println(serverSocket);
        // process the HTTP requests in an infinite loop
        while(true)
        {
            System.out.println("");
            System.out.println("***********************************************************************************************************");
            System.out.println("");
            System.out.println("Server waiting for new client requests");
            Socket connectionSocket = serverSocket.accept(); // accept() Waits for incoming client/TCP connection request
            ThreadRunner req = new ThreadRunner(connectionSocket);    // Object to process the HTTP request message 
            Thread th = new Thread(req);  // Initiating new threads to handle requests
            th.start();   // Start the thread
        }

    }
}

class ThreadRunner implements Runnable
{
    Socket socket;
    public ThreadRunner(Socket socket) throws IOException
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

    // Handling the request and sending response 
    private void handleRequest() throws IOException
    {
        // Get a reference to the socket's input and output streams.
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintStream out = new PrintStream(new BufferedOutputStream(socket.getOutputStream()));

        String command = in.readLine(); // command is the first Request line (get /path/file.html version of http) of the HTTP request message.
        System.out.println();
        System.out.println("\nRequest " + command);  // printing  the first Request line.

        String file="";
        StringTokenizer tokens = new StringTokenizer(command);
        try{

            //Parse the file from the GET command
            if (tokens.hasMoreElements() && tokens.nextToken().equalsIgnoreCase("GET") && tokens.hasMoreElements())
                file = tokens.nextToken();
            else
            {
                out.print("HTTP/1.0 400 Bad request\r\n\r\n"+
                "<html><head></head><body>HTTP/1.0 400 Bad request<br></body></html>\n");
                out.close();
                socket.close();
                in.close();
                return;
            }

            if (file.endsWith("/")) //Append "/" with "index.html"
                file += "index.html";

            while (file.indexOf("/") == 0) //Remove leading / from filename
                file = file.substring(1);

            if((new File(file).exists()) && !(new File(file).canRead()))
            {
                out.print("HTTP/1.0 403 Forbidden\r\n"+"/\r\n\r\n");
                out.print("<html><head></head><body>HTTP/1.0 403 Forbidden<br>"+"Cannot read File at Location: /"+file+"</body></html>\n");
                out.close();
                socket.close();
                in.close();
                System.out.println("connection closed");
                return;
            }

            FileInputStream f = new FileInputStream(file);

            // Determine the content type and print HTTP header
            String content = "text/plain";
            if (file.toLowerCase().endsWith(".html") || file.toLowerCase().endsWith(".htm"))
                content="text/html";
                else if (file.toLowerCase().endsWith(".jpg"))
                content="image/jpeg";
                else if (file.toLowerCase().endsWith(".gif"))
                content="image/gif";
                else if (file.toLowerCase().endsWith(".png"))
                content="image/png";
                else if (file.toLowerCase().endsWith(".ico"))
                content="/img/favicon.png";
                else if (file.toLowerCase().endsWith(".class"))
                content="application/octet-stream";

                int filelength=0;
                File file1 = new File(file);
                if(file1.exists()){
                filelength = (int) file1.length();
                }

                //Getting date and time of response
                Date get_date = new Date();  
                SimpleDateFormat response_datetime = new SimpleDateFormat("EEE, d MMM yyyy, hh:mm:ss z");
                String datetime = response_datetime.format(get_date);

                System.out.print("\nSending response headers:\n");
                System.out.print("HTTP/1.0 200 OK\r\n"+"Date: "+ datetime+"\r\n"+"Content-length: "+ filelength +"\r\n"+
                "Content-type: "+content+"\r\n\r\n");
                out.print("HTTP/1.0 200 OK\r\n"+"Date: "+ datetime+"\r\n"+"Content-length: "+ filelength +"\r\n"+
                "Content-type: "+content+"\r\n\r\n");

                byte buffer[]=new byte[5000];
                int i;
                while ((i=f.read(buffer))>0)
                    out.write(buffer, 0, i);
                out.close();
                socket.close();
                in.close();
                System.out.println("connection closed");
        
        }
        catch (FileNotFoundException fnfe) 
        {
            out.print("HTTP/1.0 404 Not Found\r\n"+
                "Content-type: text/html\r\n\r\n"+
                "<html><head></head><body>HTTP/1.0 404 Not Found<br>"+file+" not found</body></html>\n");
            out.close();
            in.close();
            socket.close();
            System.out.println("connection closed");
        }  
    }
}