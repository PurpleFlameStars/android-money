package com.dzfd.gids.baselibs.UI.widgets;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dzfd.gids.baselibs.R;

public class LoadingDialog extends ProgressDialog {

    private String mText = "loading";
    private TextView textView;
    private ProgressBar dialog;

    /**
     * Creates a new instance of LoadingDialog.
     *
     * @param context context
     */
    public LoadingDialog(Context context) {
        this(context, R.style.loading_dialog_style);
    }

    /**
     * Creates a new instance of LoadingDialog.
     *
     * @param context context
     * @param theme   theme
     */
    public LoadingDialog(Context context, int theme) {
        super(context, R.style.loading_dialog_style);
    }

    /**
     * @param savedInstanceState savedInstanceState
     * @see ProgressDialog#onCreate(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        textView = (TextView) findViewById(R.id.loading_text);
        dialog = (ProgressBar) findViewById(R.id.loading_bar);
        mText = getContext().getResources().getString(R.string.loading);
        changeText();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
    }

    // 改变提示文字
    private void changeText() {
        if (textView != null) {
            textView.setText(mText);
        }
    }

    /**
     * 设置提示文字，会在界面创建完成后生效。
     *
     * @param text 提示文字内容
     */
    public void setText(String text) {
        mText = text;
        changeText();
    }

    /**
     * LoadingDialog 创建器
     *
     */
    public static class Builder {
        private Context mContext;
        private String mText;
        private boolean mCancelable;
        private LoadingDialog myProgress;

        /**
         * Creates a new instance of Builder.
         *
         * @param context context
         */
        public Builder(Context context) {
            mContext = context;
        }

        /**
         * 设置提示文字
         *
         * @param textID 文字资源ID
         * @return Builder
         */
        public Builder setText(int textID) {
            mText = (String) mContext.getText(textID);
            return this;
        }

        /**
         * 设置提示文字
         *
         * @param text 文字内容
         * @return Builder
         */
        public Builder setText(String text) {
            mText = text;
            return this;
        }

        /**
         * 设置是否可取消
         *
         * @param cancelable 是否可取消
         * @return Builder
         */
        public Builder setCancelable(boolean cancelable) {
            mCancelable = cancelable;
            return this;
        }

        /**
         * 创建LoadingDialog
         *
         * @return LoadingDialog示例
         */
        public LoadingDialog create() {
            myProgress = new LoadingDialog(mContext);
            myProgress.setCancelable(mCancelable);
            if (mText != null) {
                myProgress.setText(mText);
            }
            return myProgress;
        }
    }


}