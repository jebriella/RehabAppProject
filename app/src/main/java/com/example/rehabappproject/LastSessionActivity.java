package com.example.rehabappproject;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;

import java.util.ArrayList;
import java.util.List;

public class LastSessionActivity extends AppCompatActivity {

    Context context = GlobalApplication.getAppContext();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_session);

        new StatusBarHandler((ImageView) findViewById(R.id.userImg),
                (TextView) findViewById(R.id.welcomeText), false);

        testScatter();
    }

    private void testScatter(){
        // read angle data from file to array list variable
        myCSVReader txtReader = new myCSVReader("angle_example_data.csv" ,context );
        ArrayList<Double> angleData = txtReader.readTxt();

        //generate 50hz timestamp AL to go with the above
        ArrayList<Float> angleDataTimeStamp = new ArrayList<>();
        float t =0.0f;
        for (int i=0; i<angleData.size()/50+1 ;i++) {
            for (int j=0; j<50; j++) {
                if ((i < (angleData.size() / 50)) || (j < Math.floorMod(angleData.size(), 50))) {
                    angleDataTimeStamp.add(t+(1.0f/50)*j);
                }
            }
            t++;
        }

        // Populate scatter plot of angle data
        ArrayList<Entry> scatterEntries = new ArrayList<>();
        ArrayList<Integer> colorList = new ArrayList<>();
        for (int i=0; i<angleData.size(); i++){
            Entry e = new Entry( angleDataTimeStamp.get(i), -angleData.get(i).floatValue() ); // multiplied with - to invert plotted curve
            scatterEntries.add(e);
            double ad = angleData.get(i);
            if (ad > 5 && ad <45 ){ //between 15 and 40
                colorList.add(getResources().getColor(R.color.colorAccent));
            } else if (ad < 5 || ad < 60 ) { // under 15 or between 40 and 60
                colorList.add(getResources().getColor(R.color.colorSTrans));
            } else if (ad > 60 ) {  //over 60
                colorList.add(getResources().getColor(R.color.colorClearRed));
            }
        }
        float timeSpan = angleData.size();
        plotScatterChart(scatterEntries, timeSpan, colorList);
    }

    private ScatterChart plotScatterChart(ArrayList<Entry> entries, float timeSpan, ArrayList<Integer> colorList) {

        ScatterChart scatterChart = findViewById(R.id.scatterplot);
        scatterChart.clear();

        ScatterDataSet scatterDataSet = new ScatterDataSet(entries, "Label");
        scatterDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        scatterDataSet.setColors(colorList);

        List<IScatterDataSet> dataSets = new ArrayList<>();
        dataSets.add(scatterDataSet);

        ScatterData data = new ScatterData(dataSets);
        scatterChart.setData(data); // set the data and list of dayNameList into chart

        // STYLING

        //borders
        scatterChart.setDrawBorders(false);

        //axises
        scatterChart.getAxisRight().setDrawAxisLine(false);
        scatterChart.getXAxis().setDrawAxisLine(false);
        scatterChart.getAxisRight().setDrawLabels(false);

        //grid lines
        scatterChart.getAxisRight().setDrawGridLines(true);
        scatterChart.getAxisLeft().setDrawGridLines(true);
        scatterChart.getXAxis().setDrawGridLines(true);

        // legend
        scatterChart.getLegend().setEnabled(false);

        // label
        scatterChart.getDescription().setEnabled(false);

        //zoom
        scatterChart.setPinchZoom(false);

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
