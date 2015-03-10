package com.messedagliavr.messeapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.messedagliavr.messeapp.R;

public class NavDrawerListAdapter extends BaseAdapter {

    private Context context;
    private String[] navDrawerItems;
    private String[] images;

    public NavDrawerListAdapter(Context context, String[] navDrawerItems, String [] images){
        this.context = context;
        this.navDrawerItems = navDrawerItems;
        this.images= images;
    }

    @Override
    public int getCount() {
        return navDrawerItems.length;
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_item, null);
        }

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.text1);


        imgIcon.setImageResource(context.getResources().getIdentifier(images[position],"drawable",context.getPackageName()));
        txtTitle.setText(navDrawerItems[position]);

        return convertView;
    }

}
