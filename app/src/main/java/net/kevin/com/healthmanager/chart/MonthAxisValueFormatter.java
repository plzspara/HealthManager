package net.kevin.com.healthmanager.chart;


import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.formatter.ValueFormatter;



/**
 * 月格式器
 */
public class MonthAxisValueFormatter extends ValueFormatter {

    private final BarLineChartBase<?> chart;

    public MonthAxisValueFormatter(BarLineChartBase<?> chart) {
        this.chart = chart;
    }

    @Override
    public String getFormattedValue(float value) {
        return "" + (int) value;
    }


}
