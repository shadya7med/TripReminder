package com.iti.example.tripreminder.Adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.iti.example.tripreminder.Fragments.HistoryFragment;
import com.iti.example.tripreminder.Fragments.MapFragment;

public class PagerAdapter extends FragmentPagerAdapter {

        private int tabsNumber;
        private Context context;
    public PagerAdapter(@NonNull FragmentManager fm, int behavior, int tabs, Context context) {
            super(fm, behavior);
            this.tabsNumber = tabs;
            this.context = context ;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    return new HistoryFragment();
                case 1:
                    return new MapFragment(context);
                default: return null;
            }
        }

        @Override
        public int getCount() {
            return tabsNumber;
        }
}
