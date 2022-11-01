package Handler;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable{
    Socket socket;
    InputStream inputStream;
    OutputStream outputStream;
    BufferedReader reader;
    PrintStream printer;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();
            this.reader = new BufferedReader(new InputStreamReader(inputStream));
            this.printer = new PrintStream(outputStream);
            GameMatcher.addClient(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void read() {

    }

    @Override
    public void run() {
        while (true) {
            read();
        }
    }
}
