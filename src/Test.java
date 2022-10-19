import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Test {

    //parent constructor
    public Test(String servAddress, int servPort, int thisPort){
        //split server and client into different threads (maybe?)
        //maybe split threads in child constructors?
    }

    //constructor for client process
    public Test(String servAddress, int servPort){
        Socket socket;
        BufferedReader input;
        DataOutputStream output;
        String line;

        try{
            //connect to server
            socket = new Socket(servAddress, servPort);
            System.out.println("Connected to server " + servAddress + " at port "+ servPort);

            //write to server
            output = new DataOutputStream(socket.getOutputStream());

            output.writeBytes("Hello from the client");
            output.flush();

            //get input from server
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            line = input.readLine();
            System.out.println(line);

            //close connection
            output.close();
            socket.close();

        } catch(IOException ue){
            ue.printStackTrace();
        }
    }

    //constructor for server process
    public Test(int thisPort){
        Socket socket;
        ServerSocket server;
        BufferedReader input;
        DataOutputStream output;
        String line;

        try{
            //start server
            server = new ServerSocket(thisPort);
            System.out.println("Server started");

            //wait for connection
            socket = server.accept();
            System.out.println("Client accepted");

            //get input from client
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            line = input.readLine();
            System.out.println(line);

            //write to client
            output = new DataOutputStream(socket.getOutputStream());
            output.writeBytes("Hello from the server");
            output.flush();

            //close connection
            System.out.println("Closing connection");
            input.close();
            output.close();
            socket.close();
            server.close();

        }catch(IOException io){
            //print exception
            io.printStackTrace();
        }
    }

    public static void main(String[] args){
        //read in files
        Scanner scanLine;
        Scanner scanWord;

        //read in Common.cfg
        //read each line
        scanLine = new Scanner("Common.cfg");
        while(scanLine.hasNextLine()){
            //read each word in line
            scanWord = new Scanner(scanLine.nextLine());
            while(scanWord.hasNext()){
                //do stuff;  maybe store in object and put in structure?
                System.out.println(scanWord.next());
            }
            scanWord.close();
        }

        //close line scanner
        scanLine.close();

        //read in PeerInfo.cfg
        //read each line
        scanLine = new Scanner("PeerInfo.cfg");
        while(scanLine.hasNextLine()){
            //read each word in line
            scanWord = new Scanner(scanLine.nextLine());
            while(scanWord.hasNext()){
                //do stuff; maybe store in object and put in structure?
                System.out.println(scanWord.next());
            }
        }

        //split into threads (maybe?)
    }
}
