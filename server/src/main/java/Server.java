import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

    private Server(){

    }

    private volatile static Server instance;

    public static Server getInstance(){
        if (instance == null) {
            synchronized (Server.class){
                instance = new Server();
            }
        }
        return Server.instance;
    }

    private static final int port = 11451;

    private boolean running = true;

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        try (ServerSocket server = new ServerSocket(port)) {
            while (running) {
                Socket socket = server.accept();
                System.out.printf("Client connected at port: %d\n", socket.getLocalPort());
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
