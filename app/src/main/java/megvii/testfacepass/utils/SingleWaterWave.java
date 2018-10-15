package megvii.testfacepass.utils;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.animation.LinearInterpolator;


/**
 * Created by user on 2016/11/29.
 */
public class SingleWaterWave extends BitmapRender {
    private int[] b1, b2;
    short[] buf, buf_;
    private ValueAnimator animator;

    public SingleWaterWave(Bitmap bitmap) {
        super(bitmap);
        int min = width;
        len0 = min * 0.02f;
        r0 = min * 0.05f;
        weight0 = min * 0.015f;
    }

    @Override
    protected void initRGBdata(int[] b1) {
        this.b1 = b1;
        b2 = new int[width * height];
        buf = new short[width * height];
        buf_ = new short[width * height];
    }

    //波形传播
  private   void rippleRender() {
        int offset;
        int i = 0;
        int length = width * height;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++, i++) {

                // 计算出偏移象素和原始象素的内存地址偏移量 :
                double disance = Math.sqrt((x0 - x) * (x0 - x) + (y0 - y) * (y0 - y));

                //先渲染在x0右侧的波
                if (disance < r - len || disance > r + len) {//不在水波影响范围
                    b2[i] = 0x00ffffff;
                    continue;
                }
                double d = (disance - r) / len;//这个值在区间内由-1变到0再到1

                //在[-1,1]区间,形成水波曲线函数,
                d = Math.cos(d * Math.PI / 2) * -weight;//向外的像素偏移值;

                int dx =(int)( d * (x - x0) / r);//x方向的偏移值
                int dy =(int)( d * (y - y0) / r);//y方向的偏移值

                offset = dy * width + dx;
                // 判断坐标是否在范围内
                if (i + offset > 0 && i + offset < length) {
                    b2[i] = b1[i + offset];
                } else {
                    b2[i] = 0x00ffffff;
                }
            }
        }
//        b2[(int) x0 + (int) y0 * width] = 0xffff0000;//波心标红 为了展示
    }

    //做x轴水波试验用的
    void onlyXzhou() {
        int x = 0, offset = 0, i = 0;
        //先渲染在x0右侧的波
        float center = x0 + r;//高潮点
        if (x < center - len || x > center + len) {//不在水波影响范围
            b2[i] = b1[i];
//            continue;
        }
        float dx = (x - center) / len;//这个值在区间内由-1变到0再到1
        //水波曲线在[-1,1]函数应该有y=A(cos(πx)+1/2),A是最高处的值weight,水平面为0,函数末端-1和1也都是cong0到0,对此求导数

        dx = (int) (Math.cos(dx * Math.PI / 2) * -weight);//这个值必须由0->weight->0->weight->0;

        offset = (int) dx;

        // 判断坐标是否在范围内
        if (x + offset > 0 && x + offset < width) {
            b2[i] = b1[i + offset];
        } else {
            b2[i] = b1[i];
        }
    }
    //传播方法启用..
    void rippleSpread() {
        weight -= dw;
        if (weight <= 0) {
            end();
            return;
        }
        len += dlen;
    }

    private void end() {
        animator.cancel();
        animator = null;
        if (l != null) {
            l.onEnd();
        }
    }

    float x0, y0;
    float len = 10;//波作用的幅度,波长,或叫波宽
    float r = 20;//波扩散距离,即平均半径
    float weight = 6;//波幅,即水波的最大高度

    //初始值
    float len0 = 10;//波作用的幅度,波长
    float r0 = 20;//波扩散距离
    float weight0 = 6;//波幅

    private static final float dw = 0.2f;//波幅衰弱的速度
    private static final float dlen = 1f;//波长增加的速度

    //弃用
    public void dropStone(int x, int y, int r, int stoneWeight) {
        x0 = x;
        y0 = y;
        this.r = r;
        this.weight = stoneWeight;
        len = len0;
    }
    /**
     * 生成一圈水波
     *
     * @param
     * @param
     * @param
     * @param
     */
    public void dropStone(float x, float y) {
        x0 = x;
        y0 = y;
        //初始化状态
        len = len0;
        r = r0;
        weight = weight0;
        final float maxR = Math.max(width, height);
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
        animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (Float) animation.getAnimatedValue();
                r = (maxR - r0) * v + r0;
                weight = weight0 - weight0 * v;
                len = len0 * v + len0;
                JNIRender.render(b1, b2, width, height, r, len, weight, x0, y0);
//                rippleRender();
                if (l != null) {
                    if (bitmap != null) {
                        bitmap.recycle();
                    }
                    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
                    bitmap.setPixels(b2, 0, width, 0, 0, width, height);
                    l.onFrame(bitmap);
                }
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                if (l != null) {
                    l.onEnd();
                }
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animator.setDuration(2000);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }
    //没用方法
    protected int[] nextFrame() {
        rippleSpread();
        rippleRender();
        return b2;
    }
    //没用方法
    public void quiet() {
        b2 = new int[width * height];
        buf = new short[width * height];
        buf_ = new short[width * height];
    }

    public interface OnFrameCreateListener {
        void onFrame(Bitmap bitmap);

        void onEnd();
    }

    private OnFrameCreateListener l;

    public void setOnFrameCreateListener(OnFrameCreateListener l) {
        this.l = l;
    }
}








