package cz.zcu.students.kiwi.network.adapter.tcp;

import cz.zcu.students.kiwi.network.adapter.socket.SocketFactory;
import sun.security.ssl.SSLContextImpl;
import sun.security.ssl.SSLSocketImpl;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public final class TcpConnection {

    public static SocketFactory socketFactory = new SocketFactory();

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    private final InetSocketAddress sockAddr;
    private InetAddress address;

    public TcpConnection(String hostname, int port) throws UnknownHostException {
        this.address = InetAddress.getByName(hostname);
        this.sockAddr = new InetSocketAddress(address, port);
    }

    public boolean isOpen() {
        return this.socket != null && this.socket.isConnected();
    }

    public void open(int timeout) throws IOException{
        this.socket = socketFactory.create(sockAddr, timeout);

        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void close() throws IOException {
        if (!isOpen()) {
            return;
        }
        this.socket.close();
        this.socket = null;
        this.writer = null;
        this.reader = null;
    }

    synchronized public void send(String message) throws IOException {
        if (writer == null) {
            throw new IOException("Failed to send message: TcpConnection is not open");
        }

        if (message.charAt(message.length() - 1) != '\n') {
            message += '\n';
        }

        this.writer.write(message);
        this.writer.flush();
    }

    public String receive() throws IOException {
        if (reader == null) {
            throw new IOException("Failed to receive message: TcpConnection is not open");
        }
        String message = reader.readLine();
        if (message == null) {
            return null;
        }


        return message.trim();
    }
}
