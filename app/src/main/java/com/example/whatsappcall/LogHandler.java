package com.example.whatsappcall;

import android.util.Log;
import android.widget.TextView;
import java.io.PrintWriter;
import java.io.StringWriter;

public class LogHandler {

    private static TextView logTextView;

    public static void initialize(TextView textView) {
        logTextView = textView;
    }

    public static void d(String tag, String message) {
        Log.d(tag, message);
        appendLog("DEBUG: " + tag + " - " + message);
    }

    public static void i(String tag, String message) {
        Log.i(tag, message);
        appendLog("INFO: " + tag + " - " + message);
    }

    public static void e(String tag, String message, Throwable throwable) {
        Log.e(tag, message, throwable);
        String stackTrace = getStackTraceString(throwable);
        appendLog("ERROR: " + tag + " - " + message + "\n" + stackTrace);
    }

    private static void appendLog(final String logMessage) {
        if (logTextView != null) {
            // Update the TextView on the main thread
            logTextView.post(() -> {
                logTextView.append(logMessage + "\n");

                // Scroll to the bottom of the TextView
                final int scrollAmount = logTextView.getLayout().getLineTop(logTextView.getLineCount()) - logTextView.getHeight();
                if (scrollAmount > 0) {
                    logTextView.scrollTo(0, scrollAmount);
                } else {
                    logTextView.scrollTo(0, 0);
                }
            });
        }
    }

    private static String getStackTraceString(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}
