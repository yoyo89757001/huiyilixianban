package megvii.testfacepass.utils;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

public class ValueAnimatorUtils  {
    private ValueAnimator valueAnimator=null;
    private  ValueAnimatorIntface valueAnimatorIntface;
    private Interpolator [] interpolators=new Interpolator[]{new AccelerateInterpolator(),new LinearInterpolator(),new DecelerateInterpolator()};

    public  void setIntface(ValueAnimatorIntface valueAnimatorIntface){
       this.valueAnimatorIntface=valueAnimatorIntface;
    }

    public  void animator(float a1, float a2,long duration,int repeatCount,int interpolator){
        ValueAnimator animator = ValueAnimator.ofFloat(a1, a2);
        //动画时长，让进度条在CountDown时间内正好从0-360走完，
        animator.setDuration(duration);
        animator.setInterpolator(interpolators[interpolator]);//持续加速
        animator.setRepeatCount(repeatCount);//0表示不循环，-1表示无限循环
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                /**
                 * 这里我们已经知道ValueAnimator只是对值做动画运算，而不是针对控件的，因为我们设置的区间值为0-1.0f
                 * 所以animation.getAnimatedValue()得到的值也是在[0.0-1.0]区间，而我们在画进度条弧度时，设置的当前角度为360*currentAngle，
                 * 因此，当我们的区间值变为1.0的时候弧度刚好转了360度
                 */
                float jiaodu = (float) animation.getAnimatedValue();
                valueAnimatorIntface.update(jiaodu);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                valueAnimatorIntface.start();
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                valueAnimatorIntface.end();
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animator.start();
        valueAnimator=animator;
    }

    public ValueAnimator getValueAnimator(){
        return valueAnimator;
    }
}
