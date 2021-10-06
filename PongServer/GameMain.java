package PongServer;

//Server game --- player1
public class GameMain {
    public static void main(String []argv){
        /**get localHost ip
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String hostAddress = address.getHostAddress();//change to String
        System.out.println(hostAddress);
         */
        GameFrame game = new GameFrame("PongServer");
        boolean success = game.Initialize();

        ServerThread server = new ServerThread(game);
        server.go();

        if(success){
            game.runLoop();
        }
        game.ShutDown();
    }
}
