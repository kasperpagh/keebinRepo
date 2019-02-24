package kasper.pagh.keebin;
import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class SwipeFunction extends Fragment {


    public static SwipeFunction newInstance()
    {
        SwipeFunction fragment = new SwipeFunction();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.swipe_view, container, false);

        MainActivity.whiteHome();
        MainActivity.blackLoyal();
        MainActivity.whiteMap();

        ViewPager pager = (ViewPager) view.findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getChildFragmentManager()));

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(pager, true);
        return view;
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

                case 0: return UsersLoyaltyCardsFragment.newInstance();
                case 1: return PremiumFragment.newInstance();
                case 2: return KlippeKortFragment.newInstance();
                default: return UsersLoyaltyCardsFragment.newInstance();
            }
        }
        @Override
        public int getCount() {
            return 3;
        }
    }
}
