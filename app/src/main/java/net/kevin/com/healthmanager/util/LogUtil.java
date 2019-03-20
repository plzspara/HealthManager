package net.kevin.com.healthmanager.util;

import android.util.Log;

/**
 * 管理Log的工具类：可通过设置mCurrentLevel，控制Log输出级别。
 * 项目上线时应将mCurrentLevel设置为LEVEL_NONE，禁止Log输出。
 */
public class LogUtil {
    //日志输出级别NONE
    public static final int LEVEL_NONE = 0;
    //日志输出级别E
    public static final int LEVEL_ERROR = 1;
    //日志输出级别W
    public static final int LEVEL_WARN = 2;
    //日志输出级别I
    public static final int LEVEL_INFO = 3;
    //日志输出级别D
    public static final int LEVEL_DEBUG = 4;
    //日志输出级别V
    public static final int LEVEL_VERBOSE = 5;

    //日志输出时的Tag
    private static String mTag = "LogUtil";
    //当前日志输出级别（是否允许输出log）
    private static int mCurrentLevel = LEVEL_VERBOSE;

    /**
     * 以级别为 d 的形式输出LOG
     */
    public static void v(String tag, String msg) {
        if (mCurrentLevel >= LEVEL_VERBOSE) {
            Log.v(tag, msg);
        }
    }

    /**
     * 以级别为 d 的形式输出LOG
     */
    public static void d(String tag, String msg) {
        if (mCurrentLevel >= LEVEL_DEBUG) {
            Log.d(tag, msg);
        }
    }

    /**
     * 以级别为 i 的形式输出LOG
     */
    public static void i(String tag, String msg) {
        if (mCurrentLevel >= LEVEL_INFO) {
            Log.i(tag, msg);
        }
    }

    /**
     * 以级别为 w 的形式输出LOG
     */
    public static void w(String tag, String msg) {
        if (mCurrentLevel >= LEVEL_WARN) {
            Log.w(tag, msg);
        }
    }

    /**
     * 以级别为 w 的形式输出Throwable
     */
    public static void w(Throwable tr) {
        if (mCurrentLevel >= LEVEL_WARN) {
            Log.w(mTag, "", tr);
        }
    }

    /**
     * 以级别为 w 的形式输出LOG信息和Throwable
     */
    public static void w(String msg, Throwable tr) {
        if (mCurrentLevel >= LEVEL_WARN && null != msg) {
            Log.w(mTag, msg, tr);
        }
    }

    /**
     * 以级别为 e 的形式输出LOG
     */
    public static void e(String tag, String msg) {
        if (mCurrentLevel >= LEVEL_ERROR) {
            Log.e(tag, msg);
        }
    }

    /**
     * 以级别为 e 的形式输出Throwable
     */
    public static void e(Throwable tr) {
        if (mCurrentLevel >= LEVEL_ERROR) {
            Log.e(mTag, "", tr);
        }
    }

    /**
     * 以级别为 e 的形式输出LOG信息和Throwable
     */
    public static void e(String msg, Throwable tr) {
        if (mCurrentLevel >= LEVEL_ERROR && null != msg) {
            Log.e(mTag, msg, tr);
        }
    }
}
