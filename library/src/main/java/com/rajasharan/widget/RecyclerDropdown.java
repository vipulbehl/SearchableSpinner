package com.rajasharan.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajasharan on 8/28/15.
 */
public class RecyclerDropdown extends RecyclerView {
    private static final String TAG = "RecyclerDropdown";
    private static final CharSequence [] EMPTY_LIST = new CharSequence[0];

    private CharSequence [] mList;
    private CharSequence [] mOriginalList;
    private Adapter mAdapter;

    public RecyclerDropdown(Context context) {
        this(context, null);
    }

    public RecyclerDropdown(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerDropdown(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mAdapter = new Adapter();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setDropdownList(null, null);
        setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        addItemDecoration(new BitmapDecorator(getContext()));
    }

    public void setDropdownList(CharSequence [] list, OnClickListener listener) {
        if (list == null) {
            mList = EMPTY_LIST;
        } else {
            mList = list;
        }
        mAdapter.init(mList, listener);
        setAdapter(mAdapter);
    }

    public void filter(String str, OnClickListener listener) {
        if (mOriginalList == null) {
            mOriginalList = mList.clone();
        }
        List<CharSequence> list = new ArrayList<>();
        for (CharSequence cs: mOriginalList) {
            if (cs.toString().toUpperCase().contains(str.toUpperCase())) {
                list.add(cs);
            }
        }
        mList = list.toArray(new CharSequence[0]);
        setDropdownList(mList, listener);
    }

    private static class Adapter extends RecyclerView.Adapter<Holder> {
        private CharSequence [] mList;
        private OnClickListener mListener;
        public void init(CharSequence [] list, OnClickListener listener) {
            mList = list;
            mListener = listener;
        }
        @Override
        public Holder onCreateViewHolder(ViewGroup viewGroup, int type) {
            LayoutInflater inflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.recycler_itemviews, viewGroup, false);
            view.setDrawingCacheEnabled(true);
            Holder holder = new Holder(view);
            holder.mTextView.setGravity(Gravity.CENTER);
            holder.mTextView.setOnClickListener(mListener);
            return holder;
        }
        @Override
        public void onBindViewHolder(Holder holder, int pos) {
            holder.mTextView.setText(mList[pos]);
        }
        @Override
        public int getItemCount() {
            return mList.length;
        }
    }

    private static class Holder extends ViewHolder {
        private TextView mTextView;
        public Holder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
        }
    }

    private static class BitmapDecorator extends ItemDecoration {
        private Paint mWhitePaint;

        public BitmapDecorator(Context context) {
            mWhitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mWhitePaint.setStyle(Paint.Style.FILL);
            mWhitePaint.setColor(Color.WHITE);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, State state) {
            transformChildren(c, parent);
        }

        private void transformChildren(Canvas canvas, RecyclerView parent) {
            int childCount = parent.getChildCount();
            int middlePos = (childCount - 1)/2;

            canvas.drawColor(Color.WHITE);

            for (int i=0; i < childCount; i++) {
                if (i == middlePos) {
                    continue;
                }
                View child = parent.getChildAt(i);
                Bitmap b = child.getDrawingCache();
                canvas.drawBitmap(b, child.getLeft(), child.getTop(), null);
            }

            View child = parent.getChildAt(middlePos);
            Rect r = new Rect();
            child.getHitRect(r);
            Bitmap b = child.getDrawingCache();

            canvas.save();
            canvas.scale(2.0f, 2.0f, r.centerX(), r.centerY());
            canvas.drawBitmap(b, child.getLeft(), child.getTop(), null);
            canvas.restore();
        }
    }
}