package com.cashLoan.money.language;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;


import com.cashLoan.money.R;

import java.util.List;

/**
 * Created by liruidong on 2019/3/11.
 */

public class LanguageSelectAdapter extends RecyclerView.Adapter {

    private OnItemClickListener mItemClickListener;
    private final int TYPE_NORMAL = 0;

    private List<LanguageItem> mDataList;

    public void setData(List<LanguageItem> list) {
        mDataList = list;
        notifyDataSetChanged();
    }

    public void setItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_NORMAL) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.language_select_item, parent, false);
            return new LanguageSelectHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case TYPE_NORMAL:
                LanguageSelectHolder mineOpHolder = (LanguageSelectHolder) holder;
                final LanguageItem languageItem = mDataList.get(position);
                mineOpHolder.bind(languageItem);
                View root = mineOpHolder.getRoot();
                if (root != null) {
                    root.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mItemClickListener != null) {
                                mItemClickListener.onItemClick(v, position, languageItem);
                            }
                        }
                    });
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_NORMAL;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position, LanguageItem language);
    }
}
