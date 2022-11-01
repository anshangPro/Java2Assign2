package Handler;

import java.util.ArrayList;
import java.util.List;

public class GameMatcher implements Runnable {

    List<ClientHandler> clientList;

    private GameMatcher() {
        clientList = new ArrayList<>();
    }

    private volatile static GameMatcher instance;

    public static GameMatcher getInstance() {
        if (instance == null) {
            synchronized (GameMatcher.class) {
                instance = new GameMatcher();
            }
        }
        return GameMatcher.instance;
    }

    public static void addClient(ClientHandler client) {
        instance.clientList.add(client);
        instance.notify();
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (clientList.size() >= 2) {
                    ClientHandler a = clientList.remove(0);
                    ClientHandler b = clientList.remove(0);
                    GameHandler game = new GameHandler(a, b);
                } else {
                    wait();
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
