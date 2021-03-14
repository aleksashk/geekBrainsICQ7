package gmail.aleksandrphilimonov;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private String userName;
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public String getUserName() {
        return userName;
    }

    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

        Thread t = new Thread(() -> {
            try {
                while (true) {
                    String msg = in.readUTF();
                    if (msg.startsWith("/login ")) {
                        String userNameFromLogin = msg.split("\\s")[1];
                        if (server.isNickBusy(userNameFromLogin)) {
                            sendMessage("login_failed. Current nickname is already used");
                            continue;
                        }
                        userName = msg.split("\\s")[1];
                        sendMessage("/login_ok " + userName);
                        server.subscribe(this);
                        break;
                    }
                }
                while (true) {
                    String msg = in.readUTF();

                    if (msg.equals("/who_am_i") && !userName.isEmpty()) {
                        sendMessage("YOUR NAME IS: " + userName);
                    } else if (msg.startsWith("/w ")) {
                        String name = msg.split(" ",3)[1];
                        msg = userName + ": " + msg.split(" ",3)[2];
                        for (ClientHandler cl : server.getClients()) {
                            if (cl.userName.equals(name)) {
                                server.sendMessage(msg, cl);
                            }
                        }
                    } else if (msg.equals("/exit")) {
                        disconnect();
                    } else {
                        server.broadCastMessage(userName + ": " + msg);
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconnect();
            }
        });
        t.start();
    }

    public void disconnect() {
        server.unsubscribe(this);
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String msg) throws IOException {
        out.writeUTF(msg);
    }
}












