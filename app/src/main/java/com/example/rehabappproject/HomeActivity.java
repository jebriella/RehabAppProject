package com.example.rehabappproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

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

import java.io.BufferedReader;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.opencsv.CSVReader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private final float week = 7f;
    private final float month = 31f;
    private float goal;

    private ArrayList<BarEntry> barEntries;
    private ArrayList<String> dayNameList;
    private ArrayList<String> dateNameList;


    private Button weekButton;
    private Button monthButton;
    private Button yearButton;

    private ImageView userImage;
    private TextView userWelcomeText;


    private final Context context = GlobalApplication.getAppContext();
    private long timeOfLastBackPressed = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.squarelogo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        testBar();

        StatusBarHandler statusBarHandler = new StatusBarHandler((ImageView) findViewById(R.id.userImg),
                (TextView) findViewById(R.id.welcomeText), true); //TODO It would be good if this could be stored somehow and used w/o creating a new one on every activity
    }

    private void testBar() {
        DateFormatSymbols dfs = new DateFormatSymbols();

        // Generate example data for the bar chart
        barEntries = new ArrayList<>();
        dayNameList = new ArrayList<>();
        dateNameList = new ArrayList<>();
        int dayNr = 1;
        int dateNr = 1;
        for (int i = 0; i < 30; i++) { //one month of sample data
            if (dayNr > 7) {
                dayNr = 1;
            }
            if (dateNr > 31) {
                dateNr = 1;
            }
            float value = (int) (Math.random() * 200);
            barEntries.add(new BarEntry(i, value));
            dayNameList.add(dfs.getShortWeekdays()[dayNr]);

            dateNameList.add(dateNr + " Oktober");
            dayNr++;
            dateNr++;
        }

        goal = 100f; // indicator line height

        //Populate bar chart
        plotWeekBarChart(barEntries, dayNameList, goal);

        weekButton = findViewById(R.id.weekButton);
        monthButton = findViewById(R.id.monthButton);
        yearButton = findViewById(R.id.yearButton);
    }

    public void lastSessionClicked (View view){
        startActivity(new Intent(getApplicationContext(), LastSessionActivity.class));
    }
    public void trackExerciseClicked (View view){

    }

    public void connectClicked(View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        long time = System.currentTimeMillis();
        if (time - timeOfLastBackPressed > 2000) {    // if less than 2 seconds, exit
            timeOfLastBackPressed = time;
            Toast.makeText(this, "Press back again to exit",
                    Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                finish();
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }

    public void monthClick(View view) {
        plotMonthBarChart(barEntries, dateNameList, goal);
    }

    private void plotMonthBarChart(ArrayList entries, ArrayList dayNameList, float goal) {

        BarChart barChart = plotBarChart(entries, dateNameList, goal, month);

        plotIndicatorLine(goal, barChart);

    }

    public void weekClick(View view) {
        plotWeekBarChart(barEntries, dayNameList, goal);
    }

    private void plotWeekBarChart(ArrayList entries, ArrayList labels, float goal) {

        BarChart barChart = plotBarChart(entries, dayNameList, goal, week);

        plotIndicatorLine(goal, barChart);

    }

    public void yearClick(View view) {
        plotYearBarChart(barEntries, dayNameList, goal);
    }

    private void plotYearBarChart(ArrayList entries, ArrayList labels, float goal) {
        //TODO Plot year
    }

    private void plotIndicatorLine(float goal, BarChart barChart) {
        String goalString = Float.toString(goal);
        String star = Character.toString((char) 9733);
        String indicatorString = star.concat(" ").concat(goalString).concat(" ").concat(star).concat("   ");

        LimitLine iLine = new LimitLine((int) goal, indicatorString);
        iLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        iLine.setTextSize(10f);
        iLine.setTextColor(Color.GRAY);
        iLine.setLineColor(getResources().getColor(R.color.colorAccent));
        iLine.setLineWidth(2f);
        barChart.getAxisLeft().addLimitLine(iLine);

    }

    private BarChart plotBarChart(ArrayList<BarEntry> entries, ArrayList labels, float goal, float noDays) {

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


        //grid lines
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);

        // legend
        barChart.getLegend().setEnabled(false);
        barChart.getDescription().setEnabled(false);

        // viewport
        barChart.resetViewPortOffsets();
        barChart.resetTracking();
        barChart.restoreDefaultFocus();
        barChart.setVisibleXRange(noDays, noDays);
        barChart.moveViewToX(60); //move focus to the right
        barChart.invalidate();

        barChart.animateY(1000);

        return barChart;

    }


}

class myCSVReader {

    private Context context;
    private String url;

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
        assert is != null;
        BufferedReader reader = new BufferedReader(is);
        return readDataRowByRow(reader);
    }

        private ArrayList<Double> readDataRowByRow(BufferedReader reader) {
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
