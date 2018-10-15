package megvii.testfacepass.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class AutoScalingTextView extends View {
    private Paint textPaint =null;
    private float width=0,hight=0;
    private String text=null;
    private int tsize=12;


    public AutoScalingTextView(Context context) {
        super(context);
        init();
    }

    public AutoScalingTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AutoScalingTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        textPaint=new Paint();
        textPaint.setTextSize(tsize);
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
    }

    public void setDate(float width,float hight,String text){
        this.width=width;
        this.hight=hight;
        this.text=text;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("AutoScalingTextView", "textPaint.getFontMetrics().ascent:" + textPaint.getFontMetrics().top);
        Log.d("AutoScalingTextView", "textPaint.getFontMetrics().descent:" + textPaint.getFontMetrics().bottom);
        Log.d("AutoScalingTextView", "Math.abs:" + (Math.abs(textPaint.getFontMetrics().top)+textPaint.getFontMetrics().bottom));

        if (hight!=0 && text!=null){
            float th=hight-80;
            float tw=width-10;
            float abs=0;

            while (true){
                 abs=(Math.abs(textPaint.getFontMetrics().top)+textPaint.getFontMetrics().bottom);
                if (abs<th ){
                    if ((th-abs)<8){
                        //比它小,又在10的范围内
                        break;
                    }else {
                        tsize+=2;
                        textPaint.setTextSize(tsize);
                    }
                }
            }

            float w=0;
            while (true){
                w=textPaint.measureText(text);
                if (w>width){
                    tsize-=2;
                    textPaint.setTextSize(tsize);
                }else {

                    break;
                }
            }


            abs=(Math.abs(textPaint.getFontMetrics().top)+textPaint.getFontMetrics().bottom);
            canvas.drawText(text,width/2-w/2,hight/2+abs/4,textPaint); //Y 默认是从基准线绘制 就是中心偏上点

        }



    }


}
