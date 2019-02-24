package kasper.pagh.keebin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by pelle on 12/6/2016.
 */

public class KeebinInfoFragment extends Fragment {

    public static KeebinInfoFragment newInstance(){
        Bundle args = new Bundle();
        KeebinInfoFragment fragment = new KeebinInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.keebin_info, container, false);
        return view;
    }
}
