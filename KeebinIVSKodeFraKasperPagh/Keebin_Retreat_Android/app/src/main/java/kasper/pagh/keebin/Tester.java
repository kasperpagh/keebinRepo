package kasper.pagh.keebin;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import rest.houseKeeping.GetDBVersion;
import rest.userReST.GetUser;

public class Tester extends Activity implements AsyncResponse
{

    boolean isClicked = true;
    PopupWindow popUpWindow;
    LayoutParams layoutParams;
    LinearLayout mainLayout;
    Button btnClickHere;
    LinearLayout containerLayout;
    TextView tvMsg;
    String llama ="";
    AsyncResponse abekat = this;
    Context abekatc = this;


    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        View view1 = (View) findViewById(R.id.test);
        builder.setView(view1);

//        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener()
//        {
//            public void onClick(DialogInterface dialog, int id)
//            {
//                llama = "no";
//                Toast.makeText(getApplicationContext(), llama, Toast.LENGTH_LONG).show();
//            }
//        });
//
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
//        {
//            public void onClick(DialogInterface dialog, int id)
//            {
//                llama = "yes";
//                Toast.makeText(getApplicationContext(), llama, Toast.LENGTH_LONG).show();
//                GetUser dbv = new GetUser("asdf","asdf",abekat,abekatc);
//            }
//        });

        builder.show();

    }

    @Override
    public void processFinished(String output)
    {

    }
}