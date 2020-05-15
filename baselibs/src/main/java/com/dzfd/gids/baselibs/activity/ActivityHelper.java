package com.dzfd.gids.baselibs.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.dzfd.gids.baselibs.R;

/**
 * Created by zheng on 2018/12/24.
 */

public class ActivityHelper {

    public static  void _startActivity(Context context,Class cls){

        Intent intent=new Intent();
        intent.setClass(context,cls);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(R.anim.in_from_right, 0);
        }
    }





}
