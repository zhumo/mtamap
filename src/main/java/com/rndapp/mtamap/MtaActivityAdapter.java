package com.rndapp.mtamap;
/**
 * Created by ladmin on 10/06/16.
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class MtaActivityAdapter extends BaseAdapter implements Filterable, AdapterView.OnItemClickListener {

    private final Context context;
    ArrayList<Station> stationsArrayList;
    ArrayList<Station> stationFilterArray;
    ValueFilter valueFilter;
    DBHelper dbHelper;
    Stack<AsyncTask> pendingTaskQueue = new Stack<>();
    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    MtaActivityAdapter(Context context, ArrayList<Station> stationList, DBHelper dbHelper) {
        this.context = context;
        this.stationsArrayList = stationList;
        stationFilterArray = new ArrayList<>(this.stationsArrayList);
        this.dbHelper = dbHelper;
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
        Station stationObj = stationFilterArray.get(position);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        txtTitle.setText(stationObj.name);
        if(stationObj.colors==null || stationObj.colors.size()==0)
            retrieveStationForLines(new LinesForStationTask(stationObj,rowView));
        else
            setColors(stationObj.colors,rowView);

        return rowView;
    }

    class LinesForStationTask extends AsyncTask<String,Integer,String>{
        final Station station;
        final View rootView;
        ArrayList<Integer> colors = new ArrayList<>();
        LinesForStationTask(Station station, View rootView){
            this.station = station;
            this.rootView = rootView;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            setColors(colors,rootView);
            station.colors = colors;
            executePendingTask();
        }

        @Override
        protected String doInBackground(String... params) {
            ArrayList<LineViewModel> lineModelArrayList = linesForStation(station);
            for (int i=0;i<lineModelArrayList.size();i++){
                LineViewModel model = lineModelArrayList.get(i);
                colors.add(model.color);
            }
            return null;
        }
    }
    private void setColors(ArrayList<Integer> colors,View rootView){
        ImageView routeOneImg = (ImageView) rootView.findViewById(R.id.imageView1);
        ImageView routeTwoImg = (ImageView) rootView.findViewById(R.id.imageView2);
        ImageView routeThreeImg = (ImageView) rootView.findViewById(R.id.imageView3);
        ImageView routeFourImg = (ImageView) rootView.findViewById(R.id.imageView4);
        ImageView [] images = {routeOneImg,routeTwoImg,routeThreeImg,routeFourImg};
        HashMap<String,Integer> map = new HashMap<>();
        int imgIndex = 0;
        for (int i=0;i<colors.size()&& imgIndex<4;i++){
            if(!map.containsKey(colors.get(i)+"")) {
                images[imgIndex].setImageDrawable(new ColorDrawable(colors.get(i)));
                map.put(colors.get(i)+"",colors.get(i));
                imgIndex++;
            }
        }
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB) // API 11
    private void retrieveStationForLines(AsyncTask asyncTask){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                pendingTaskQueue.add(asyncTask);
                if(executor.getActiveCount()<executor.getMaximumPoolSize())
                    pendingTaskQueue.pop().executeOnExecutor(executor, null);
            }
            else
                asyncTask.execute(null);
    }
    private void executePendingTask(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if(pendingTaskQueue.size()>0 && executor.getActiveCount()<executor.getMaximumPoolSize())
                pendingTaskQueue.pop().executeOnExecutor(executor, null);
        }
    }

    public ArrayList<LineViewModel> linesForStation(Station station) {

        NYCRouteColorManager colorManager = new NYCRouteColorManager();
        ArrayList<LineViewModel> lineModelsArrayList = new ArrayList<LineViewModel>();
        ArrayList<String> routeIdsArrayList = dbHelper.routeIdsForStation(station);

        for (String routeId : routeIdsArrayList) {
            LineViewModel lineModel = new LineViewModel();
            lineModel.routeIdsArrayList.add(routeId);
            lineModel.color = colorManager.colorForRouteId(routeId);
            int lineIndex = getModelIndex(lineModelsArrayList,lineModel);

            if (lineIndex > 0) {
                if (!isContainValue(lineModelsArrayList.get(lineIndex).routeIdsArrayList,routeId)) {
                    lineModelsArrayList.get(lineIndex).routeIdsArrayList.add(routeId);
                } else {
                    lineModelsArrayList.add(lineModel);
                }
            } else {
                lineModelsArrayList.add(lineModel);
            }
        }
        return lineModelsArrayList;
    }

    private int getModelIndex(ArrayList<LineViewModel> lineModels,LineViewModel newModel){
        int lineIndex = 0;
        for (LineViewModel model:lineModels){
            if(model.color==newModel.color && model.routeIdsArrayList.size()== newModel.routeIdsArrayList.size()){
                int counter = 0;
                for (int j=0;j<model.routeIdsArrayList.size();j++) {
                    if(model.routeIdsArrayList.get(j).equals(newModel.routeIdsArrayList.get(j))){
                        counter++;
                    }
                }
                if(counter==model.routeIdsArrayList.size()){
                    lineIndex++;
                }
            }

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
        Station stationObj = (Station) stationFilterArray.get(position);
        Date time = new Date();

        ArrayList<Prediction> predictionsArrayList = dbHelper.getPredictions(stationObj, time);
        Intent intent = new Intent(context, StationsDetailActivity.class);
        intent.putExtra("station", stationObj); //Optional parameters
        intent.putExtra("preArray", predictionsArrayList);
        context.startActivity(intent);
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<Station> filterList = new ArrayList<Station>();
                for (int i = 0; i < stationsArrayList.size(); i++) {
                    if ((stationsArrayList.get(i).getName().toUpperCase()).contains(constraint.toString().toUpperCase())) {
                        Station stationObj = stationsArrayList.get(i);
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
