package PongClient;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameFrame extends Frame implements KeyListener{
    final int Thickness = 10;
    final float paddleH = 100.0f;
    private Panel mPanel;

    byte[] player2Order; //记录player2 的移动指令
    int player2 = 0;    //长度
    int win_player = 0; //1为p1 win  2为p2 win


    //Position of Paddle
    Vector2 []mPaddlePos = new Vector2[2];
    //Direction of Paddle
    int mPaddleDir; //not used

    //Position of Ball
    Vector2 mBallPos;
    Vector2 mBallVel; //ball's velocity 矢量速度  client 可以不用
    long mTicksCount;
    float deltaTime;
    boolean mIsRunning;

    public GameFrame(String ss){
        super(ss);
        mPanel = null;

        mPaddleDir = 0;
        mTicksCount = 0;
        mIsRunning = true;

    }
    boolean Initialize(){
        this.setBounds(750,70,600,500);
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

        player2Order = new byte[200];

        mBallPos = new Vector2(getWidth() / 2 - Thickness / 2, getHeight() / 2 - Thickness / 2) ;
        mPaddlePos[0] = new Vector2(0, getHeight() / 2 - paddleH / 2);
        mPaddlePos[1] = new Vector2(getWidth() - Thickness * 2.5f, getHeight() / 2 - paddleH / 2);
        mBallVel = new Vector2(-0.175f, 0.225f);

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
            case KeyEvent.VK_UP:
                player2Order[player2++] = '8';
                mPaddlePos[1].y -= 10.0f;
                if(mPaddlePos[1].y < Thickness)
                    mPaddlePos[1].y = Thickness;
                break;
            case KeyEvent.VK_DOWN:
                player2Order[player2++] = '2';
                mPaddlePos[1].y += 10.0f;
                if(mPaddlePos[1].y > getHeight() - paddleH * 1.5f)
                    mPaddlePos[1].y = getHeight() - paddleH * 1.5f;
                break;
        }
    }
    @Override
    public void keyReleased(KeyEvent e) { }
/**
* Update is Thread
* */
    private void ProcessInput(){

    };
    class UpdateGameThread extends Thread{
        @Override
        public void run(){
            while(mIsRunning) {
/**
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

                //the move of ball
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
                        win_player = 2;
                        mIsRunning = false;
                        //mBallVel.x *= -1;
                    }

                // When ball hits the wall
                if (mBallPos.x > getWidth() - Thickness * 3.5)
                    mBallVel.x *= -1;//右墙
                if (mBallPos.y < Thickness && mBallVel.y < 0 || mBallPos.y > getHeight() - Thickness * 6 && mBallVel.y > 0)
                    mBallVel.y *= -1;//上下墙
*/
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
        int score1 = 0;
        int score2 = 0;
        float total = 0;
        try {
            Thread.sleep(100);
            score1 = Integer.parseInt(sd.GetKeyValue("Player1"));
            score2 = Integer.parseInt(sd.GetKeyValue("Player2"));
            total = Float.parseFloat(sd.GetKeyValue("Total"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(win_player == 1){
            str = "Player1 Win";
            sd.SetKeyValue("Player1", String.valueOf(++score1));
            sd.SetKeyValue("Total", String.valueOf(++total));
            sd.SetKeyValue("WiningPercentage", (score1 / total) + " / " + (score2 / total));
        }
        else if(win_player == 2){
            str = "Player2 Win";
            sd.SetKeyValue("Player2", String.valueOf(++score2));
            sd.SetKeyValue("Total", String.valueOf(++total));
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
        //清屏
        mgra.setColor(getBackground());
        mgra.fillRect(0, 0, getWidth(),getHeight());

        mgra.setColor(Color.BLUE);
        //Create Paddle
        mgra.fillRect((int)mFram.mPaddlePos[0].x ,(int)mFram.mPaddlePos[0].y, mFram.Thickness,(int)mFram.paddleH);
        mgra.fillRect((int)mFram.mPaddlePos[1].x ,(int)mFram.mPaddlePos[1].y, mFram.Thickness,(int)mFram.paddleH);

        //Create wall
        mgra.fillRect(0,0, getWidth(),mFram.Thickness);
        //g.fillRect(getWidth() - mFram.Thickness,0, mFram.Thickness,getHeight());
        mgra.fillRect(0,getHeight() - mFram.Thickness, getWidth(),mFram.Thickness);

        //create Ball
        mgra.fillRect((int)mFram.mBallPos.x, (int)mFram.mBallPos.y, mFram.Thickness,mFram.Thickness );

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
