package PongServer;


import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class GameFrame extends Frame implements KeyListener{
    final int Thickness = 10;
    final float paddleH = 100.0f;
    private Panel mPanel;

    //每个数据长度为10
    byte[] player1Packet; //记录player1的ball & paddle 's x &

    int win_player = 0; //1为p1 win  2为p2 win

    //Position of Paddle
    Vector2 []mPaddlePos = new Vector2[2];
    //Direction of Paddle
    int mPaddleDir; //not used
    //Position of Ball
    Vector2 mBallPos;
    Vector2 mBallVel;
    long mTicksCount;
    float deltaTime;
    private boolean mIsRunning;

    public GameFrame(String ss){
        super(ss);
        mPanel = null;

        mPaddleDir = 0;
        mTicksCount = 0;
        mIsRunning = true;

    };
    boolean Initialize(){
        this.setBounds(150,70,600,500);
        this.setResizable(false);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        mPanel = new MyPanel(this);
        mPanel.setBackground(Color.BLACK);
        this.add(mPanel);

        addKeyListener(this);

        player1Packet = new byte[200];

        mBallPos = new Vector2(getWidth() / 2 - Thickness / 2, getHeight() / 2 - Thickness / 2) ;
        mPaddlePos[0] = new Vector2(0, getHeight() / 2 - paddleH / 2);
        mPaddlePos[1] = new Vector2(getWidth() - Thickness * 2.5f, getHeight() / 2 - paddleH / 2);
        mBallVel = new Vector2(-0.175f, 0.225f);

        this.setFocusable(true);
        this.setVisible(true);
        return true;
    };
    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key){
            case KeyEvent.VK_ESCAPE:
                mIsRunning = false;
                break;
            case KeyEvent.VK_W:
                //player1Order[player1++] = 'W';
                mPaddlePos[0].y -= 10.0f;
                if(mPaddlePos[0].y < Thickness)
                    mPaddlePos[0].y = Thickness;
                break;
            case KeyEvent.VK_S:
                //player1Order[player1++] = 'S';
                mPaddlePos[0].y += 10.0f;
                if(mPaddlePos[0].y > getHeight() - paddleH * 1.5f)
                    mPaddlePos[0].y = getHeight() - paddleH * 1.5f;
                //System.out.println(Arrays.toString(player1Order) + "\n" + player1);
                break;
//            case KeyEvent.VK_UP:
//                mPaddlePos[1].y -= 10.0f;
//                if(mPaddlePos[1].y < Thickness)
//                    mPaddlePos[1].y = Thickness;
//                break;
//            case KeyEvent.VK_DOWN:
//                mPaddlePos[1].y += 10.0f;
//                if(mPaddlePos[1].y > getHeight() - paddleH * 1.5f)
//                    mPaddlePos[1].y = getHeight() - paddleH * 1.5f;
//                break;

        }
    }
    @Override
    public void keyReleased(KeyEvent e) { }
    /**
    * Update is Thread
    * */
    private void ProcessInput(){

    };

    //Inner Class
    class UpdateGameThread extends Thread{
        @Override
        public void run(){
            while(mIsRunning) {
                //convert nano to seconds
                deltaTime = (System.nanoTime() - mTicksCount) / 1000000.0f;
                //Update mTicksCount (for next frame)
                mTicksCount = System.nanoTime();
                //Clamp maximum deltaTime value(50ms per frame)
                if (deltaTime > 0.03f)
                    deltaTime = 0.03f;

                //the move of ball
                mBallPos.x += mBallVel.x * deltaTime;
                mBallPos.y += mBallVel.y * deltaTime;
                //碰挡板的判定 or lose判定
                if (mBallPos.x < Thickness)
                    if (0 <= mBallPos.y - mPaddlePos[0].y && mBallPos.y - mPaddlePos[0].y < paddleH)
                        mBallVel.x *= -1;
                    else {
                        win_player = 2;
                        mIsRunning = false;
                        //mBallVel.x *= -1;
                    }
                if (mBallPos.x > mPaddlePos[1].x)
                    if (0 <= mBallPos.y - mPaddlePos[1].y && mBallPos.y - mPaddlePos[1].y < paddleH)
                        mBallVel.x *= -1;
                        //System.out.println("******");
                    else {
                        win_player = 1;
                        mIsRunning = false;
                        //mBallVel.x *= -1;
                    }

                // When ball hits the wall
//                if (mBallPos.x > getWidth() - Thickness * 3 && mBallVel.x > 0)
//                    mBallVel.x *= -1;  右边的墙
                if (mBallPos.y < Thickness && mBallVel.y < 0 || mBallPos.y > getHeight() - Thickness * 6 && mBallVel.y > 0)
                    mBallVel.y *= -1;   //上下的墙

                //make send Packet to Client
                //ball's x & y
                String []bll = new String[3];
                bll[0] = Float.toString(mBallPos.x);
                bll[1] = Float.toString(mBallPos.y);
                bll[2] = Float.toString(mPaddlePos[0].y);
                /** 前九位存坐标数值  第十位存数值长度 */
                /** 第50位存 win_palyer */
                player1Packet[49] = (byte)(win_player + 48);

                System.arraycopy(bll[0].getBytes(),0,
                        player1Packet,0,bll[0].getBytes().length);
                player1Packet[9] = (byte)(bll[0].getBytes().length + 48);

                System.arraycopy(bll[1].getBytes(),0,
                        player1Packet,10,bll[1].getBytes().length);
                player1Packet[19] = (byte)(bll[1].getBytes().length + 48);

                System.arraycopy(bll[2].getBytes(),0,
                        player1Packet,20,bll[2].getBytes().length);
                player1Packet[29] = (byte)(bll[2].getBytes().length + 48);

                //String outt = new String(player1Packet,0,30);
                //System.out.println(player1Packet);
                mPanel.repaint();
            }
            //ShutDown();
        }
    };
    private void GenerateOutput(){};

    public void runLoop(){
        new UpdateGameThread().start();
        while(mIsRunning){
            this.setFocusable(true);
            ProcessInput();
            GenerateOutput();
            //System.out.print(mIsRunning);
        }
    };
    public void ShutDown(){
        GameOver();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    };
    public void GameOver(){
        String str = null;
        //Save Data
        SaveData sd = new SaveData();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int score1 = Integer.parseInt(sd.GetKeyValue("Player1"));
        int score2 = Integer.parseInt(sd.GetKeyValue("Player2"));
        float total = Float.parseFloat(sd.GetKeyValue("Total"));
        if(win_player == 1){
            str = "Player1 Win";
            sd.SetKeyValue("Player1", String.valueOf(++score1));
            sd.SetKeyValue("Total", String.valueOf((int)++total));
            sd.SetKeyValue("WiningPercentage", (score1 / total) + " / " + (score2 / total));
        }
        else if(win_player == 2){
            str = "Player2 Win";
            sd.SetKeyValue("Player2", String.valueOf(++score2));
            sd.SetKeyValue("Total", String.valueOf((int)++total));
            sd.SetKeyValue("WiningPercentage", (score1 / total) + " / " + (score2 / total));
        }
        else
            System.exit(0);
        //BufferedImage res_image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics res_graphic = mPanel.getGraphics();

        this.update(res_graphic);

        //clear screen  !!!!!  seems to be failed
        res_graphic.setColor(Color.black);
        res_graphic.drawRect(0, 0, this.getWidth(), this.getHeight());


        //paint String
        res_graphic.setColor(Color.BLUE);
        res_graphic.setFont(new Font(null, Font.BOLD, 50));
        res_graphic.drawString(str, getWidth() / 2 - 150, getHeight() / 2 - 70);

    }
}

