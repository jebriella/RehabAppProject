package com.example.rehabappproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    float week = 7f;
    float month = 31f;
    float goal;

    ArrayList<BarEntry> barEntries;
    ArrayList<String> dayNameList;
    ArrayList<String> dateNameList;

    ArrayList<Entry> scatterEntries;

    Button weekButton;
    Button monthButton;
    Button yearButton;

    private Context context = GlobalApplication.getAppContext();
    

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        DateFormatSymbols dfs = new DateFormatSymbols();

        // Generate example data for the bar chart
        barEntries = new ArrayList<>();
        dayNameList = new ArrayList<String>();
        dateNameList = new ArrayList<>();
        int dayNr =1; int dateNr =1;
        for (int i =0 ; i<60; i++){
            if (dayNr>7){
                dayNr=1;
            }
            if (dateNr>31){
                dateNr=1;
            }
            float value = (int) (Math.random() * 100);
            barEntries.add(new BarEntry(i, value ));
            dayNameList.add(dfs.getShortWeekdays()[dayNr]);

            dateNameList.add(dateNr + " Oktober");
            dayNr++; dateNr++;
        }

        goal = 20f; // indicator line height

        //Populate bar chart
        plotWeekBarChart(barEntries, dayNameList, goal);

        weekButton = findViewById(R.id.weekButton);
        monthButton = findViewById(R.id.monthButton);
        yearButton = findViewById(R.id.yearButton);

        // read angle data from file to arraylist varible
            myCSVReader txtReader = new myCSVReader("angle_example_data.csv" ,context );
            ArrayList<Double> angleData = txtReader.readTxt();

            //generate 50hz timestamp AL to go with the above
        ArrayList<Float> angleDataTimeStamp = new ArrayList<>();
        Float t =0.0f;
        Math.floorMod(angleData.size(), 50);
        for (int i=0; i<angleData.size()/50+1 ;i++) {
            for (int j=0; j<50; j++) {
                if ((i < (angleData.size() / 50)) || (j < Math.floorMod(angleData.size(), 50))) {
                    angleDataTimeStamp.add(t+(1.0f/50)*j);
                }
            }
            t++;
        }


        // Populate scatter plot of angle data
        scatterEntries = new ArrayList<>();
        ArrayList<Integer> colorList = new ArrayList<>();
        for (int i=0; i<angleData.size(); i++){
            Entry e = new Entry( angleDataTimeStamp.get(i), angleData.get(i).floatValue() ); // multiplied with - to invert plotted curve
            scatterEntries.add(e);
            if (angleData.get(i)<40){
                colorList.add(getResources().getColor(R.color.colorAccent));
            } else if (angleData.get(i) < 60 ) {
                colorList.add(getResources().getColor(R.color.colorSTrans));
            } else if (angleData.get(i) > 60 ) {
                colorList.add(getResources().getColor(R.color.colorDarkBlue));
            }
        }
        float timeSpan = angleData.size();
        plotScatterChart(scatterEntries, "Angle", timeSpan, colorList);

    }

    public void homeClicked(View view){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    public void btClicked(View view){
        startActivity(new Intent(getApplicationContext(), DeviceScanActivity.class));
        //finish();
    }

    public void logOutButtonClicked(View V) {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        // startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    }
                });
    }

    public void monthClick(View view){
        plotMonthBarChart(barEntries, dateNameList, goal);
    }
    private void plotMonthBarChart (ArrayList entries, ArrayList dayNameList, float goal){

        BarChart barChart = plotBarChart(entries, dateNameList, goal, month);

       plotIndicatorLine(goal, barChart);

    }

    public void weekClick(View view){
        plotWeekBarChart(barEntries, dayNameList, goal);
    }
    private void plotWeekBarChart (ArrayList entries, ArrayList labels, float goal){

        BarChart barChart = plotBarChart(entries, dayNameList, goal, week);

        plotIndicatorLine(goal, barChart);

    }

    public void yearClick(View view){
        plotYearBarChart(barEntries, dayNameList, goal);
    }
    private void plotYearBarChart (ArrayList entries, ArrayList labels, float goal){

    }

    private void plotIndicatorLine (float goal, BarChart barChart) {
        String goalString = Float.toString(goal) ;
        String star = Character.toString( (char) 9733 );
        String indicatorString = star.concat(" ").concat(goalString).concat(" ").concat(star).concat("   ");

        LimitLine iLine = new LimitLine((int) goal, indicatorString);
        iLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        iLine.setTextSize(10f);
        iLine.setTextColor(Color.GRAY);
        iLine.setLineColor(getResources().getColor(R.color.colorAccent));
        iLine.setLineWidth(2f);
        barChart.getAxisLeft().addLimitLine(iLine);

    }

    private BarChart plotBarChart(ArrayList entries, ArrayList labels, float goal, float noDays) {

        BarChart barChart = findViewById(R.id.barchart);
        barChart.clear();

        BarDataSet bardataset = new BarDataSet(entries, "Example Label");

        BarData data = new BarData(bardataset);
        barChart.setData(data); // set the data and list of dayNameList into chart
        //barChart.setFitBars(true);
        //barChart.setDescription("");  // set the description

        // STYLING

        //bars
        bardataset.setColor(getResources().getColor(R.color.colorSTrans));

        //borders
        barChart.setDrawBorders(false);

        //Values
        bardataset.setDrawValues(false);

        //axises
        barChart.getAxisRight().setDrawAxisLine(false);
        barChart.getXAxis().setDrawAxisLine(false);
        //  barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);  //move x dayNameList below
        barChart.getAxisRight().setDrawLabels(false);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));


        //gridlines
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);

        // legend
        barChart.getLegend().setEnabled(false);

        // viewport
        barChart.resetViewPortOffsets();
        barChart.resetTracking();
        barChart.restoreDefaultFocus();
        barChart.setVisibleXRange(noDays,noDays);
        barChart.moveViewToX(60); //move focus to the right
        barChart.invalidate();

        barChart.animateY(1000);

        return barChart;

    }


    private ScatterChart plotScatterChart(ArrayList<Entry> entries, String labels, float timeSpan, ArrayList<Integer> colorList) {

        ScatterChart scatterChart = findViewById(R.id.scatterplot);
        scatterChart.clear();

        /*
        entries.clear();
        colorList.clear();

        for (int i=0; i<30; i++){
            Entry e = new Entry(i, i); // multiplied with - to invert plotted curve
            entries.add(e);
            if (i<10){
                colorList.add(getResources().getColor(R.color.colorSTrans));
            }else if (i<20) {
                colorList.add(getResources().getColor(R.color.colorDarkBlue));
            } else {
                colorList.add(getResources().getColor(R.color.fui_bgGitHub));
            }
        }


         */
        ScatterDataSet scatterDataSet = new ScatterDataSet(entries, "Label");
        scatterDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        scatterDataSet.setColors(colorList);

        List<IScatterDataSet> dataSets = new ArrayList<>();
        dataSets.add(scatterDataSet);

        ScatterData data = new ScatterData(dataSets);
        scatterChart.setData(data); // set the data and list of dayNameList into chart

        // STYLING

        //bars
        //scatterChart.setColor(getResources().getColor(R.color.colorSTrans));

        //borders
        //barChart.setDrawBorders(false);

        //Values
        //bardataset.setDrawValues(false);

        //axises
        //barChart.getAxisRight().setDrawAxisLine(false);
        //barChart.getXAxis().setDrawAxisLine(false);
        //  barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);  //move x dayNameList below
        //barChart.getAxisRight().setDrawLabels(false);

        //gridlines
        //barChart.getAxisRight().setDrawGridLines(false);
        //barChart.getAxisLeft().setDrawGridLines(false);
        //barChart.getXAxis().setDrawGridLines(false);

        // legend
        //barChart.getLegend().setEnabled(false);

        // viewport
        //barChart.resetViewPortOffsets();
        //barChart.resetTracking();
        //barChart.restoreDefaultFocus();
        //barChart.setVisibleXRange(noDays,noDays);
        //barChart.moveViewToX(60); //move focus to the right
        scatterChart.invalidate();

        scatterChart.animateY(1000);

        return scatterChart;

    }




}

class myCSVReader {

    Context context;
    String url;

    myCSVReader(String url, Context context) {
        this.url = url;
        this.context = context;
    }

    public ArrayList<Double> readTxt() {

        InputStreamReader is = null;
        try {
            is = new InputStreamReader(context.getAssets().open(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(is);
        return readDataRowByRow(reader);
    }

        public ArrayList<Double> readDataRowByRow(BufferedReader reader) {
            ArrayList<Double> angleData = new ArrayList<>();
            try {
                CSVReader csvReader = new CSVReader(reader);
                String[] nextRecord;
                NumberFormat nf = NumberFormat.getNumberInstance();

                while ((nextRecord = csvReader.readNext()) != null) {
                    for (String cell : nextRecord) {
                        cell = cell.replaceAll("\\uFEFF", ""); //removing very annoying invisible UNICODE character. Go away!!
                        angleData.add(Double.parseDouble(cell.trim())); //trim to make sure no whitespaces are present
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return angleData;
        }
    }
