package application.Controller;

import application.Main;
import application.View.*;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class ClientController implements Runnable {

    private static final int CIRCLE = 1;
    private static final int LINE = 2;

    public String name;
    public String oppositeName;
    int selfColor;
    int step;
    Socket socket;
    InputStream inputStream;
    OutputStream outputStream;
    BufferedReader reader;
    PrintStream printer;
    private boolean running;
    public boolean finished;


    public int winCnt = 0;
    public int totalCnt = 0;

    public boolean isStarted() {
        return started;
    }

    private boolean started;
    private Controller controller;

    public ClientController(String name, Controller controller) {
        running = true;
        started = false;
        finished = false;
        try {
            this.name = name;
            this.controller = controller;
            this.socket = new Socket("localhost", 11451);
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();
            this.reader = new BufferedReader(new InputStreamReader(inputStream));
            this.printer = new PrintStream(outputStream);
            printer.println("Hello");
            printer.flush();
            if (!reader.readLine().equals("Hi there")) {
                stop();
            }
        } catch (IOException e) {
            System.out.println("can not establish connection with server");
            AlertWindow.show("can not establish connection with server");
            AlertWindow.setOnExit((event) -> {
                AlertWindow.close();
                this.finished = true;
                LoginWindow.close();
            });
            running = false;
        }
    }

    public void login(String name, String passwd) {
        printer.printf("login.%s.%s\n", name, passwd);
        printer.flush();
    }

    public void register(String name, String passwd) {
        printer.printf("register.%s.%s\n", name, passwd);
        printer.flush();
    }

    public void stop() {
        running = false;
        try {
            this.inputStream.close();
            this.outputStream.close();
            this.reader.close();
            this.printer.close();
            this.socket.close();
        } catch (IOException | NullPointerException ignored) {
        }
    }

    private void resolve(String in) {
        String[] msg = in.split("\\.");
        switch (msg[0]) {
            case ("play"): // play.{step}.{playerID}.{loc[0]}.{loc[1]}
                controller.refreshBoard(msg[2].equals("1"), Integer.parseInt(msg[3]),
                        Integer.parseInt(msg[4]));
                break;
            case ("start"): //start.{opposite}.{color}
                this.selfColor = Integer.parseInt(msg[2]);
                this.oppositeName = msg[1];
                this.started = true;
                controller.initial(false, new boolean[3][3], new int[3][3]);
                System.out.printf("Connected. self: %s, opposite: %s\n", this.selfColor, this.oppositeName);
                AlertWindow.show(String.format("Connected. self: %s, opposite: %s\n", this.selfColor, this.oppositeName));
                UserListWindow.close();
                Main.showGame();
                break;
            case ("success"): //success.{playerID}
//                System.out.printf("Player: %s win\n", msg[1]);
                finished = true;
                if (Integer.parseInt(msg[1]) == selfColor) {
                    AlertWindow.show(String.format("Player: %s win\nYou win!!", name));
                    winCnt++;
                } else {
                    AlertWindow.show(String.format("Player: %s win\nYou lose.", oppositeName));
                }
                totalCnt++;
                restart();
                break;
            case ("exit"):
//                System.out.println("opposite exited");
                AlertWindow.show("opposite exited");
                break;
            case ("wait"):
//                System.out.println("waiting for opposite");
                AlertWindow.show("Waiting for opposite");
                break;
            case ("tie"):
                AlertWindow.show("Tie");
                finished = true;
                totalCnt++;
                restart();
                break;
            case ("login"):
                if (msg[1].equals("success")){
                    this.name = msg[2];
                    this.winCnt = Integer.parseInt(msg[3]);
                    this.totalCnt = Integer.parseInt(msg[4]);
                    LoginWindow.close();
                    StartWindow.show(this);
                    if (msg[5].equals("1")) {
                        ResumeWindow.show(this);
                    }
                } else{
                    AlertWindow.show("Login failed");
                }
                break;
            case("register"):
                if (msg[1].equals("success")){
                    AlertWindow.show("Register success, please login");
                } else{
                    AlertWindow.show("Register failed");
                }
                break;
            case("list"):
                receiveList(Integer.parseInt(msg[1]));
                break;
            case("invite"):
                InviteWindow.show(this, msg[1], msg[2]);
                break;
            case("resume"):
                this.started = true;
                Main.showGame();
                StartWindow.close();
                loadData();
                break;
            case("reconnect"):
                AlertWindow.show("opposite reconnected");
                System.out.println("re");
                break;
        }
    }

    public void loadData() {
        try {
            boolean turn = reader.readLine().equals("1");
            selfColor = Integer.parseInt(reader.readLine());
            oppositeName = reader.readLine();
            int[][] board = new int[3][3];
            boolean[][] flag = new boolean[3][3];
            int cnt = 0;
            String[] b = reader.readLine().split(" ");
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    board[i][j] = Integer.parseInt(b[cnt++]);
                    flag[i][j] = board[i][j] != 0;
                }
            }
            Platform.runLater(() -> controller.initial(turn, flag, board));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void resume() {
        printer.println("resume");
        printer.flush();
    }

    public void play(boolean turn, int x, int y) {
        printer.printf("play.%d.%d.%d.%d\n", step++, (turn ? CIRCLE : LINE), x, y);
        printer.flush();
    }

    public void start() {
        printer.println("start");
        printer.flush();
    }

    long time = 0;

    public void getList() {
        if (time == 0 || System.currentTimeMillis() - time > 1500) {
            time = System.currentTimeMillis();
            this.printer.println("getList");
            this.printer.flush();
        }
    }

    private void receiveList(int n) {
        HashMap<String, String> users = new HashMap<>();
        try {
            for (int i = 0; i < n; i++) {
                String entry = reader.readLine();
                String[] entries = entry.split("\\."); // {name}.{uuid}
                users.put(entries[0], entries[1]);
            }
        } catch (IOException e) {
            System.out.println("socket error, receive list failed");
        }
        UserListWindow.setList(users);
    }

    public void invite(String opposite) {
        printer.printf("invite.%s\n", opposite);
        printer.flush();
    }

    public void accept(String invitorUUID) {
        printer.printf("accept.%s\n", invitorUUID);
        printer.flush();
    }

    public void reject(String invitorUUID) {
        printer.printf("reject.%s\n", invitorUUID);
        printer.flush();
    }

    @Override
    public void run() {
        try {
            while (running) {
                String in = reader.readLine();
//                System.out.println(in);
                if (in == null) {
                    throw new IOException();
                }
                resolve(in);
            }
        } catch (IOException e) {
            AlertWindow.show("Disconnected from server");
            AlertWindow.setOnExit(event -> {
                AlertWindow.close();
                this.running = false;
                InviteWindow.close();
                LoginWindow.close();
                ResumeWindow.close();
                StartWindow.close();
                UserListWindow.close();
            });
            System.out.println("socket closed");
        }
    }

    private void restart() {
        AlertWindow.setOnExit(event -> {
            Main.close();
            StartWindow.show(this);
            finished = false;
        });
    }
}
