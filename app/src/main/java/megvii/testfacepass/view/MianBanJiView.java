package megvii.testfacepass.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


import java.util.ArrayList;
import java.util.List;

import megvii.testfacepass.R;


public class MianBanJiView extends View{
    private Context context;
    private Paint centerPaint;
    private Paint spreadPaint;
    List<Integer> alphas=new ArrayList<>();
    List<Integer> spreadRadius=new ArrayList<>();
    private int centerX=0,centerY=0;
    private int radius=100; //头像的宽度
    private int radius2=60;
    private int distance=1; //扩散的快慢
    private int maxRadius=30;
    private Bitmap bitmap=null;
    private RectF rectF=new RectF();
    private RectF rectF2=new RectF();
    private Bitmap henfu= null;
    private int type=0;


    private void initPaints() {
        henfu=BitmapFactory.decodeResource(context.getResources(), R.drawable.viphf_203);
        //画笔1:
        centerPaint = new Paint();
        centerPaint.setAntiAlias(true);//抗锯齿效果
        //最开始不透明且扩散距离为0
        alphas.add(255);
        spreadRadius.add(0);
        //画笔2:
        spreadPaint = new Paint();
        spreadPaint.setAntiAlias(true);
        spreadPaint.setAlpha(255);
        spreadPaint.setColor(Color.WHITE);
    }


    public MianBanJiView(Context context) {
        super(context);
        this.context=context;
        initPaints();
    }


    public MianBanJiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        initPaints();
    }

    public MianBanJiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        initPaints();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        int size = Math.min(w, h);
        setMeasuredDimension(size, size);
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //圆心位置
        centerX = w / 2;
        centerY = h / 2;

        rectF.set(centerX-radius,centerY-radius,centerX+radius,centerY+radius);
        rectF2.set(centerX-radius-60,centerY+radius-30,centerX+radius+60,centerY+radius+50);
    }




    public void setBitmap(Bitmap bitmap,int type){
        this.bitmap=bitmap;
        this.type=type;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < spreadRadius.size(); i++) {
            int alpha = alphas.get(i);
            spreadPaint.setAlpha(alpha);
            int width = spreadRadius.get(i);
            //绘制扩散的圆
            canvas.drawCircle(centerX, centerY, radius2 + width, spreadPaint);
            //每次扩散圆半径递增，圆透明度递减
            if (alpha > 0 && width < 300) {
                alpha = alpha - distance > 0 ? alpha - distance : 0;
                alphas.set(i, alpha);
                spreadRadius.set(i, width + distance);
            }
        }
        //当最外层扩散圆半径达到最大半径时添加新扩散圆
        if (spreadRadius.get(spreadRadius.size() - 1) > maxRadius) {
            spreadRadius.add(0);
            alphas.add(255);
        }
        //超过8个扩散圆，删除最先绘制的圆，即最外层的圆
        if (spreadRadius.size() >= 13) {
            alphas.remove(0);
            spreadRadius.remove(0);
        }
        //中间的圆
        canvas.drawBitmap(bitmap,null,rectF, centerPaint);
        if (type==0){
            canvas.drawBitmap(henfu,null,rectF2, centerPaint);
        }
        //TODO 可以在中间圆绘制文字或者图片
        //延迟更新，达到扩散视觉差效果
        postInvalidateDelayed(10);


    }



}
