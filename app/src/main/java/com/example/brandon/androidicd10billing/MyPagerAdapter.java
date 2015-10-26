package com.example.brandon.androidicd10billing;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MyPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

   public MyPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                BillActivity tab1 = new BillActivity();
                return tab1;
            case 1:
                DrillDownCodeSearchActivity tab2 = new DrillDownCodeSearchActivity();
                return tab2;
//            case 2:
//                TabFragment3 tab3 = new TabFragment3();
//                return tab3;
            default:
                return null;
        }
//        return null;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}