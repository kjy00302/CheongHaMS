package com.kimjio.cheongha.ms.musiclist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kimjio.cheongha.ms.R;

import java.util.ArrayList;

/**
 * Created by whdghks913 on 2015-12-10.
 */
class MusicListViewHolder {
    public TextView mTitle;
    public TextView mMessage;
    public TextView mDate;
}

class MusicListShowData {
    public String title;
    public String message;
    public String date;
}

public class MusicListAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<MusicListShowData> mListData = new ArrayList<MusicListShowData>();

    public MusicListAdapter(Context mContext) {
        super();

        this.mContext = mContext;
    }

    public void addItem(String title, String message, String date) {
        MusicListShowData addItemInfo = new MusicListShowData();
        addItemInfo.title = title;
        addItemInfo.message = message;
        addItemInfo.date = date;

        mListData.add(0, addItemInfo);
    }

    public void clearData() {
        mListData.clear();
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public MusicListShowData getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        MusicListViewHolder mHolder;

        if (convertView == null) {
            mHolder = new MusicListViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_musiclist_item, null);

            mHolder.mTitle = (TextView) convertView.findViewById(R.id.mTitle);
            mHolder.mMessage = (TextView) convertView.findViewById(R.id.mMessage);
            mHolder.mDate = (TextView) convertView.findViewById(R.id.mDate);

            convertView.setTag(mHolder);
        } else {
            mHolder = (MusicListViewHolder) convertView.getTag();
        }

        MusicListShowData mData = mListData.get(position);
        String title = mData.title;
        String message = mData.message;
        String date = mData.date;

        mHolder.mTitle.setText(title);
        mHolder.mMessage.setText(message);
        mHolder.mDate.setText(date);

        return convertView;
    }
}
