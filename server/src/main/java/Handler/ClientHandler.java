package Handler;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

import Serializable.User;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;

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
                    sendLine(String.format("login.success.%s.%d.%d", msg[1], user.winCnt, user.totalCnt)); // login.success.{name}.{winCnt}.{totalCnt}
                    this.name = msg[1];
                } else sendLine("login.fail");
                break;
            case("register"):
                if (register(msg[1], msg[2]))
                    sendLine("register.success");
                else sendLine("register.fail");
                break;
            case("start"):
                GameMatcher.addClient(this);
                break;
            case("getList"):
                GameMatcher.getUserList(this);
                break;
            case("invite"):
                GameMatcher.invite(msg[1], this);
                break;
            case("accept"):
                GameMatcher.accept(msg[1], this);
                break;
        }
    }

    public void isInvited(String name, String uuid) {
        printer.printf("invite.%s.%s\n", name, uuid);
        printer.flush();
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
                file.createNewFile();
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

    public void save() {
        try {
            String fileName = String.format("data/%s.o", name);
            File file = new File(fileName);
            ObjectOutput out = new ObjectOutputStream(Files.newOutputStream(file.toPath()));
            out.writeObject(user);
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String in = reader.readLine();
                if (socket.isClosed() || !socket.isConnected()) {
                    throw new IOException();
                }
                if (in == null) {
                     throw new IOException();
                }
//                System.out.println(in);
                read(in);
            }
        } catch (IOException e) {
            if (gameHandler != null)  gameHandler.exit(color);
            GameMatcher.removeClient(this);
            save();
            System.out.printf("Player: %s-%d exited\n", name, color);
        }
    }
}
