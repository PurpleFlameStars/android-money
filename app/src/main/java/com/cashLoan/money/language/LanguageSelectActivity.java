package com.cashLoan.money.language;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cashLoan.money.MainActivity;
import com.cashLoan.money.R;
import com.cashLoan.money.base.activity.BunBaseActivity;
import com.cashLoan.money.language.utils.LocalManageUtil;

import java.util.ArrayList;
import java.util.List;

public class LanguageSelectActivity extends BunBaseActivity implements View.OnClickListener, LanguageSelectAdapter.OnItemClickListener {
    @Override
    public int getFragmentcontainerViewId() {
        return 0;
    }

    @Override
    public int getActivityLayoutId() {
        return R.layout.activity_change_language;
    }

    @Override
    public Fragment getMainFragment() {
        return null;
    }

    @Override
    public boolean isShowImmerseLayout() {
        return true;
    }

    private View backView;
    private RecyclerView recyclerView;
    private LanguageSelectAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdpterSystemTheme();

        backView = findViewById(R.id.language_back);
        backView.setOnClickListener(this);
        recyclerView = findViewById(R.id.language_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new LanguageSelectAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setData(loadSupportLanguageList(this));
        adapter.setItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.language_back:
                finish();
                break;
        }
    }

    private void changeLanguage(int language) {
        LocalManageUtil.saveSelectLanguage(this, language);


        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private List<LanguageItem> loadSupportLanguageList(final Context context) {
        List<LanguageItem> languages = new ArrayList<>();
        if (context == null) {
            return languages;
        }
        int selectedCode = LocalManageUtil.getSelectLanguageCode(context);
        Resources resources = context.getResources();
        LanguageItem systemItem = new LanguageItem(Language.AUTO, resources.getString(R.string.following_system_language), selectedCode == Language.AUTO);
        languages.add(systemItem);
        LanguageItem chineseItem = new LanguageItem(Language.CHINESE, resources.getString(R.string.Chinese), selectedCode == Language.CHINESE);
        languages.add(chineseItem);
        LanguageItem englishItem = new LanguageItem(Language.ENGLISH, resources.getString(R.string.English), selectedCode == Language.ENGLISH);
        languages.add(englishItem);
        LanguageItem spanishItem = new LanguageItem(Language.SPANISH, resources.getString(R.string.Spanish), selectedCode == Language.SPANISH);
        languages.add(spanishItem);
        LanguageItem portugueseItem = new LanguageItem(Language.PORTUGUESE, resources.getString(R.string.Portuguese), selectedCode == Language.PORTUGUESE);
        languages.add(portugueseItem);
        return languages;
    }

    @Override
    public void onItemClick(View view, int position, LanguageItem language) {
        if (language == null) {
            return;
        }
        int currentCode = LocalManageUtil.getSelectLanguageCode(this);
        if (currentCode == language.code) {
            return;
        }

        changeLanguage(language.code);
    }
}
