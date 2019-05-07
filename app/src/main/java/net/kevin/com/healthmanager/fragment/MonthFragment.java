package net.kevin.com.healthmanager.fragment;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.model.GradientColor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.kevin.com.healthmanager.R;
import net.kevin.com.healthmanager.chart.MonthAxisValueFormatter;
import net.kevin.com.healthmanager.chart.MyValueFormatter;
import net.kevin.com.healthmanager.chart.XYMarkerView;
import net.kevin.com.healthmanager.javaBean.User;

import cn.bmob.v3.BmobUser;

/**
 * 月运动历史fragment
 */
public class MonthFragment extends Fragment {


    private BarChart chart;

    public MonthFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_month, container, false);
        chart = view.findViewById(R.id.chart2);
        //chart.setOnChartValueSelectedListener(this);

        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);

        chart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawGridBackground(false);
        // chart.setDrawYLabels(false);

        ValueFormatter xAxisFormatter = new MonthAxisValueFormatter(chart);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(Typeface.DEFAULT_BOLD);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);

        ValueFormatter custom = new MyValueFormatter("步");

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTypeface(Typeface.DEFAULT_BOLD);
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(Typeface.DEFAULT_BOLD);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        XYMarkerView mv = new XYMarkerView(getActivity(), xAxisFormatter);
        mv.setChartView(chart); // For bounds control
        chart.setMarker(mv); // Set the marker to the chart

        // setting data
        setData();
        return view;
    }

    private void setData() {
        int count = 0;
        User user = BmobUser.getCurrentUser(User.class);
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentTime = sdf.format(date);
        String month = currentTime.substring(5,7);
        String day = currentTime.substring(8,10);

        if (month.substring(0,1).equals("0")) {
            month = month.substring(1,2);
        }



        if (day.substring(0,1).equals("0")) {
            count = Integer.parseInt(day.substring(1,2));
        } else {
            count = Integer.parseInt(day);
        }

        float start = 1f;

        ArrayList<BarEntry> values = new ArrayList<>();

        for (int i = (int) start; i < start + count; i++) {


            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - count + i);// 让日期减去5
            Date endDate = cal.getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String d = simpleDateFormat.format(endDate);
            Log.d("data", "setData: "+d);
            int k = user.getStepDate().size();
            for (int j = 0; j < count;j++) {
                if ( k-1-j>=0 && user.getStepDate().get(k-1-j).equals(d)) {
                    float y = Float.parseFloat(user.getStep().get(k-1-j));
                    values.add(new BarEntry(i,y));
                    break;
                }
                if (j == count-1) {
                    values.add(new BarEntry(i,0));
                }
            }
        }
        BarDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();

        } else {
            set1 = new BarDataSet(values, month+"月");
            set1.setDrawIcons(false);

//            set1.setColors(ColorTemplate.MATERIAL_COLORS);

            /*int startColor = ContextCompat.getColor(this, android.R.color.holo_blue_dark);
            int endColor = ContextCompat.getColor(this, android.R.color.holo_blue_bright);
            set1.setGradientColor(startColor, endColor);*/
            int startColor1 = ContextCompat.getColor(getActivity(), android.R.color.holo_orange_light);
            int startColor2 = ContextCompat.getColor(getActivity(), android.R.color.holo_blue_light);
            int startColor3 = ContextCompat.getColor(getActivity(), android.R.color.holo_orange_light);
            int startColor4 = ContextCompat.getColor(getActivity(), android.R.color.holo_green_light);
            int startColor5 = ContextCompat.getColor(getActivity(), android.R.color.holo_red_light);
            int endColor1 = ContextCompat.getColor(getActivity(), android.R.color.holo_blue_dark);
            int endColor2 = ContextCompat.getColor(getActivity(), android.R.color.holo_purple);
            int endColor3 = ContextCompat.getColor(getActivity(), android.R.color.holo_green_dark);
            int endColor4 = ContextCompat.getColor(getActivity(), android.R.color.holo_red_dark);
            int endColor5 = ContextCompat.getColor(getActivity(), android.R.color.holo_orange_dark);

            List<GradientColor> gradientColors = new ArrayList<>();
            gradientColors.add(new GradientColor(startColor1, endColor1));
            gradientColors.add(new GradientColor(startColor2, endColor2));
            gradientColors.add(new GradientColor(startColor3, endColor3));
            gradientColors.add(new GradientColor(startColor4, endColor4));
            gradientColors.add(new GradientColor(startColor5, endColor5));

            set1.setGradientColors(gradientColors);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setValueTypeface(Typeface.DEFAULT_BOLD);
            data.setBarWidth(0.9f);

            chart.setData(data);
        }
    }

}
