package PongServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ServerThread{
    GameFrame gf;
    InetAddress clientaddress;
    final int CLIENTPORT = 58888;

    public ServerThread(){}
    public ServerThread(GameFrame gf){
        this.gf = gf;
    }

    public void go(){
        DatagramSocket datagramSocket = null;
        DatagramPacket indatapacket = null;
        byte[] p2Order = new byte[200];

        //第一次获取P2的IP
        try {
            datagramSocket = new DatagramSocket(57777);
            indatapacket = new DatagramPacket(p2Order, p2Order.length);

            //receive packet form client
            System.out.println("Connecting");
            datagramSocket.receive(indatapacket);
            System.out.println("Connected");
            //System.out.println(indatapacket.getAddress() + " " + indatapacket.getPort());

        } catch (IOException e) {
                e.printStackTrace();
        }

        clientaddress = indatapacket.getAddress();

        System.out.println(CLIENTPORT + " " + clientaddress);
        datagramSocket.close();

        new Thread(new SendThread(gf, clientaddress, CLIENTPORT)).start();
        new Thread(new ReceiveThread(gf)).start();
    }
}

class SendThread implements Runnable {
    GameFrame gf;
    InetAddress clientaddress;
    int clientport;
    public SendThread(GameFrame gf, InetAddress add, int port) {
        this.gf = gf;
        this.clientaddress = add;
        this.clientport = port;
    }

    @Override
    public void run() {
        DatagramSocket datagramSocket = null;
        DatagramPacket outdatapacket;

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                //Thread.sleep(5);
                datagramSocket = new DatagramSocket();
                //send player2 packet (byte[])
                //gf.player1Order[0] = (byte) gf.player1;
                outdatapacket = new DatagramPacket(gf.player1Packet,
                        gf.player1Packet.length, clientaddress, clientport);
                //gf.player1 = 1;//clear player1byte.length
                datagramSocket.send(outdatapacket);

            } catch (Exception e) {
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
        byte[] p2Order = new byte[200];

        try {
            datagramSocket = new DatagramSocket(57777);
        } catch (SocketException e) {
            e.printStackTrace();
        }


        while (true){
            byte[] mov = new byte[200];
            try {
                indatapacket = new DatagramPacket(p2Order, p2Order.length);
                //receive packet form client
                datagramSocket.receive(indatapacket);
                /**everytime's clientport is different!!!*/
                //System.out.print( "***" + indatapacket.getPort() + "*" + indatapacket.getAddress());

                mov = indatapacket.getData();
                //System.out.print(" " + mov[0] + " ");

            } catch (Exception e) {
                e.printStackTrace();
            }

            //movment of player2    ------ Parse DataPacket
            for(int i = 1; i < mov[0]; i++){
                //System.out.print(mov[i] + " ");
                //8对应上  2对应下
                if(mov[i] == '8'){
                    gf.mPaddlePos[1].y -= 10.0f;
                    if(gf.mPaddlePos[1].y < gf.Thickness)
                        gf.mPaddlePos[1].y = gf.Thickness;
                }else if(mov[i] == '2'){
                    gf.mPaddlePos[1].y += 10.0f;
                    if(gf.mPaddlePos[1].y > gf.getHeight() - gf.paddleH * 1.5f)
                        gf.mPaddlePos[1].y = gf.getHeight() - gf.paddleH * 1.5f;
                }
            }

        }

    }
}