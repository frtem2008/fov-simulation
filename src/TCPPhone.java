// FIXME: 17.10.2022 CLASS FOR EASIER ONLINE COMMUNICATION

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class TCPPhone implements Closeable {
    private final Socket socket; //socket
    //reader and writer
    private final DataOutputStream writer;
    private final DataInputStream reader;

    //is closed (for proper resource clearing)
    public boolean closed = false;
    private int port = -1;

    //client constructor
    public TCPPhone(String ip, int port) {
        try {
            this.socket = new Socket(ip, port);
            this.writer = new DataOutputStream(socket.getOutputStream());//writer creation
            this.reader = new DataInputStream(socket.getInputStream());//reader creation
            this.port = port;
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //startCommunication constructor
    public TCPPhone(ServerSocket server) {
        try {
            this.socket = server.accept();//waiting for client
            this.writer = new DataOutputStream(socket.getOutputStream());//writer creation
            this.reader = new DataInputStream(socket.getInputStream());//reader creation
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getPort() {
        return port;
    }

    //getting ip address through socket
    public String getIp() {
        return socket.getInetAddress().getHostAddress();
    }

    //reading bytes to dest array, returns number of bytes read
    public int readBytes(byte[] dest) throws IOException {
        if (!closed) {
            return reader.read(dest);
        } else
            throw new SocketException("Socket closed");
    }

    //writing bytes
    public void writeBytes(byte[] bytes) throws IOException {
        if (!closed) {
            writer.write(bytes);
            writer.flush();
        } else
            throw new SocketException("Socket closed");
    }

    //for try-catch with resources
    @Override
    public void close() throws IOException {
        reader.close();
        writer.close();
        socket.close();
    }


    //equals by ip address
    public boolean equals(Object x) {
        if (x == null || x.getClass() != this.getClass())
            return false;
        if (x == this)
            return true;
        TCPPhone cur = (TCPPhone) x;
        return cur.socket == this.socket &&
                cur.getIp().equals(this.getIp());
    }

    @Override
    public String toString() {
        return "TCPPhone{" +
                "ip=" + getIp() +
                ", closed=" + closed +
                '}';
    }
}