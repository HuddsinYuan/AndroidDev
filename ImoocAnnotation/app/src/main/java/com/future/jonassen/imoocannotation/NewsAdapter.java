package com.future.jonassen.imoocannotation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Jonassen on 15/11/15.
 */
public class NewsAdapter extends BaseAdapter implements AbsListView.OnScrollListener
{

    private List<NewsBean> mList;
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private int mStart, mEnd;
    public static String[] URLS;
    private boolean mFirstInit;

    public NewsAdapter(Context context, List<NewsBean> data, ListView listview)
    {
        mFirstInit = true;
        mList = data;
        mInflater = LayoutInflater.from(context);
        mImageLoader = new ImageLoader(listview);
        URLS = new String[data.size()];

        for (int i = 0; i < data.size(); i++)
        {
            URLS[i] = data.get(i).newsIconUrl;
        }
        listview.setOnScrollListener(this);
    }

    @Override
    public int getCount()
    {
        return mList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder = null;
        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_layout, null);
            viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.tv_icon);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);

            convertView.setTag(viewHolder);

        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.ivIcon.setImageResource(R.mipmap.ic_launcher);
        String url = new String(mList.get(position).newsIconUrl);
        viewHolder.ivIcon.setTag(url);
        mImageLoader.showImageByAsyncTask(viewHolder.ivIcon, url);
        viewHolder.tvTitle.setText(mList.get(position).newsTitle);
        viewHolder.tvContent.setText(mList.get(position).newsContent);
        return convertView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        if (scrollState == SCROLL_STATE_IDLE)
        {
            mImageLoader.loadImages(mStart, mEnd);
        }
        else
        {
            mImageLoader.cancelAllTasks();

        }
    }

    @Override
    public void onScroll(
            AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        mStart = firstVisibleItem;
        mEnd = firstVisibleItem + visibleItemCount;
        if (mFirstInit && visibleItemCount > 0)
        {
            mImageLoader.loadImages(mStart, mEnd);
            mFirstInit = false;
        }
    }

    class ViewHolder
    {
        public TextView tvTitle, tvContent;
        public ImageView ivIcon;
    }
}
