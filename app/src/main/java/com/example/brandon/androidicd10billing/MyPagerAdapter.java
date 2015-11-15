package com.example.brandon.androidicd10billing;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;

public class MyPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    FragmentManager fm;

   public MyPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.fm = fm;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                BillFragment tab1 = new BillFragment();
                return tab1;
            case 1:
                DrillDownCodeSearchFragment tab2 = new DrillDownCodeSearchFragment();
                return tab2;
            case 2:
                PatientListFragment tab3 = new PatientListFragment();
                return tab3;
            case 3:
                DoctorListFragment tab4 = new DoctorListFragment();
                return tab4;
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