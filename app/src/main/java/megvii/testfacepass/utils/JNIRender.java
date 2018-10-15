package megvii.testfacepass.utils;

import android.graphics.Bitmap;

/**
 * Created by user on 2016/11/29.
 */
public class JNIRender {

    public static native void render(int[] b1, int[] b2, int width, int height, double r, double len, double weight, double x0, double y0);
}

//////C++代码com_example_user_mc_JNIRender.cpp
//
//#include "com_example_user_mc_JNIRender.h"
//        #include "FrameHandler.h"
//        #include "../../../../../../android/ndk/android-ndk32-r10-windows-x86_64/android-ndk-r10/toolchains/mipsel-linux-android-4.8/prebuilt/windows-x86_64/lib/gcc/mipsel-linux-android/4.8/include/stddef.h"
//        #include <cmath>
//void JNICALL Java_com_example_user_mc_JNIRender_render(JNIEnv *env, jclass jclass1, jintArray b1,
//        jintArray b2, jint width, jint height,
//        jdouble r, jdouble len, jdouble weight,
//        jdouble x0,
//        jdouble y0) {
//        jint *b3 = env->GetIntArrayElements(b1, false);
//        jint size = env->GetArrayLength(b1);
//        jint *b4 = env->GetIntArrayElements(b2, false);
//        int offset;
//        int i = 0;
//        int length = width * height;
//        for (int y = 0; y < height; y++) {
//        for (int x = 0; x < width; x++, i++) {
//
//        // 计算出偏移象素和原始象素的内存地址偏移量 :
//        //offset = width * yoffset + xoffset
////                offset = (width * (buf[i - width] - buf[i + width])) + (buf[i - 1] - buf[i + 1]);
//        double disance = sqrt((x0 - x) * (x0 - x) + (y0 - y) * (y0 - y));
//
//        //先渲染在x0右侧的波
//        if (disance < r - len || disance > r + len) {//不在水波影响范围
//        b4[i] = 0x00ffffff;
//        continue;
//        }
//        double d = (disance - r) / len;//这个值在区间内由-1变到0再到1
//
//        //在[-1,1]区间,形成水波曲线函数,
//        d = cos(d * M_PI / 2) * -weight;//向外的像素偏移值;
//
//        int dx =(int)( d * (x - x0) / r);//x方向的偏移值
//        int dy =(int)( d * (y - y0) / r);//y方向的偏移值
//
//        offset = dy * width + dx;
//
//        // 判断坐标是否在范围内
//        if (i + offset > 0 && i + offset < length) {
//        b4[i] = b3[i + offset];
//        } else {
//        b4[i] = 0x00ffffff;
//        }
//        }
//        }
//        env->SetIntArrayRegion(b2, 0, size, b4);
////    delete[] b4, b3;
    //    }