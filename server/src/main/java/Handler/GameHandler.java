package Handler;

import java.util.UUID;

public class GameHandler {

    private static final int CIRCLE = 1;
    private static final int LINE = 2;
    private static final int BLANK = 0;
    private ClientHandler a;
    private ClientHandler b;
    private int step;

    private int[][] board = new int[3][3];
    private boolean turn;
    public UUID uuid = UUID.randomUUID();
    public boolean exited = false;
    private boolean finished = false;


    public GameHandler(ClientHandler a, ClientHandler b){
        this.a = a;
        this.b = b;
        step = 0;
        turn = false;
    }

    public void play(int player, int x, int y, int from) {
        board[x][y] = player;
        if (from == 1) {
            b.send(String.format("play.%d.%d.%d.%d\n", step, player, x, y));
        } else {
            a.send(String.format("play.%d.%d.%d.%d\n", step, player, x, y));
        }
        step++;
        if (judge(player)) {
            a.sendLine(String.format("success.%d", player));
            b.sendLine(String.format("success.%d", player));
            if (player == 1) {
                a.user.winCnt++;
                a.user.totalCnt++;
                b.user.totalCnt++;
            } else {
                b.user.winCnt++;
                b.user.totalCnt++;
                a.user.totalCnt++;
            }
            GameMatcher.gameOver(this);
            finished = true;
        } else if (step == 9) {
            a.sendLine("tie");
            b.sendLine("tie");
            a.user.totalCnt++;
            b.user.totalCnt++;
            GameMatcher.gameOver(this);
            finished = true;
        }
        turn = !turn;
    }

    public void exit(int player) {
        if (!exited && !finished){
            exited = true;
            if (player == 1) {
                a.user.save = uuid;
                b.sendLine("exit");
            } else {
                b.user.save = uuid;
                a.sendLine("exit");
            }
        } else GameMatcher.gameOver(this);
    }

    public void resume(ClientHandler client) {
        exited = false;
        String opposite;
        if (client.user.color == 1) {
            a = client;
            b.printer.println("reconnect");
            b.printer.flush();
            opposite = b.name;
        } else {
            b = client;
            a.printer.println("reconnect");
            a.printer.flush();
            opposite = a.name;
        }
        client.printer.println("resume");
        client.printer.println(turn ? "1" : "0");
        client.printer.println(client.user.color);
        client.printer.println(opposite);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                client.printer.print(board[i][j]);
                client.printer.print(' ');
            }
        }
        client.printer.println();
        client.printer.flush();
    }

    public boolean judge(int player) {
        int cnt_c = 0;
        int cnt_d = 0;
        for (int i = 0; i < 3 ; i++) {
            int cnt_a = 0;
            int cnt_b = 0;
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == player) {
                    cnt_a++;
                }
                if (board[j][i] == player) {
                    cnt_b++;
                }
            }
            if (board[i][i] == player) {
                cnt_c++;
            }
            if (board[3-i-1][i] == player) {
                cnt_d++;
            }
            if (cnt_a == 3 || cnt_b == 3) {
                return true;
            }
        }
        if (cnt_c == 3 || cnt_d == 3) {
            return true;
        }
        return false;
    }

}