class MyPanel extends Panel{
    private GameFrame mFram;
    private Image mimage;
    private Graphics mgra;
    public MyPanel(GameFrame jf){
        mFram = jf;
    }
    @Override
    public void paint(Graphics g){
        if(mimage == null){
          mimage = createImage(mFram.getWidth(), mFram.getHeight());
          mgra = mimage.getGraphics();
        }
        //Clear Screen
        mgra.setColor(getBackground());
        mgra.fillRect(0, 0, getWidth(),getHeight());

        //Create/Update Paddle
        mgra.setColor(Color.BLUE);
        mgra.fillRect((int)mFram.mPaddlePos[0].x ,(int)mFram.mPaddlePos[0].y, mFram.Thickness,(int)mFram.paddleH);
        mgra.fillRect((int)mFram.mPaddlePos[1].x ,(int)mFram.mPaddlePos[1].y, mFram.Thickness,(int)mFram.paddleH);

        //Create/Update wall
        mgra.fillRect(0,0, getWidth(),mFram.Thickness);
        //g.fillRect(getWidth() - mFram.Thickness,0, mFram.Thickness,getHeight());
        mgra.fillRect(0,getHeight() - mFram.Thickness, getWidth(),mFram.Thickness);

        //create/Update Ball
        mgra.fillRect((int)mFram.mBallPos.x, (int)mFram.mBallPos.y, mFram.Thickness,mFram.Thickness );

        //Draw
        g.drawImage(mimage, 0, 0, null);
    }

    @Override
    public void update(Graphics src){
        this.setFocusable(true);
        paint(src);
    }
}


class Vector2{
    public float x;
    public float y;
    public Vector2(){};
    public Vector2(float x_, float _y){x = x_; y = _y;}
    public void set(float x_, float _y){x = x_; y = _y;}
}
