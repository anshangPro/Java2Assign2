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
    }

    public static void removeClient(ClientHandler client) {
        instance.clientList.remove(client);
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (clientList.size() >= 2) {
                    ClientHandler a = clientList.remove(0);
                    ClientHandler b = clientList.remove(0);
                    a.sendLine(String.format("start.%s.1", b.name));
                    b.sendLine(String.format("start.%s.2", a.name));
                    GameHandler game = new GameHandler(a, b);
                    a.setGameHandler(game);
                    a.color = 1;
                    b.setGameHandler(game);
                    b.color = 2;
                } else {
                    if (clientList.size() == 1 && !clientList.get(0).informed) {
                        clientList.get(0).inform();
                    }
                    Thread.sleep(1000);
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
