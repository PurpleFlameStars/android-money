package com.cashLoan.money.language;

import java.io.Serializable;

public class LanguageItem implements Serializable {
    private static final long serialVersionUID = 3259134436687979739L;

    public LanguageItem(int code, String text, boolean selected) {
        this.code = code;
        this.languageText = text;
        this.selected = selected;
    }

    public LanguageItem() {

    }
    //语言代码
    public int code;
    //语言
    public String languageText;
    //是否选中
    public boolean selected;

    @Override
    public String toString() {
        return "LanguageItem{" +
                "code=" + code +
                ", languageText='" + languageText + '\'' +
                ", selected=" + selected +
                '}';
    }
}
