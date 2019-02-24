package utils;

import android.os.Build;
import android.text.Html;
import android.text.Spanned;

/**
 * Created by kaspe on 03-04-2017.
 */

public class HtmlParser
{
    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String source)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        }
        else
        {
            return Html.fromHtml(source);
        }
    }
}
