package net.kevin.com.healthmanager.chart;


import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.formatter.ValueFormatter;


/**
 * 周格式器
 */
public class DayAxisValueFormatter extends ValueFormatter {

    private final String[] mWeek = new String[]{
            "周日", "周一", "周二", "周三", "周四", "周五", "周六"
    };

    private final BarLineChartBase<?> chart;

    public DayAxisValueFormatter(BarLineChartBase<?> chart) {
        this.chart = chart;
    }

    @Override
    public String getFormattedValue(float value) {
        return mWeek[((int) value)%7];
    }

}
