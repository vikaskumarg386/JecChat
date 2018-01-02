package com.example.vikas.jecchat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by vikas on 16/12/17.
 */

class SectionPagerAdapter  extends FragmentPagerAdapter{
    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                requestF rf=new requestF();
                return rf;
            case 1:
                chatF cf=new chatF();
                return cf;
            case 2:
                friendsF ff=new friendsF();
                return ff;
            default:return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position){

        switch (position){
            case 0:
                return "REQUESTS";
            case 1:
                return "CHATS";
            case 2:
                return "FRIENDS";
            default:
                return null;

        }
    }
}
