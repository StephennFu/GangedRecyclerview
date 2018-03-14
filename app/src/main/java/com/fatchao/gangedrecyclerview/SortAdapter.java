package com.fatchao.gangedrecyclerview;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import java.util.List;

public class SortAdapter extends RvAdapter<String> {

    private final static String TAG = "SortAdapter";

    private int checkedPosition;

    public void setCheckedPosition(int checkedPosition, int flag) {
        this.checkedPosition = checkedPosition;
        Log.d(TAG, "setCheckedPosition: checkedPosition = " + checkedPosition + "(" + flag + ")");
        notifyDataSetChanged();
    }

    public SortAdapter(Context context, List<String> list, RvListener listener) {
        super(context, list, listener);
    }

    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.item_sort_list;
    }

    @Override
    protected RvHolder getHolder(View view, int viewType) {
        return new SortHolder(view, viewType, listener);
    }

    private class SortHolder extends RvHolder<String> {

        private TextView tvName;
        private View mView;

        SortHolder(View itemView, int type, RvListener listener) {
            super(itemView, type, listener);
            this.mView = itemView;
            tvName = (TextView) itemView.findViewById(R.id.tv_sort);
        }

        @Override
        public void bindHolder(String string, int position) {
            tvName.setText(string);
            if (position == checkedPosition) {
                Log.d(TAG, "bindHolder: checkedPosition = position = " + checkedPosition);
                mView.setBackgroundColor(Color.parseColor("#f3f3f3"));
                tvName.setTextColor(Color.parseColor("#0068cf"));
            } else {
                Log.d(TAG, "bindHolder: position = " + position + ", checkedPosition = " + checkedPosition);
                mView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                tvName.setTextColor(Color.parseColor("#1e1d1d"));
            }
        }

    }
}
