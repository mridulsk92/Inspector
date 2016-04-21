package com.example.mridul_xpetize.test;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapterWorker extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapterWorker(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                WorkerTab2 tab1 = new WorkerTab2();
                return tab1;
            case 1:
                worker_tab1 tab2 = new worker_tab1();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}