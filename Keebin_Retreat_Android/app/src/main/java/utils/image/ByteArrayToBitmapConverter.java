package utils.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by kaspe on 2017-02-10.
 */

//Creds for this code goes to sackoverflow user Lazy Ninja.
public class ByteArrayToBitmapConverter
{
    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image)
    {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
