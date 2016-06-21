package com.subway.ladmin.subwaynyk;
/**
 * Created by ladmin on 10/06/16.
 */

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivityAdapter extends BaseAdapter implements Filterable, AdapterView.OnItemClickListener {

    private final Context context;
    ArrayList<Station> stationArray;
    ArrayList<Station> stationFilterArray;
    ValueFilter valueFilter;
    DBHelper DBHelper;

    MainActivityAdapter(Context context, ArrayList<Station> stationList, DBHelper myDBHelper) {
        this.context = context;
        this.stationArray = stationList;
        stationFilterArray = new ArrayList<>(this.stationArray);
        this.DBHelper = myDBHelper;
    }

    @Override
    public int getCount() {
        return stationFilterArray.size();
    }

    @Override
    public Object getItem(int position) {
        return stationFilterArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return stationFilterArray.indexOf(getItem(position));
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item, null, true);
        Station innerStation = stationFilterArray.get(position);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        txtTitle.setText(innerStation.name);

//        ImageView imageView1 = (ImageView) rowView.findViewById(R.id.imageView1);
//        ImageView imageView2 = (ImageView) rowView.findViewById(R.id.imageView2);
//        ImageView imageView3 = (ImageView) rowView.findViewById(R.id.imageView3);
//        ImageView imageView4 = (ImageView) rowView.findViewById(R.id.imageView4);
//        ImageView [] images = {imageView1,imageView2,imageView3,imageView4};
//
//
//        ArrayList<LineViewModel> innerLineModels = linesForStation(innerStation);
//        for (int i=0;i<innerLineModels.size();i++){
//            LineViewModel model = innerLineModels.get(i);
//            ImageView imageView = images[i];
//            imageView.setBackgroundColor(model.color);
//        }

        return rowView;
    }

    public ArrayList<LineViewModel> linesForStation(Station station) {

        NYCRouteColorManager colorManager = new NYCRouteColorManager();
        ArrayList<LineViewModel> lineModels = new ArrayList<LineViewModel>();
        ArrayList<String> routeIds = DBHelper.routeIdsForStation(station);

        for (String routeId : routeIds) {
            LineViewModel lineModel = new LineViewModel();
            lineModel.routeIds.add(routeId);
            lineModel.color = colorManager.colorForRouteId(routeId);

            int lineIndex = getModelIndex(lineModels,lineModel);

            if (lineIndex > 0) {

                if (!isContainValue(lineModels.get(lineIndex).routeIds,routeId)) {
                    lineModels.get(lineIndex).routeIds.add(routeId);
                } else {
                    lineModels.add(lineModel);
                }
            } else {
                lineModels.add(lineModel);
            }
        }
        return lineModels;
    }
    private int getModelIndex(ArrayList<LineViewModel> lineModels,LineViewModel newModel){
        int lineIndex = 0;
        for (LineViewModel model:lineModels){
            if(model.color==newModel.color && model.routeIds.size()== newModel.routeIds.size()){
                return lineIndex;
            }
            lineIndex++;
        }
        return -1;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }
    private boolean isContainValue(ArrayList<String> list, String value) {
        for (String element : list) {
            if (element.equals(value))
                return true;
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Station station = (Station) stationFilterArray.get(position);
        Date time = new Date();
        ArrayList<Prediction> preArray = DBHelper.getPredictions(station, time);
        Intent intent = new Intent(context, StationsDetailActivity.class);
        intent.putExtra("station", station); //Optional parameters
        intent.putExtra("preArray", preArray);
        context.startActivity(intent);
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<Station> filterList = new ArrayList<Station>();
                for (int i = 0; i < stationArray.size(); i++) {
                    if ((stationArray.get(i).getName().toUpperCase()).contains(constraint.toString().toUpperCase())) {
                        Station stationObj = stationArray.get(i);
                        filterList.add(stationObj);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = stationFilterArray.size();
                results.values = stationFilterArray;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            stationFilterArray = (ArrayList<Station>) results.values;
            notifyDataSetChanged();
        }
    }
}
