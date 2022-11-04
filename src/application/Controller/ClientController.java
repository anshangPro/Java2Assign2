package application.Controller;

import application.View.AlertWindow;

import java.io.*;
import java.net.Socket;

public class ClientController implements Runnable {

    private static final int CIRCLE = 1;
    private static final int LINE = 2;

    String name;
    String oppositeName;
    int selfColor;
    int step;
    Socket socket;
    InputStream inputStream;
    OutputStream outputStream;
    BufferedReader reader;
    PrintStream printer;
    private boolean running;

    public boolean isStarted() {
        return started;
    }

    private boolean started;
    private Controller controller;

    public ClientController(String name, Controller controller) {
        running = true;
        started = false;
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
            running = false;
        }
    }

    public boolean login(String name, String passwd) {
        printer.printf("login.%s.%s\n", name, passwd);
        printer.flush();
        return true;
    }

    public boolean register(String name, String passwd) {
        printer.printf("register.%s.%s\n", name, passwd);
        printer.flush();
        return true;
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
                controller.initial(selfColor == CIRCLE);
                System.out.printf("Connected. self: %s, opposite: %s\n", this.selfColor, this.oppositeName);
                AlertWindow.show(String.format("Connected. self: %s, opposite: %s\n", this.selfColor, this.oppositeName));
                break;
            case ("success"): //success.{playerID}
//                System.out.printf("Player: %s win\n", msg[1]);
                AlertWindow.show(String.format("Player: %s win", msg[1]));
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
                break;
            case ("login"):
                if (msg[1].equals("success")){
                    this.name = msg[2];
                    //TODO: inform close;
                } else{
                    //TODO: fail
                }
                break;
            case("register"):
                break;
        }
    }

    public void play(boolean turn, int x, int y) {
        printer.printf("play.%d.%d.%d.%d\n", step++, (turn ? CIRCLE : LINE), x, y);
        printer.flush();
    }

    @Override
    public void run() {
        try {
            while (running) {
                String in = reader.readLine();
//                System.out.println(in);
                resolve(in);
            }
        } catch (IOException e) {
            System.out.println("socket closed");
        }
    }
}
