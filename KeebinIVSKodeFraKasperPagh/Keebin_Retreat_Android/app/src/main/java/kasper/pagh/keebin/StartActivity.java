package kasper.pagh.keebin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class StartActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        final Intent intent = new Intent(this, LoginActivity.class);
        new android.os.Handler().postDelayed(
                new Runnable()
                {
                    public void run()
                    {
                    Log.d("start", "venter på login");

                        startActivity(intent);
                    }
                },
                1000);

    }
}
