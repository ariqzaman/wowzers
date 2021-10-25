package com.example.taskdhd.presenter;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.HighLowDataEntry;
import com.example.taskdhd.R;
import com.example.taskdhd.model.Company;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class StockIndexAdapter
        extends RecyclerView.Adapter<StockIndexAdapter.stockIndexViewHolder> {

    private ArrayList<Company> stockIndexesArrayList;


    public static class stockIndexViewHolder extends RecyclerView.ViewHolder{

        //public AnyChartView stockIndexChart;
        public CandleStickChart candleStickChart;
        public TextView indexOpen;
        public TextView indexHigh;
        public TextView indexLow;
        public TextView indexClose;
        public TextView indexVolume;

        public stockIndexViewHolder(View view){
            super(view);
            //stockIndexChart = view.findViewById(R.id.stockIndexChart);
            candleStickChart = view.findViewById(R.id.mpstockIndexChart);
            indexOpen = view.findViewById(R.id.stockIndexOpenValue);
            indexHigh = view.findViewById(R.id.stockIndexHighValue);
            indexLow = view.findViewById(R.id.stockIndexLowValue);
            indexClose = view.findViewById(R.id.stockIndexCloseValue);
            indexVolume = view.findViewById(R.id.stockIndexVolumeValue);
        }

    }


    public StockIndexAdapter(ArrayList<Company> stockIndexesArrayList){
        this.stockIndexesArrayList = stockIndexesArrayList;
    }

    @NonNull
    @Override
    public stockIndexViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.mp_chart_layout, viewGroup, false);

        return new stockIndexViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull stockIndexViewHolder stockIndexViewHolder, int i) {
        String[] arrayIndexName = {"S&P/ASX 200 Index","S&P/ASX 50 Index"};
        Company index = stockIndexesArrayList.get(i);
        //LoadChartClass loadChartClass = new LoadChartClass();
        //stockIndexViewHolder.stockIndexChart.setChart(loadChartClass.loadAnyChart(index, arrayIndexName[i]));

        stockIndexViewHolder.candleStickChart.setHighlightPerDragEnabled(true);
        stockIndexViewHolder.candleStickChart.setDrawBorders(true);
        stockIndexViewHolder.candleStickChart.setBorderColor(Color.LTGRAY);

        YAxis yAxis = stockIndexViewHolder.candleStickChart.getAxisLeft();
        YAxis rightAxis = stockIndexViewHolder.candleStickChart.getAxisRight();
        yAxis.setDrawGridLines(true);
        rightAxis.setDrawGridLines(true);
        stockIndexViewHolder.candleStickChart.requestDisallowInterceptTouchEvent(true);

        XAxis xAxis = stockIndexViewHolder.candleStickChart.getXAxis();

        xAxis.setDrawGridLines(true);// disable x axis grid lines
        xAxis.setDrawLabels(true);
        rightAxis.setTextColor(Color.WHITE);
        yAxis.setDrawLabels(true);

        Legend l = stockIndexViewHolder.candleStickChart.getLegend();
        l.setEnabled(true);

        ArrayList<CandleEntry> candleValues = new ArrayList<>();

        String[] dateIndex = new String[index.getCompanyStockPrices().size()];
        try {
            for (int j = 0; j < index.getCompanyStockPrices().size(); j++) {
                //System.out.println((float)index.getCompanyStockPrices().get(j).getDailyHigh());
                if(index.getCompanyStockPrices().get(j).getDailyClose() != 0){
                    dateIndex[j] = String.valueOf(index.getCompanyStockPrices().get(j).getDailyDate());
                    candleValues.add(new CandleEntry(
                            (float)j * 1f,
                            (float)index.getCompanyStockPrices().get(index.getCompanyStockPrices().size()-1-j).getDailyHigh() * 1f,
                            (float)index.getCompanyStockPrices().get(index.getCompanyStockPrices().size()-1-j).getDailyLow() * 1f,
                            (float)index.getCompanyStockPrices().get(index.getCompanyStockPrices().size()-1-j).getDailyOpen() * 1f,
                            (float)index.getCompanyStockPrices().get(index.getCompanyStockPrices().size()-1-j).getDailyClose() * 1f));
                }
            }
        }catch (Exception ex){ex.printStackTrace();}

        List<String> list = Arrays.asList(dateIndex);
        Collections.reverse(list);
        String[] reversedArray = list.toArray(dateIndex);

        IndexAxisValueFormatter indexAxisValueFormatter = new IndexAxisValueFormatter(reversedArray);
        xAxis.setValueFormatter(indexAxisValueFormatter);
        xAxis.setLabelCount(4);

        //System.out.println(candleValues.toString());
        CandleDataSet set1 = new CandleDataSet(candleValues, "Stock Prices");
        set1.setColor(Color.rgb(80, 80, 80));
        set1.setShadowColor(Color.GRAY);
        set1.setShadowWidth(0.8f);
        set1.setDecreasingColor(Color.RED);
        set1.setDecreasingPaintStyle(Paint.Style.FILL);
        set1.setIncreasingColor(Color.GREEN);
        set1.setIncreasingPaintStyle(Paint.Style.FILL);
        set1.setNeutralColor(Color.LTGRAY);
        set1.setDrawValues(true);

        Description description = new Description();
        description.setText(arrayIndexName[i]);

        CandleData data = new CandleData(set1);
        stockIndexViewHolder.candleStickChart.setDescription(description);
        stockIndexViewHolder.candleStickChart.setData(data);
        stockIndexViewHolder.candleStickChart.notifyDataSetChanged();
        stockIndexViewHolder.candleStickChart.invalidate();

        stockIndexViewHolder.indexOpen.setText(String.valueOf(index.getCompanyStockPrices().get(0).getDailyOpen()));
        stockIndexViewHolder.indexHigh.setText(String.valueOf(index.getCompanyStockPrices().get(0).getDailyHigh()));
        stockIndexViewHolder.indexLow.setText(String.valueOf(index.getCompanyStockPrices().get(0).getDailyLow()));
        stockIndexViewHolder.indexClose.setText(String.valueOf(index.getCompanyStockPrices().get(0).getDailyClose()));
        stockIndexViewHolder.indexVolume.setText(String.valueOf(index.getCompanyStockPrices().get(0).getDailyVolume()));
    }

    @Override
    public int getItemCount() {
        return stockIndexesArrayList.size();
    }
}
