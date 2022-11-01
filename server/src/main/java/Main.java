

public class Main {
    private static final int THREAD = 2;

    public static void main(String[] args) {
        Server server = Server.getInstance();
        Thread serverThread = new Thread(server);
        serverThread.start();

    }
}
