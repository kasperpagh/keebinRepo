package utils.Loaders;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import kasper.pagh.keebin.R;

/**
 * Creds to http://stackoverflow.com/questions/8295001/custom-progress-dialog-android/14662430#14662430 for this class, i have made a slight modification, allowing for choosing the color of the progressBar
 * as well as modifying the xml file, to contain both our logo and some text.
 */

public class CustomProgressbar extends Dialog {
    private static CustomProgressbar mCustomProgressbar;
    private CustomProgressbar mProgressbar;
    private OnDismissListener mOnDissmissListener;

    private CustomProgressbar(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_progressbar);
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public CustomProgressbar(Context context, Boolean instance) {
        super(context);
        mProgressbar = new CustomProgressbar(context);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mOnDissmissListener != null) {
            mOnDissmissListener.onDismiss(this);
        }
    }

    public static void showProgressBar(Context context, boolean cancelable) {
        showProgressBar(context, cancelable, null);
    }

    public static void showProgressBar(Context context, boolean cancelable, String message) {
        if (mCustomProgressbar != null && mCustomProgressbar.isShowing()) {
            mCustomProgressbar.cancel();
        }
        mCustomProgressbar = new CustomProgressbar(context);
        mCustomProgressbar.setCancelable(cancelable);
        //
        ProgressBar pgb = (ProgressBar) mCustomProgressbar.findViewById(R.id.progressbar);
        pgb.getIndeterminateDrawable().setColorFilter(Color.parseColor("#ee5c28"), android.graphics.PorterDuff.Mode.MULTIPLY);
        //
        mCustomProgressbar.show();

    }

    public static void showProgressBar(Context context, OnDismissListener listener) {

        if (mCustomProgressbar != null && mCustomProgressbar.isShowing()) {
            mCustomProgressbar.cancel();
        }
        mCustomProgressbar = new CustomProgressbar(context);
        mCustomProgressbar.setListener(listener);
        mCustomProgressbar.setCancelable(Boolean.TRUE);
        mCustomProgressbar.show();
    }

    public static void hideProgressBar() {
        if (mCustomProgressbar != null) {
            mCustomProgressbar.dismiss();
        }
    }

    private void setListener(OnDismissListener listener) {
        mOnDissmissListener = listener;

    }

    public static void showListViewBottomProgressBar(View view) {
        if (mCustomProgressbar != null) {
            mCustomProgressbar.dismiss();
        }

        view.setVisibility(View.VISIBLE);
    }

    public static void hideListViewBottomProgressBar(View view) {
        if (mCustomProgressbar != null) {
            mCustomProgressbar.dismiss();
        }

        view.setVisibility(View.GONE);
    }

    public void showProgress(Context context, boolean cancelable, String message) {

        if (mProgressbar != null && mProgressbar.isShowing()) {
            mProgressbar.cancel();
        }
        mProgressbar.setCancelable(cancelable);
        mProgressbar.show();
    }

}