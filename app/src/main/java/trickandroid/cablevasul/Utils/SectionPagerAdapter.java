package trickandroid.cablevasul.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Simon Peter on 07-Jul-17.
 */

public class SectionPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = "SectionPagerAdapter";

    private final List<Fragment> mFragmentLists = new ArrayList<>();
    private final List<Fragment> mFragmentStringLists = new ArrayList<>();



    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentLists.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentLists.size();
    }

    public void addFragments(Fragment fragment, String areaName){
        mFragmentLists.add(fragment);
        mFragmentStringLists.add(fragment);
    }

}






















