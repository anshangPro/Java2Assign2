package Handler;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

import Serializable.User;

public class ClientHandler implements Runnable{

    private static final int CIRCLE = 1;
    private static final int LINE = 2;

    Socket socket;
    InputStream inputStream;
    OutputStream outputStream;
    BufferedReader reader;
    PrintStream printer;
    String name;
    User user;
    int color;
    boolean informed;

    public void setGameHandler(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    GameHandler gameHandler;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();
            this.reader = new BufferedReader(new InputStreamReader(inputStream));
            this.printer = new PrintStream(outputStream);
            this.informed = false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void read(String in) {
        if (in == null) return;
        String[] msg = in.split("\\.");
        switch (msg[0]) {
            case("play"): // play.{step}.{playerID}.{loc[0]}.{loc[1]}
                gameHandler.play(Integer.parseInt(msg[2]), Integer.parseInt(msg[3]),
                        Integer.parseInt(msg[4]), color);
                break;
            case("Hello"):
                sendLine("Hi there");
                break;
            case("login"):
                if (login(msg[1], msg[2])) {
                    sendLine(String.format("login.success.%s", msg[1]));
                    GameMatcher.addClient(this);
                    this.name = msg[1];
                } else sendLine("login.fail");
                break;
            case("register"):
                if (register(msg[1], msg[2]))
                    sendLine("register.success");
                else sendLine("register.fail");
                break;
        }
    }

    private boolean login(String name, String passwd){
        try {
            String fileName = String.format("data/%s.o", name);
            File file = new File(fileName);
            if (file.exists()) {
                ObjectInput in = new ObjectInputStream(Files.newInputStream(file.toPath()));
                User u = (User) in.readObject();
                if (u.passwd.equals(passwd)) {
                    user = u;
                    return true;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private boolean register(String name, String passwd) {
        try {
            String fileName = String.format("data/%s.o", name);
            File file = new File(fileName);
            if (!file.exists()) {
                User u = new User();
                u.passwd = passwd;
                u.name = name;
                ObjectOutput out = new ObjectOutputStream(Files.newOutputStream(file.toPath()));
                out.writeObject(u);
                out.close();
                return true;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    protected void inform() {
        this.informed = true;
        sendLine("wait");
    }

    protected void send(String out) {
        printer.print(out);
        printer.flush();
    }

    protected void sendLine(String out) {
        printer.println(out);
        printer.flush();
    }

    @Override
    public void run() {
        try {
            while (true) {
                String in = reader.readLine();
                if (in == null) throw new IOException();
//                System.out.println(in);
                read(in);
            }
        } catch (IOException e) {
            if (gameHandler != null)  gameHandler.exit(color);
            GameMatcher.removeClient(this);
            System.out.printf("Player: %s-%d exited\n", name, color);
        }
    }
}
