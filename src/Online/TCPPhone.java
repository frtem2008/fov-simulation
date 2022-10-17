// FIXME: 17.10.2022 CLASS FOR EASIER ONLINE COMMUNICATION
package Online;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class TCPPhone implements Closeable {
    private final Socket socket; //socket
    //reader and writer
    private final ObjectOutputStream objectWriter;
    private final ObjectInputStream objectReader;

    //is closed (for proper resource clearing)
    public boolean closed = false;

    //client constructor
    public TCPPhone(String ip, int port) {
        try {
            this.socket = new Socket(ip, port);
            this.objectWriter = new ObjectOutputStream(socket.getOutputStream());//reader creation
            this.objectReader = new ObjectInputStream(socket.getInputStream());//writer creation
            objectWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //server constructor
    public TCPPhone(ServerSocket server) {
        try {
            this.socket = server.accept();//waiting for client
            this.objectWriter = new ObjectOutputStream(socket.getOutputStream());//writer creation
            this.objectReader = new ObjectInputStream(socket.getInputStream());//reader creation
            objectWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    //getting ip address through socket
    public String getIp() {
        return socket.getInetAddress().getHostAddress();
    }

    //reading a string
    public String readLine() throws IOException, ClassNotFoundException {
        if (!closed) {
            Object received = objectReader.readObject();
            if (received instanceof String)
                return (String) received;
            throw new ClassCastException("Received object isn't a string");
        } else
            throw new SocketException("Socket closed");
    }

    public void writeObject(Object o) throws IOException {
        if (!closed) {
            objectWriter.writeObject(o);
            objectWriter.flush();
        } else
            throw new SocketException("Socket closed");
    }

    public Object readObject() throws IOException, ClassNotFoundException {
        if (!closed) {
            return objectReader.readObject();
        } else
            throw new SocketException("Socket closed");
    }

    //for try-catch with resources
    @Override
    public void close() throws IOException {
        objectReader.close();
        objectWriter.close();
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
}