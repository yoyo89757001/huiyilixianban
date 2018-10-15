package megvii.testfacepass.utils;

import android.graphics.Bitmap;

import java.nio.ByteBuffer;

public class ImageUtils {

    public static byte[] bitmapToByte(Bitmap bmp){
        int bytes = bmp.getByteCount();
        ByteBuffer buf = ByteBuffer.allocate(bytes);
        bmp.copyPixelsToBuffer(buf);
        return  buf.array();
    }
}
