package top.wefor.wordfeel.ui.widget.crazycat;

/**
 * edited by ice on 16/11/12
 */

public class Dot {
    int x, y;
    int status; // 灰色：猫可以走，红色：以及被设置为路障，橘色：猫的位置
    public static final int STATUS_ON = 1;  // 开启路障（红色）
    public static final int STATUS_OFF = 0;   // 关闭路障（灰色）
    public static final int STATUS_IN = 9;   // 神经猫的位置

    public Dot(int x, int y) {
        super();
        this.x = x;
        this.y = y;
        status = STATUS_OFF;
    }

    public void setXY(int x, int y){
        this.x=x;
        this.y=y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
