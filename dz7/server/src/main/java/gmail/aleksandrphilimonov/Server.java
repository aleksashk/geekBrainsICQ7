package gmail.aleksandrphilimonov;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private int port;
    private List<ClientHandler> clients;

    public List<ClientHandler> getClients() {
        return clients;
    }

    public Server(int port) {
        this.port = port;
        clients = new ArrayList<>();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту " + port);

            while (true) {
                System.out.println("Ожидание нового клиента");
                Socket socket = serverSocket.accept();
                System.out.println("Новый клиент подключился");
                new ClientHandler(this, socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public void sendMessage(String message, ClientHandler clientHandler) throws IOException {
            clientHandler.sendMessage(message);
    }

    public void broadCastMessage(String message) throws IOException {
        for (ClientHandler clientHandler : clients) {
            clientHandler.sendMessage(message);
        }
    }
    public boolean isNickBusy(String userName){
        for (ClientHandler clientHandler : clients) {
            if(clientHandler.getUserName().equals(userName)){
                return true;
            }
        }
        return false;
    }
}


























