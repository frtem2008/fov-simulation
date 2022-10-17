// FIXME: 17.10.2022 TEST CLASS FOR SERVER COMMUNICATION

package Client;

import Online.TCPPhone;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Main {
    private final int PORT = 26780;
    private final ServerSocket serverSocket;
    private TCPPhone client;

    public Main() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server socket created");
        } catch (IOException e) {
            throw new RuntimeException("Unable to create a server" + e);
        }

        System.out.println("Client created");
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.begin();
    }

    private void begin() {
        System.out.println("Started");
        new Thread(this::server).start();
        new Thread(this::client).start();
    }

    private void server() {
        System.out.println("Server started on port: " + serverSocket.getLocalPort());

        while (true) {
            TCPPhone client = new TCPPhone(serverSocket);
            new Thread(() -> {
                System.out.println("[SERVER]A client with ip address: " + client.getIp() + " connected");
                System.out.println("[SERVER]Waiting for data...");
                while (true) {
                    try {
                        Object received = client.readObject();
                        System.out.println("[SERVER]Received a " + received.getClass());
                        System.out.println("[SERVER]Object is: " + received);
                        if (received instanceof ArrayList)
                            System.out.println("[SERVER]Received[0] is: " + ((ArrayList) received).get(0));
                        client.writeObject("Got object with hash: " + received.hashCode());
                        System.out.println("[SERVER]Object hash (" + received.hashCode() + ") has been sent to a client");
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void client() {
        client = new TCPPhone("127.0.0.1", PORT);
        System.out.println("[CLIENT]Connected to a server: " + client.getIp() + ":" + PORT);
        ArrayList<Integer> n = new ArrayList<>();
        n.add(0);
        n.set(0, 23);
        while (true) {
            System.out.println("___________________________________________________________");
            Object received;
            n.set(0, n.get(0) + 1);

            System.out.println("[CLIENT]Sending n: " + n.get(0) + " to server...");
            System.out.println("[CLIENT] hash is " + n.hashCode());
            try {
                client.writeObject(n.clone());
                System.out.println("[CLIENT]N has been sent");
                received = client.readObject();
                System.out.println("[CLIENT]Received answer from the server: ");
                System.out.println("[CLIENT]Received is " + received.getClass());
                int hash = -1;
                if (received instanceof String)
                    hash = Integer.parseInt(((String) received).replaceAll("Got object with hash: ", ""));
                System.out.println("[CLIENT]Received hash is " + hash);
                if (n.hashCode() == hash)
                    System.out.println("[CLIENT]Hash matches");

                Thread.sleep(2000);
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
