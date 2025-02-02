package com.example.whatsappcall;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;


public class WhatsAppNotiListener extends NotificationListenerService {

    private static final String WHATSAPP_PACKAGE = "com.whatsapp";
    private static final String TAG = "@vi";
    public static int WHATSAPP_CURRENT_STATE =-1;
    enum WHATSAPP_STATE {
        CALLING, RINGING, ONGOING_VOICE_CALL, ONGOING_VIDEO_CALL, INCOMING_VOICE_CALL, INCOMING_VIDEO_CALL
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        String notificationText = sbn.getNotification().extras.getString("android.text");
        String notificationTitle = sbn.getNotification().extras.getString("android.title");
        //LogHandler.i(TAG, packageName+ notificationTitle+ notificationText);   //For All notification

        if (sbn.getPackageName().equals(WHATSAPP_PACKAGE)) {
            if (notificationText != null) {
                LogHandler.i(TAG, packageName+"-"+notificationTitle+"-"+notificationText);
                handleWhatsAppNotification(notificationText);
            }
        }
    }

    private void handleWhatsAppNotification(String notificationText) {
        //showToast(notificationText) ;
        if(notificationText.contains("Calling")){
            if (WHATSAPP_CURRENT_STATE != WHATSAPP_STATE.CALLING.ordinal()){
                WHATSAPP_CURRENT_STATE = WHATSAPP_STATE.CALLING.ordinal();
                showToast("WhatsAppNoti: Outgoing Call Started") ;
            }
        }
        if(notificationText.contains("Ringing")) {
            if (WHATSAPP_CURRENT_STATE != WHATSAPP_STATE.RINGING.ordinal()) {
                WHATSAPP_CURRENT_STATE = WHATSAPP_STATE.RINGING.ordinal();
                showToast("WhatsAppNoti: Outgoing Call Ringing");
            }
        }
        if (notificationText.contains("Ongoing voice call")) {
            if (WHATSAPP_CURRENT_STATE == WHATSAPP_STATE.RINGING.ordinal() || WHATSAPP_CURRENT_STATE == WHATSAPP_STATE.CALLING.ordinal()) {
                WHATSAPP_CURRENT_STATE = WHATSAPP_STATE.ONGOING_VOICE_CALL.ordinal();
                showToast("WhatsAppNoti: Outgoing voice call");
            }
        }
        if (notificationText.contains("Ongoing video call")) {
            if (WHATSAPP_CURRENT_STATE == WHATSAPP_STATE.RINGING.ordinal() || WHATSAPP_CURRENT_STATE == WHATSAPP_STATE.CALLING.ordinal()) {
                WHATSAPP_CURRENT_STATE = WHATSAPP_STATE.ONGOING_VIDEO_CALL.ordinal();
                showToast("WhatsAppNoti: Outgoing video call");
            }
        }
        if (notificationText.contains("Incoming voice call")){
            if (WHATSAPP_CURRENT_STATE != WHATSAPP_STATE.INCOMING_VOICE_CALL.ordinal()) {
                WHATSAPP_CURRENT_STATE = WHATSAPP_STATE.INCOMING_VOICE_CALL.ordinal();
                showToast("WhatsAppNoti: Incoming voice call");
            }
        }
        if (notificationText.contains("Incoming video call"))
            if (WHATSAPP_CURRENT_STATE != WHATSAPP_STATE.INCOMING_VIDEO_CALL.ordinal()) {
                WHATSAPP_CURRENT_STATE = WHATSAPP_STATE.INCOMING_VIDEO_CALL.ordinal();
                showToast("WhatsAppNoti: Incoming video call");
            }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // Handle notification removal if needed
    }

    // Helper method to show a Toast on the main thread
    private void showToast(final String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}