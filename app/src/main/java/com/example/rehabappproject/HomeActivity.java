package com.example.rehabappproject;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.DateFormatSymbols;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    float week = 7f;
    float month = 31f;
    float goal;

    ArrayList<BarEntry> entries;
    ArrayList<String> dayNameList;
    ArrayList<String> dateNameList;

    Button weekButton;
    Button monthButton;
    Button yearButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        DateFormatSymbols dfs = new DateFormatSymbols();

        entries = new ArrayList<>();
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
            entries.add(new BarEntry(value, i));
            dayNameList.add(dfs.getShortWeekdays()[dayNr]);
            dateNameList.add(dateNr + " Oktober");
            dayNr++; dateNr++;
        }

        goal = 7f;

        plotWeekBarChart(entries, dayNameList, goal);

        weekButton = (Button) findViewById(R.id.weekButton);
        monthButton = (Button) findViewById(R.id.monthButton);
        yearButton = (Button) findViewById(R.id.yearButton);

    }

    public void monthClick(View view){
        plotMonthBarChart(entries, dateNameList, goal);
    }
    private void plotMonthBarChart (ArrayList entries, ArrayList dayNameList, float goal){

        BarChart barChart = plotBarChart(entries, dateNameList, goal, month);

       plotIndicatorLine(goal, barChart);

    }

    public void weekClick(View view){
        plotWeekBarChart(entries, dayNameList, goal);
    }
    private void plotWeekBarChart (ArrayList entries, ArrayList labels, float goal){


        BarChart barChart = plotBarChart(entries, dayNameList, goal, week);


        plotIndicatorLine(goal, barChart);


    }

    public void yearClick(View view){
        plotYearBarChart(entries, dayNameList, goal);
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

        BarChart barChart = (BarChart) findViewById(R.id.barchart);
        barChart.clear();

        BarDataSet bardataset = new BarDataSet(entries, "Label");

        BarData data = new BarData(labels, bardataset);
        barChart.setData(data); // set the data and list of dayNameList into chart
        barChart.setDescription("");  // set the description

        // FORMATTING

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
}
