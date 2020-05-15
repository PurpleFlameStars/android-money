package com.cashLoan.money.language;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cashLoan.money.R;

public class LanguageSelectHolder extends RecyclerView.ViewHolder {
    private View root;
    private TextView languageText;

    public LanguageSelectHolder(View itemView) {
        super(itemView);
        root = itemView.findViewById(R.id.language_root);
        languageText = itemView.findViewById(R.id.language_text);
    }

    public void bind(LanguageItem data) {
        if (data == null) {
            return;
        }
        languageText.setText(data.languageText);
        languageText.setSelected(data.selected);
    }

    public View getRoot() {
        return root;
    }
}
