package kasper.pagh.keebin;

import android.graphics.Bitmap;
import java.util.List;

import entity.BrandPicture;

/**
 * Created by kaspe on 2017-02-10.
 */

public interface BrandPictureDelegate
{
    public void sendBrandPic(List<BrandPicture> bmList);
}
