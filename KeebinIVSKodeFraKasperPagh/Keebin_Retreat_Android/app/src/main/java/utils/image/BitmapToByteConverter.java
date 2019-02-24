package utils.image;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

/**
 * Created by kaspe on 2017-02-10.
 */
//Creds for this code goes to sackoverflow user Lazy Ninja.
public class BitmapToByteConverter
{
    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

}
