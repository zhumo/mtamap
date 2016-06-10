package com.subway.ladmin.subwaynyk;
/**
 * Created by ladmin on 10/06/16.
 */

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter implements Filterable {

        private final Context context;
        ArrayList<Station> stationArray;
        ArrayList<Station> mStringFilterList;
        ValueFilter valueFilter;

    CustomAdapter(Context context, ArrayList<Station> stationList) {
        this.context = context;
        this.stationArray = stationList;
        mStringFilterList = stationList;
    }

    @Override
    public int getCount() {
        return stationArray.size();
    }

    @Override
    public Object getItem(int position) {
        return stationArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return stationArray.indexOf(getItem(position));
    }




    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            View rowView=mInflater.inflate(R.layout.list_item, null,true);
            TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
            ImageView imageView1 = (ImageView) rowView.findViewById(R.id.imageView1);
            imageView1.setBackgroundColor(Color.rgb(100, 100, 50));
            ImageView imageView2 = (ImageView) rowView.findViewById(R.id.imageView2);
            imageView2.setBackgroundColor(Color.rgb(100, 50, 100));
            ImageView imageView3 = (ImageView) rowView.findViewById(R.id.imageView3);
            imageView3.setBackgroundColor(Color.rgb(50, 50, 50));
            ImageView imageView4 = (ImageView) rowView.findViewById(R.id.imageView4);
            imageView4.setBackgroundColor(Color.rgb(20, 20, 20));


            Station newStation = stationArray.get(position);
            txtTitle.setText(newStation.name);
            return rowView;

        };

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                ArrayList<Station> filterList = new ArrayList<Station>();
                for (int i = 0; i < mStringFilterList.size(); i++) {
                    if ((mStringFilterList.get(i).getName().toUpperCase())
                            .contains(constraint.toString().toUpperCase())) {

                        Station stationObj = new Station(mStringFilterList.get(i).getName() );
                        filterList.add(stationObj);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mStringFilterList.size();
                results.values = mStringFilterList;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            stationArray = (ArrayList<Station>) results.values;
            notifyDataSetChanged();
        }

    }

}
