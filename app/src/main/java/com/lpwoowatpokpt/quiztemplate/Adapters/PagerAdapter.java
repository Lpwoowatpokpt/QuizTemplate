package com.lpwoowatpokpt.quiztemplate.Adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.lpwoowatpokpt.quiztemplate.R;
import com.lpwoowatpokpt.quiztemplate.UI.Tabs.TabAnswers;
import com.lpwoowatpokpt.quiztemplate.UI.Tabs.TabHint;
import com.lpwoowatpokpt.quiztemplate.UI.Tabs.TabQuestion;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;

    public PagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return TabQuestion.getInstance();
            case 1:
              return TabAnswers.getInstance();
            case 2:
                return TabHint.getInstance();

                default:
                    return TabQuestion.getInstance();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return mContext.getString(R.string.Question);
            case 1:
                return mContext.getString(R.string.Answers);
            case 2:
                return mContext.getString(R.string.Hint);
        }
        return null;
    }
}
