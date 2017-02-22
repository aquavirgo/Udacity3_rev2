package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.entries;
import static android.R.attr.value;
import static android.R.attr.x;
import static com.github.mikephil.charting.charts.Chart.LOG_TAG;
import static com.udacity.stockhawk.R.id.symbol;

public class Graph extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, OnChartValueSelectedListener {
int position=0;
    ArrayList<Long> stockData = new ArrayList<Long>();
    ArrayList<String> stockDataString = new ArrayList<>();
    ArrayList<Double> stockPrice = new ArrayList<Double>();
    String symbol;
    ArrayList<Entry> entries;

    @BindView(R.id.detail_stock_label) TextView stockLabel;
    @BindView(R.id.detail_price_label) TextView priceLabel;
    @BindView(R.id.detail_date_label) TextView dateLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_graph);

        ButterKnife.bind(this);
    //    dateLabel = (TextView) findViewById(R.id.detail_date_label);
      //  priceLabel = (TextView) findViewById(R.id.detail_price_label);
        //stockLabel = (TextView) findViewById(R.id.detail_stock_label);


     position = getIntent().getIntExtra("stock_position", 0);

        Toast.makeText(this, position+"", Toast.LENGTH_LONG).show();
        getSupportLoaderManager().initLoader(1, null, this);

    }
/*
    @Override
protected void onResume(){
    super.onResume();

    Cursor data = getContentResolver().query(Contract.Quote.URI,
            Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
            null, null, Contract.Quote.COLUMN_SYMBOL);
    data.moveToPosition(position);
    symbol = data.getString(data.getColumnIndex(Contract.Quote.COLUMN_SYMBOL));
    String price = data.getString(data.getColumnIndex(Contract.Quote.COLUMN_PRICE));
    String absChnage = data.getString(data.getColumnIndex(Contract.Quote.COLUMN_ABSOLUTE_CHANGE));
    String hist = data.getString(data.getColumnIndex(Contract.Quote.COLUMN_HISTORY));
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    String[] test = hist.split("\n");

    for (int i = 0; i < test.length; i++) {

        String[] dataTermin=test[i].split(", ");
        stockData.add(Long.valueOf(dataTermin[0]));
        stockPrice.add(Double.valueOf(dataTermin[1]));

      stockDataString.add(formatter.format(new Date(Long.valueOf(dataTermin[0]))));

    }
}
*/


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                Contract.Quote.URI,
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                null, null, Contract.Quote.COLUMN_SYMBOL);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        data.moveToPosition(position);
        symbol = data.getString(data.getColumnIndex(Contract.Quote.COLUMN_SYMBOL));
        String price = data.getString(data.getColumnIndex(Contract.Quote.COLUMN_PRICE));
        String absChnage = data.getString(data.getColumnIndex(Contract.Quote.COLUMN_ABSOLUTE_CHANGE));
        String hist = data.getString(data.getColumnIndex(Contract.Quote.COLUMN_HISTORY));
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String[] test = hist.split("\n");

        stockPrice.clear();
        stockDataString.clear();
        stockData.clear();
        for (int i = 0; i < test.length; i++) {

            String[] dataTermin=test[i].split(", ");
            stockData.add(Long.valueOf(dataTermin[0]));
            stockPrice.add(Double.valueOf(dataTermin[1]));

            stockDataString.add(formatter.format(new Date(Long.valueOf(dataTermin[0]))));



        }
     ///   Log.d("symbol", String.valueOf(stockDataString));

        stockLabel.setText(symbol);
        priceLabel.setText(getString(R.string.dolar)+roundPrice(String.valueOf(stockPrice.get(0))) +"");
        dateLabel.setText(stockDataString.get(0));

        com.github.mikephil.charting.charts.LineChart lineChart = (com.github.mikephil.charting.charts.LineChart) findViewById(R.id.chart);
        entries = new ArrayList<>();



        for( int i =0;i<stockPrice.size();i++){
            double wyn = stockPrice.get(i);
            entries.add(new Entry((float) i, (float)wyn));
        }


        LineDataSet dataset = new LineDataSet(entries, symbol);
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();

        for (ILineDataSet set : dataSets) {
            set.setDrawFilled(true);
        }
        dataSets.add(dataset);


        LineData datas = new LineData(dataSets);
        dataset.setColors(ColorTemplate.MATERIAL_COLORS); //

        dataset.setColor(Color.RED);
        dataset.setDrawFilled(true);


        lineChart.setData(datas);


     lineChartSetiings(lineChart);
        lineChart.setOnChartValueSelectedListener(this);


    }





    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {



       // Log.d(LOG_TAG, "onSelected Entry: " + value.closeValue);
        //Log.d(LOG_TAG, "onSelected Index: " + value.date);


        dateLabel.setText(stockDataString.get((int) e.getX())+"");
        priceLabel.setText("$"+roundPrice(String.valueOf(e.getY()))+"");

    }

    @Override
    public void onNothingSelected() {

    }
    public String roundPrice(String originalPrice){

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_EVEN);
        Double number = Double.parseDouble(originalPrice);

        return ""+df.format(number);
    }

void lineChartSetiings(LineChart lineChart){
    lineChart.animateY(2000);
    lineChart.setTouchEnabled(true);
    //lineChart.setDrawValues(false);
    lineChart.setDragEnabled(false);
    lineChart.setScaleEnabled(false);
    lineChart.setPinchZoom(false);
    lineChart.setEnabled(false);


    //mChart.setBackgroundColor(Color.BLACK);

    Legend l = lineChart.getLegend();
    l.setForm(Legend.LegendForm.CIRCLE);
    l.setTextSize(12f);
    l.setTextColor(Color.WHITE);
    l.setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);
    l.setEnabled(false);

    XAxis xAxis = lineChart.getXAxis();
    xAxis.setEnabled(false);
    xAxis.setTextSize(12f);
    xAxis.setTextColor(Color.WHITE);
    xAxis.setDrawGridLines(false);
    xAxis.setDrawAxisLine(false);
    // xAxis.setSpaceBetweenLabels(1);
    xAxis.setEnabled(false);



    YAxis leftAxis = lineChart.getAxisLeft();
//        leftAxis.setTypeface(tf);
    leftAxis.setTextColor(Color.WHITE);
    //  leftAxis.setAxisMaxValue((float) upperLimit);
    leftAxis.setDrawGridLines(true);

    //setting up the upper line


    YAxis rightAxis = lineChart.getAxisRight();

    rightAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
    rightAxis.setDrawLimitLinesBehindData(true);
    rightAxis.setTextColor(Color.WHITE);
    // rightAxis.setAxisMaxValue((float) upperLimit);
    rightAxis.enableGridDashedLine(10f, 10f, 0f);
//  lineChart.animateXY(1000, 1000);
    lineChart.invalidate();


}

}
