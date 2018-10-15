package megvii.testfacepass.utils;



import android.graphics.Color;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import megvii.testfacepass.R;
import megvii.testfacepass.view.GlideCircleTransform;


public class GlideUtils {

    public static RequestOptions getRequestOptions(){
        return  new RequestOptions()
                .centerCrop()

                .error(R.drawable.erroy_bg)
                .transform(new GlideCircleTransform(2, Color.WHITE))
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                ;
    }


}
