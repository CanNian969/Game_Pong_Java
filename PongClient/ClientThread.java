package PongClient;


import org.w3c.dom.CDATASection;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class ClientThread{
    public GameFrame gf;
    public ClientThread(){}
    public ClientThread(GameFrame gf){
        this.gf = gf;
    }

    public void go(){


        new Thread(new SendThread(gf)).start();
        try {
            DatagramSocket datagramSocket = new DatagramSocket(58888);
            byte[] buf = new byte[100];
            datagramSocket.receive(new DatagramPacket(buf,buf.length));
            datagramSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Thread(new ReceiveThread(gf)).start();

    }
}
class SendThread implements Runnable {
    GameFrame gf;
    public SendThread(GameFrame gf) {
        this.gf = gf;
    }

    @Override
    public void run(){
        DatagramSocket datagramSocket = null;
        DatagramPacket outdatapacket;
        InetAddress serverAddress;
        byte[] p1Order = new byte[200];

        while (true){
            try {
                datagramSocket = new DatagramSocket();
                //change localHost to Player1's
                //serverAddress = InetAddress.getLocalHost();
                serverAddress = InetAddress.getByName("10.118.12.212");

                //send player1 Order(byte[])
                gf.player2Order[0] = (byte) gf.player2;
                outdatapacket = new DatagramPacket(gf.player2Order, gf.player2, serverAddress, 57777);
                gf.player2 = 1;//clear player2byte.length

                datagramSocket.send(outdatapacket);
            }catch (Exception e) {
                e.printStackTrace();
            }

            datagramSocket.close();
        }

    }
}

class ReceiveThread implements Runnable{
    GameFrame gf;
    public ReceiveThread(GameFrame gf) {
        this.gf = gf;
    }

    @Override
    public void run(){
        DatagramSocket datagramSocket = null;
        DatagramPacket indatapacket = null;
        byte[] p1Order = new byte[200];

        try {
            datagramSocket = new DatagramSocket(58888);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        while (true){
            try {
                //Thread.sleep(5);
                indatapacket = new DatagramPacket(p1Order, p1Order.length);
                datagramSocket.receive(indatapacket);
                //System.out.println("Connected");

                //datagramSocket.close();
            }catch (Exception e) {
                e.printStackTrace();
            }

            /**The Order : ball x  -->  ball y --> paddle y*/
            //movment of ball
            byte[] mov = indatapacket.getData();

            String movtion = new String(mov,0,50);
            String x_ball = movtion.substring(0, 6);
            String y_ball = movtion.substring(10, 16);
            String win_p = movtion.substring(49,50);

            //Receive Game-Result
            int winp = Integer.parseInt(win_p);
            if(winp != 0){
                //System.out.println("Lose");
                gf.win_player = winp;
                gf.mIsRunning = false;
            }
            float xball = Float.parseFloat(x_ball);
            float yball = Float.parseFloat(y_ball);
            gf.mBallPos.x = xball;
            gf.mBallPos.y = yball;

            //movment of player1
            String y_paddle = movtion.substring(20, 25);
            float ypaddle = Float.parseFloat(y_paddle);
            gf.mPaddlePos[0].y = ypaddle;

            //movment of player1
//            for(int i = 1; i < mov[0]; i++){
//                System.out.print(mov[i]);
//                //8对应上  2对应下
//                if(mov[i] == 'W'){
//                    gf.mPaddlePos[0].y -= 10.0f;
//                    if(gf.mPaddlePos[0].y < gf.Thickness)
//                        gf.mPaddlePos[0].y = gf.Thickness;
//                }else if(mov[i] == 'S'){
//                    gf.mPaddlePos[0].y += 10.0f;
//                    if(gf.mPaddlePos[0].y > gf.getHeight() - gf.paddleH * 1.5f)
//                        gf.mPaddlePos[0].y = gf.getHeight() - gf.paddleH * 1.5f;
//                }
//            }
        }

    }
}