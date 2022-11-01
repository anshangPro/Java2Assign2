package application.controller;

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
            printer.printf("Hello.%s\n", name);
            printer.flush();
            if (!reader.readLine().equals("Hi there")) {
                stop();
            }
        } catch (IOException e) {
            System.out.println("can not establish connection with server");
        }
    }

    public void stop() {
        running = false;
        try {
            this.inputStream.close();
            this.outputStream.close();
            this.reader.close();
            this.printer.close();
            this.socket.close();
        } catch (IOException ignored) {}
    }

    private void resolve(String in) {
        String[] msg = in.split("\\.");
        switch (msg[0]) {
            case("play"): // play.{step}.{playerID}.{loc[0]}.{loc[1]}
                controller.refreshBoard(msg[2].equals("1"), Integer.parseInt(msg[3]),
                        Integer.parseInt(msg[4]));
                break;
            case("start"): //start.{opposite}.{color}
                this.selfColor = Integer.parseInt(msg[2]);
                this.oppositeName = msg[1];
                this.started = true;
                controller.initial(selfColor == CIRCLE);
                System.out.printf("Connected. self: %s, opposite: %s\n", this.selfColor, this.oppositeName);
                break;
        }
    }

    public void play(boolean turn, int x, int y) {
        printer.printf("play.%d.%d.%d.%d\n", step++, (turn ? CIRCLE : LINE), x, y);
        printer.flush();
    }

    @Override
    public void run() {
        System.out.println("Waiting for connection");
        try {
            while (running) {
                String in = reader.readLine();
                System.out.println(in);
                resolve(in);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
