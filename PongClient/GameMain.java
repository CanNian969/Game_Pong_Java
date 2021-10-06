package PongClient;

//Client Game  -- player2
public class GameMain {
    public static void main(String []argv){

        GameFrame game = new GameFrame("PongClient");
        boolean success = game.Initialize();

        ClientThread client = new ClientThread(game);
        client.go();

        if(success){
            game.runLoop();
        }
        game.ShutDown();
    }
}
