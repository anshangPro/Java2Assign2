package Handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GameMatcher {

    HashMap<UUID, ClientHandler> clientList;

    private GameMatcher() {
        clientList = new HashMap<>();
    }
    private static HashMap<UUID, GameHandler> games = new HashMap<>();

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
        instance.clientList.put(client.user.uuid, client);
        instance.clientList.forEach((uuid, clientHandler) -> {
            if (uuid != client.user.uuid) {
                getUserList(clientHandler);
            }
        });
    }

    public static void removeClient(ClientHandler client) {
        if (client.user != null) {
            instance.clientList.remove(client.user.uuid);
            instance.clientList.forEach((uuid, clientHandler) -> {
                if (uuid != client.user.uuid) {
                    getUserList(clientHandler);
                }
            });
        }
    }

    public static void getUserList(ClientHandler client) {
        client.printer.printf("list.%d\n", instance.clientList.size()-1);
        instance.clientList.forEach((key, value) -> {
            if (client != value) { // {name}.{uuid}
                client.printer.printf("%s.%s\n", value.name, key.toString());
            }
        });
        client.printer.flush();
    }

    public static void invite(String uuid, ClientHandler client) {
        UUID u = UUID.fromString(uuid);
        ClientHandler opposite = instance.clientList.get(u);
        opposite.isInvited(client.name, client.user.uuid.toString());
    }

    public static void accept(String uuid, ClientHandler client) {
        UUID u = UUID.fromString(uuid);
        ClientHandler opposite = instance.clientList.get(u);
//        opposite.isAccepted(client.user.uuid.toString());
        client.sendLine(String.format("start.%s.1", opposite.name));
        opposite.sendLine(String.format("start.%s.2", client.name));
        GameHandler game = new GameHandler(client, opposite);
        client.color = 1;
        opposite.color = 2;
        client.user.color = 1;
        opposite.user.color = 2;
        client.setGameHandler(game);
        opposite.setGameHandler(game);
        removeClient(client);
        removeClient(opposite);
        games.put(game.uuid, game);
    }

    public static void gameOver(GameHandler gameHandler) {
        games.remove(gameHandler.uuid);
    }

    public static boolean contains(UUID uuid) {
        return games.containsKey(uuid);
    }

    public static GameHandler getGame(UUID uuid) {
        return games.get(uuid);
    }

/*    @Override
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
    }*/
}
