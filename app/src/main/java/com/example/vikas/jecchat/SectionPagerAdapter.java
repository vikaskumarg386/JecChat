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
                timeLineF tf=new timeLineF();
                        return tf;
            case 1:
                requestF rf=new requestF();
                return rf;
            case 2:
                chatF cf=new chatF();
                return cf;
            case 3:
                friendsF ff=new friendsF();
                return ff;
            default:return null;
        }

    }

    @Override
    public int getCount() {
        return 4;
    }

    public CharSequence getPageTitle(int position){

        switch (position){
            case 0:
                return "TIMELINE";
            case 1:
                return "REQUEST";
            case 2:
                return "CHATS";
            case 3:
                return "FRIENDS";
            default:
                return null;

        }
    }
}
