package top.wefor.wordfeel.ui.widget.crazycat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;

import java.util.HashMap;
import java.util.Vector;

import top.wefor.wordfeel.R;

/**
 * edited by ice on 16/11/12
 */

public class Playground extends SurfaceView implements View.OnTouchListener {

    // int k = 1;  供后文测试使用

    // 根据屏幕设置宽度的一种方法：它由MainActivity类中的getWindowManager来获取
    //private static final int sWidth = (int)(MainActivity.screenWidth*0.8)/10;
    // 根据屏幕设置宽度的另一种方法，在下面的方法surfaceChanged中进行设置
    private static float sWidth;
    private static final int COL = 10;   // 每列有10个元素
    private static final int ROW = 10;   // 每行有10个元素
    private static final int BLOCKS = 15;   // 默认添加的路障数量

    private Dot matrix[][];  // 声明数组matrix来保存游戏元素
    private Dot cat;    // 猫

    private ResultCallback mResultCallback;

    public Playground(Context context, ResultCallback resultCallback) {
        super(context);

        mResultCallback = resultCallback;

        getHolder().addCallback(callback);  // 为getHolder设置回调
        matrix = new Dot[ROW][COL];   // 初始化
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                matrix[i][j] = new Dot(j, i);  // x表示行，y表示列，因此和数组的COL/ROW是错开的
            }
        }


        setOnTouchListener(this);  // 该类已经实现了OnTouchListener接口，因此只用传入this即可
        initGame();

    }

    private void initGame() {
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                matrix[i][j].setStatus(Dot.STATUS_OFF);
            }
        }
        cat = new Dot(ROW / 2, COL / 2);   // 初始化猫的位置
        getDot(ROW / 2, COL / 2).setStatus(Dot.STATUS_IN);  // 初始化猫所在位置的状态
        for (int i = 0; i < BLOCKS; ) {
            int x = (int) ((Math.random() * 1000) % COL);  // x为横坐标，数组中的列
            int y = (int) ((Math.random() * 1000) % ROW);  // y为纵坐标，数组中的行
            if (getDot(x, y).getStatus() == Dot.STATUS_OFF) {   // 如果当前Dot的状态为OFF
                getDot(x, y).setStatus(Dot.STATUS_ON);        // 则将其打开，并让i自增
                i++;
                //System.out.println("BLOCKS+"+i);
            }
        }
    }

    private static double sDotH;

    private void redraw() {
        Canvas c = getHolder().lockCanvas();  // 先上锁
        c.drawColor(getContext().getResources().getColor(R.color.beige_half));   // 为canvas设置为
        Paint paint = new Paint();  // 开始绘制到屏幕上
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);  // 抗锯齿
        for (int i = 0; i < ROW; i++) {
            int offset = 0;     // 设置偏移量
            if (i % 2 != 0) {   // i为奇数表示是第2，4，6……行（索引为1，3，5……）
                offset = (int) (sWidth / 2 + 0.5);   // 偏移量为元素的宽度的一半
            }
            for (int j = 0; j < COL; j++) {
                Dot one = getDot(j, i);
                switch (one.getStatus()) {
                    case Dot.STATUS_OFF:
                        paint.setColor(0xFFc9d9d9);
                        break;
                    case Dot.STATUS_IN:
                        paint.setColor(0xFFFF0000);
                        break;
                    case Dot.STATUS_ON:
                        paint.setColor(0xFFFFCC00);
                        break;
                    default:
                        break;
                }

                if (sDotH <= 0) {
                    double cos30 = Math.cos(30 * Math.PI / 180);
                    sDotH = sWidth * cos30;
                }

                float topY = (float) (one.getY() * sDotH);

                float bottomY = topY + sWidth;
                c.drawOval(new RectF(
                                one.getX() * sWidth + offset,
                                topY,
                                (one.getX() + 1) * sWidth + offset,
                                bottomY),
                        paint);    // 大小由屏幕宽度决定
            }
        }
        getHolder().unlockCanvasAndPost(c);   // 解锁
    }

    Callback callback = new Callback() {    // 回调方法
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            redraw();  // 调用redraw进行重绘
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (sWidth <= 0)
                sWidth = width / (COL + 0.5f);
            redraw();   // 修改宽度后进行重绘
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            //Toast.makeText(getContext(),event.getX()+":"+ event.getY(),
            //        Toast.LENGTH_SHORT).show();
            int x, y;    // 触摸的X、Y坐标
            y = (int) (event.getY() / sDotH);
            if (y % 2 == 0) {   // 表示第1、3、5……行元素（索引为0、2、4……）
                x = (int) (event.getX() / sWidth);
            } else {
                x = (int) ((event.getX() - sWidth / 2) / sWidth);    // 事件获取的X坐标减去左边空余的WIDTH/2部分，再除以WIDTH
            }
            // 以下代码对坐标进行保护，如果没有这些代码当点击位置超出坐标范围后也就是超出了数组索引
            if (x + 1 > COL || y + 1 > ROW) {     // 超出边界，当前点击无效
                // 以下为测试代码
//                System.out.println("------------------------");
//                getNrighbour(cat,k).setStatus(Dot.STATUS_IN);
//                k++;
//                redraw();
                initGame();
                // 以下为测试代码
//                System.out.println("------------------------");
//                for(int i = 1;i < 7; i++){
//                    System.out.println(i+"@"+getDistance(cat,i));

            } else if (getDot(x, y).getStatus() == Dot.STATUS_OFF) {  // 当这个点可用时
                getDot(x, y).setStatus(Dot.STATUS_ON);   // 点击有效，开启路障状态
                move();
            }
            redraw();   // 重绘
        }
        return true;
    }

    private Dot getDot(int x, int y) {  // 传入x,y返回matrix[y][x]
        return matrix[y][x];
    }

    private boolean isAtEdge(Dot d) {
        if (d.getX() * d.getY() == 0 || d.getX() + 1 == COL || d.getY() + 1 == ROW) {   // 此时处于游戏边界
            return true;
        }
        return false;
    }

    private Dot getNeighbor(Dot d, int dir) { // 从水平相连的左侧点开始为1，依次顺时针计数
        switch (dir) {
            case 1:
                return getDot(d.getX() - 1, d.getY());
            case 2:
                if (d.getY() % 2 == 0) {
                    return getDot(d.getX() - 1, d.getY() - 1);
                } else {
                    return getDot(d.getX(), d.getY() - 1);
                }
            case 3:
                if (d.getY() % 2 == 0) {
                    return getDot(d.getX(), d.getY() - 1);
                } else {
                    return getDot(d.getX() + 1, d.getY() - 1);
                }
            case 4:
                return getDot(d.getX() + 1, d.getY());
            case 5:
                if (d.getY() % 2 == 0) {
                    return getDot(d.getX(), d.getY() + 1);
                } else {
                    return getDot(d.getX() + 1, d.getY() + 1);
                }
            case 6:
                if (d.getY() % 2 == 0) {
                    return getDot(d.getX() - 1, d.getY() + 1);
                } else {
                    return getDot(d.getX(), d.getY() + 1);
                }
            default:
                return null;
        }
    }

    // dir为方向：如果距离某个方向路障中间有2个灰色（OFF）则返回-2；
    // 如果紧挨该方向的路障，则返回0；
    // 如果该方向没有路障，则返回至边界中间的灰色元素个数，为正数
    private int getDistance(Dot d, int dir) {
        int distance = 0;
        if (isAtEdge(d)) {    // 如果该点已经在屏幕边缘，那么就不必继续判断直接返回distance即可
            return 1;
        }
        Dot ori = d, next;
        while (true) {
            next = getNeighbor(ori, dir);     // 将当前点d的某方向的邻居赋值给next
            if (next.getStatus() == Dot.STATUS_ON) {  // 碰到了路障，返回0或负数
                return distance * -1;
            }
            if (isAtEdge(next)) {     // 抵达了场景边缘，返回正数
                distance++;     // 说明下一个点也是可用的
                return distance;
            }
            distance++;     // 距离自增
            ori = next;     // 将next设置为当前参考点
        }
    }

    private void MoveTo(Dot d) {     // 移动神经猫到某个点
        d.setStatus(Dot.STATUS_IN);
        getDot(cat.getX(), cat.getY()).setStatus(Dot.STATUS_OFF);
        cat.setXY(d.getX(), d.getY());
    }

    private void move() {        // 移动猫
        if (isAtEdge(cat)) {  // 判断猫是否在场景边界
            // 游戏失败
            if (mResultCallback != null)
                mResultCallback.lose();
            return;
        }
        Vector<Dot> avaliable = new Vector<>();     // 当前点周围6个点中的可用点
        Vector<Dot> positive = new Vector<>();  // 当前点到边界的距离点
        HashMap<Dot, Integer> length = new HashMap<Dot, Integer>();// 搭配positive，其用于记录方向
        for (int i = 1; i < 7; i++) {
            Dot n = getNeighbor(cat, i);
            if (n.getStatus() == Dot.STATUS_OFF) {    // 如果这邻居（点）可用
                avaliable.add(n);      // 将其添加到avaliable中
                length.put(n, i);    // i为方向
                if (getDistance(n, i) > 0) {   // 正数表示可以到达边界
                    positive.add(n);
                }
            }
        }
        if (avaliable.size() == 0) {      // 没有可用点
            // 成功围住神经猫
            if (mResultCallback != null)
                mResultCallback.win();
        } else if (avaliable.size() == 1) {     // 只有一个可用点
            MoveTo(avaliable.get(0));
            //}else if(justInit){       // justInit为真表示用户第一次点击
            //    int s = (int)((Math.random()*1000)%avaliable.size());
            //    MoveTo(avaliable.get(s));   // 移动猫到第一个可用点
        } else {      // 既不是第一次点击，且有多条路可走
            // 根据到边界edge的距离distance（包括路障等的计算）来决定走的方向
            Dot best = null;   // 最终决定要移动到的元素（点）
            if (positive.size() != 0) {   // 含有可到达边界的方向
                //System.out.println("向前进");
                int min = 999;  // 用于记录到达边界的最小值，初始值为一个较大值
                for (int i = 0; i < positive.size(); i++) {
                    int tempDis = getDistance(positive.get(i), length.get(positive.get(i)));
                    if (tempDis < min) {
                        min = tempDis;
                        best = positive.get(i);
                    }
                }
            } else {  // 所有方向都有路障
                //System.out.println("躲路障");
                int max = 0;    // 所有方向都有路障时，距离要么为负数要么为0
                for (int i = 0; i < avaliable.size(); i++) {
                    int tempDis = getDistance(avaliable.get(i), length.get(avaliable.get(i)));
                    if (tempDis < max) {   // 因为tempDis是负数，所以用小于号
                        max = tempDis;
                        best = avaliable.get(i);
                    }
                }
            }
            if (best == null) {      // 当best为空时，随机一个路径
                int s = (int) ((Math.random() * 1000) % avaliable.size());
                MoveTo(avaliable.get(s));
            } else {
                MoveTo(best);   // 移动到最合适的一点
            }
        }
    }

    public interface ResultCallback {
        void lose();

        void win();
    }

}
