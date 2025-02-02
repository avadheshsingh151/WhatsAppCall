package com.example.whatsappcall;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class CallReceiver extends BroadcastReceiver {
    private static final String TAG = "@vi";
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if (state != null) {
            LogHandler.i(TAG, "TelephonyManager-"+state);
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                Toast.makeText(context, "WhatsApp@vi: Incoming call Ringing", Toast.LENGTH_SHORT).show();
                LogHandler.i(TAG, "TelephonyManager-Incoming call Ringing");
            }
            if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                //Toast.makeText(context, "Call is in Progress", Toast.LENGTH_SHORT).show();
                LogHandler.i(TAG, "TelephonyManager-Call is in Progress");
                String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                if (incomingNumber!=null)
                    LogHandler.d(TAG, "TelephonyManager-Incoming call from: " + incomingNumber);
            }
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                Toast.makeText(context, "Call ended.", Toast.LENGTH_SHORT).show();
                LogHandler.i(TAG, "TelephonyManager-Call ended.");
                WhatsAppNotiListener.WHATSAPP_CURRENT_STATE =-1;
            }
        }
    }
}
