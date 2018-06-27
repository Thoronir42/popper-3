package cz.zcu.students.kiwi.popApp.network.adapter.socket;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;

public class SocketFactory {

    public Socket create(SocketAddress server, int timeout) throws IOException {
        Socket socket = new Socket();
        socket.connect(server, timeout);
        return socket;
    }
}
