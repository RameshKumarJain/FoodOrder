package order.food.com.foodorder;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;

public class BitMapLoader extends AsyncTask<InputStream, Void, Bitmap> {

    ImageView imageView;
    String imageName;
    AssetManager mAssert;

    public BitMapLoader(ImageView image, String name, AssetManager mAssert) {
        imageView = image;
        imageName = name;
        this.mAssert = mAssert;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(AssetManager mAssert, String imageName, int reqWidth, int reqHeight) {
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(mAssert.open(imageName));

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(mAssert.open(imageName));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(InputStream... params) {
        final Bitmap bitmap = decodeSampledBitmapFromResource(mAssert, imageName, 100, 100);
        LRUCacheLoader.addBitmapToMemoryCache(imageName, bitmap);
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }
}
