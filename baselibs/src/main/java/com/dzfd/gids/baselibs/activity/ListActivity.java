package com.dzfd.gids.baselibs.activity;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import com.dzfd.gids.baselibs.R;

public  abstract  class ListActivity extends BunActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layoutid=getActivityLayoutId();
        if(layoutid>0){
            setContentView(layoutid);
        }
        Fragment maininst= getMainFragment();
        if(maininst !=null){
            addMainFragment(maininst);
        }

    }
    public void addMainFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().add(R.id.common_content_layout, fragment).commit();
    }
    public void ReplaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.common_content_layout, fragment).commitAllowingStateLoss();

    }
    public int getActivityLayoutId(){
        return R.layout.activity_bun;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    abstract public Fragment getMainFragment();


}
