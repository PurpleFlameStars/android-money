package com.cashLoan.money.splash;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.cashLoan.money.JumpHelper;
import com.cashLoan.money.R;
import com.cashLoan.money.splash.adapter.ViewPagerAdapter;
import com.cashLoan.money.splash.bean.GuideShowBean;
import com.cashLoan.money.splash.listener.FragmentEvent;
import com.cashLoan.money.utils.MoneyConfig;
import com.dzfd.gids.baselibs.utils.AndroidUtilsCompat;
import com.dzfd.gids.baselibs.utils.ContextUtils;
import com.dzfd.gids.baselibs.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;


public class GuideFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "SplashActivity-SplashActivity";
    private View rootView;
    private ViewPager mPager;
    private TextView mGuideSkip;
    private TextView mGuideOK;
    private RadioGroup mGuideRadioGroup;

    private List<View> viewList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        hideBottomUIMenu();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_guide_start, container, false);
        initViews(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SPUtils.put(MoneyConfig.CONFIG_FILE, MoneyConfig.KEY_SHOW_INTEREST_GUIDE, true);
    }

    private void initViews(View view) {
        mPager = view.findViewById(R.id.guide_viewpager);
        mGuideSkip =  view.findViewById(R.id.guide_skip_ok);
        mGuideOK = view.findViewById(R.id.guide_ok);
        mGuideRadioGroup = view.findViewById(R.id.guide_radio_group);

        mGuideSkip.setOnClickListener(this);
        mGuideOK.setOnClickListener(this);
        initPager();
    }
    private void pageSelected(int sp){

        if (sp == viewList.size() - 1) {
            mGuideOK.setVisibility(View.VISIBLE);
            mGuideSkip.setVisibility(View.GONE);
            mGuideRadioGroup.setVisibility(View.GONE);
        } else {
            mGuideOK.setVisibility(View.INVISIBLE);
            mGuideSkip.setVisibility(View.VISIBLE);
            mGuideRadioGroup.setVisibility(View.VISIBLE);
            mGuideRadioGroup.check(mGuideRadioGroup.getChildAt(sp).getId());
        }

    }

    private List<GuideShowBean> getWelcomePagesByVersion(){
        List<GuideShowBean> list = new ArrayList<>();
        String[] names = getActivity().getResources().getStringArray(R.array.names);
        String[] title = getActivity().getResources().getStringArray(R.array.title);
        String[] details = getActivity().getResources().getStringArray(R.array.details);
        String[] icons = getActivity().getResources().getStringArray(R.array.icons);

        List<Integer> iconLists = new ArrayList<>();
        for (int i = 0; i < icons.length; i++) {
            iconLists.add(getContext().getResources().getIdentifier(icons[i], "drawable", getContext().getPackageName()));
        }

        Integer[] images = iconLists.toArray(new Integer[iconLists.size()]);
        int size=4;
        if (size==names.length&&size==title.length&&size==details.length&&size==images.length){
            for (int i = 0; i < size; i++) {
                GuideShowBean guideShowBean = new GuideShowBean();
                guideShowBean.title=names[i];
                guideShowBean.title1=title[i];
                guideShowBean.des=details[i];
                guideShowBean.img=images[i];
                list.add(guideShowBean);
            }
        }
        return list;
    }
    private void initPager() {
        viewList = new ArrayList<View>();
        List<GuideShowBean> list = getWelcomePagesByVersion();
        for (int i = 0; i < list.size(); i++) {
            viewList.add(initView(list.get(i)));
        }
        mPager.setAdapter(new ViewPagerAdapter(viewList));
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                pageSelected(arg0);
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }
    private View initView(GuideShowBean guideShowBean) {
        View view = LayoutInflater.from(ContextUtils.getAppContext()).inflate(R.layout.item_guide, null);
        ImageView imageView = view.findViewById(R.id.guide_item_img);
        imageView.setImageResource(guideShowBean.img);
        TextView title = view.findViewById(R.id.guide_item_title);
        TextView title1 = view.findViewById(R.id.guide_item_title1);
        TextView des = view.findViewById(R.id.guide_item_des);
        title.setText(guideShowBean.title);
        title1.setText(guideShowBean.title1);
        des.setText(guideShowBean.des);
        return view;
    }


    /**
     * 将底部导航栏设置透明，解决冷启动底部图标位移问题
     */
    private void hideBottomUIMenu() {
        if (getActivity() == null) {
            return;
        }
        Window window = getActivity().getWindow();
        if (window == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // 状态栏（以上几行代码必须，参考setStatusBarColor|setNavigationBarColor方法源码）
            window.setStatusBarColor(Color.TRANSPARENT);
            // 虚拟导航键
            window.setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 虚拟导航键
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.guide_skip_ok:

            case R.id.guide_ok:
                if (AndroidUtilsCompat.isFinish(getActivity()))
                    return;
                jumpHomePage();
                break;

        }
    }

    private void jumpHomePage() {
        Context cxt = getContext();
        if (cxt instanceof FragmentEvent) {
            ((FragmentEvent) cxt).OnFragmentFinished();
        }
    }
}
