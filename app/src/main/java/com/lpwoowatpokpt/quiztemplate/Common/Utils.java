package com.lpwoowatpokpt.quiztemplate.Common;

import android.app.Activity;
import android.content.Intent;

import com.lpwoowatpokpt.quiztemplate.R;


/**
 * Created by Death on 22/12/2017.
 */

public class Utils {
    public final static int THEME_DEFAULT = 0;
    public final static int THEME_DARK = 1;

    public static void ChangeToTheme(Activity activity)
    {
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }


    public static void onActivityCreateSetTheme(Activity activity, int sTheme)
    {
        switch (sTheme)
        {
            default:
            case THEME_DEFAULT:
                activity.setTheme(R.style.AppTheme);
                break;
            case THEME_DARK:
                activity.setTheme(R.style.DarkTheme);
                break;
        }
    }
}
