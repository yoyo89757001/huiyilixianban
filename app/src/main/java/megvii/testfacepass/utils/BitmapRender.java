package megvii.testfacepass.utils;

import android.graphics.Bitmap;



    /**
     * Created by user on 2016/11/30.
     */
    public abstract class BitmapRender {
        protected Bitmap bitmap;
        protected int width, height;

        public BitmapRender(Bitmap bitmap) {
            width = bitmap.getWidth();
            height = bitmap.getHeight();
            int[] b1 = new int[width * height];
            bitmap.getPixels(b1, 0, width, 0, 0, width, height);
            this.bitmap = bitmap;
            initRGBdata(b1);
        }

        protected abstract void initRGBdata(int[] b1);

        protected abstract int[] nextFrame();

        public final Bitmap nextAction() {
            if (bitmap != null) {
                bitmap.recycle();
            }
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            bitmap.setPixels(nextFrame(), 0, width, 0, 0, width, height);
            return bitmap;
        }

        public abstract void dropStone(int x, int y, int stoneSize, int stoneWeight);

        public abstract void quiet();


}
