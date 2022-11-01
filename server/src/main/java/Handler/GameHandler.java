package Handler;

public class GameHandler {
    private ClientHandler a;
    private ClientHandler b;
    private int step;


    public GameHandler(ClientHandler a, ClientHandler b){
        this.a = a;
        this.b = b;
        step = 0;
    }

    public void play(int player, int x, int y, int from) {
        if (from == 1) {
            b.send(String.format("play.%d.%d.%d.%d\n", step, player, x, y));
        } else {
            a.send(String.format("play.%d.%d.%d.%d\n", step, player, x, y));
        }
        step++;
    }

}
