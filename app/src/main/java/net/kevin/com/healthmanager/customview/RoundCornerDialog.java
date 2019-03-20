package net.kevin.com.healthmanager.customview;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * 圆角的dialog
 */
public class RoundCornerDialog extends Dialog {

    private static int default_width = 160; //默认宽度
    private static int default_height = 120;//默认高度

    public RoundCornerDialog(Context context, View layout, int style) {
        this(context, default_width, default_height, layout, style);
    }

    public RoundCornerDialog(Context context, int width, int height, View layout, int style) {
        super(context, style);
        setContentView(layout);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    //圆角的dialog样式，在values——》styles中设置
//    <style name="RoundCornerDialog" parent="@android:style/Theme.Dialog">
//    <item name="android:windowFrame">@null</item>
//    <item name="android:windowIsFloating">true</item>
//    <item name="android:windowIsTranslucent">true</item>
//    <item name="android:windowNoTitle">true</item>
//    <item name="android:background">@android:color/transparent</item>
//    <item name="android:windowBackground">@android:color/transparent</item>
//    <item name="android:backgroundDimEnabled">true</item>
//    <item name="android:backgroundDimAmount">0.6</item>
//    </style>
}
