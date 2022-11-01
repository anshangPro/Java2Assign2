import Handler.GameHandler;
import Handler.GameMatcher;

public class Main {
    private static final int THREAD = 2;

    public static void main(String[] args) {
        Server server = Server.getInstance();
        GameMatcher gameMatcher = GameMatcher.getInstance();
        Thread serverThread = new Thread(server);
        Thread gameMatcherThread = new Thread(gameMatcher);
        serverThread.start();
        gameMatcherThread.start();
    }
}
