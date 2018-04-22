package it.ipramodsinghrawat.aurids;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService{
    private static final String TAG = "MyFMS";
    Context context;
    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived: ");

        //Toast.makeText(getApplicationContext(),"From: " + remoteMessage.getFrom(), Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(),"Notification Message Body: " + remoteMessage.getNotification().getBody(), Toast.LENGTH_LONG).show();

        //NotificationDataModal notificationDataModal = null;

        if (remoteMessage == null)
            return;

        //Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            handleNotification(remoteMessage.getNotification().getBody());
        }

        //Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());

                handleDataMessage(json);

            } catch (Exception e) {
                //Log.e(TAG, e.getMessage());
            }
        }

        //below one note using right Now
        //String notificationType = remoteMessage.getData().get("notificationType");
        //String tittle = remoteMessage.getData().get("tittle");
        //String message = remoteMessage.getData().get("message");
    }

    private void handleNotification(String message) {

        Log.d(TAG, "handleNotification Function call message: "+message);

        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {

            Log.d(TAG, "NotificationUtils.isAppIsInBackground");

            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        }else{
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(JSONObject json) {
        //Log.d(TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("data");

            String messageType = data.getString("message_type");
            String title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");

            /*
            if(data.getJSONObject("payload") != null){
                JSONObject payload = data.getJSONObject("payload");
                Log.e(TAG, "payload: " + payload.toString());
            }
            */

            //Log.e(TAG, "message_type: " + messageType);
            //Log.e(TAG, "title: " + title);
            //Log.e(TAG, "message: " + message);
            //Log.e(TAG, "isBackground: " + isBackground);
            //Log.e(TAG, "imageUrl: " + imageUrl);
            //Log.e(TAG, "timestamp: " + timestamp);

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                //Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                //pushNotification.putExtra("message", message);
                //LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                Intent resultIntent = null;
                if(messageType.equals("loc_notification_for_family")){
                    //resultIntent = new Intent(getApplicationContext(), FamilyOnMapActivity.class);
                }else{
                    resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                }
                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
                notificationUtils.showNotificationMessage(title, message, timestamp, resultIntent);

                //showNotificationMessage(getApplicationContext(), title, message, timestamp, null);

                //Log.e(TAG, "if not NotificationUtils.isAppIsInBackground");
            } else {

                // app is in background, show the notification in notification tray
                Intent resultIntent = null;
                if(messageType.equals("loc_notification_for_family")){
                    //resultIntent = new Intent(getApplicationContext(), FamilyOnMapActivity.class);
                }else{
                    resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                }

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    //Log.e(TAG, "showNotificationMessage");
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    //Log.e(TAG, "showNotificationMessage with image ");
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }

                //Log.e(TAG, "else NotificationUtils.isAppIsInBackground");
            }
        } catch (JSONException e) {
            //Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            //Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }

    /*
    private void sendCurrentLocationNotification(String notificationType, String title, String message) {
        Intent intent = null;
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent = new Intent(this,FamilyOnMapActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                                    .setSmallIcon(R.drawable.heareim)
                                    .setContentTitle(title)
                                    .setContentText(message)
                                    .setAutoCancel(true)
                                    .setSound(defaultSoundUri)
                                    .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notificationBuilder.build());
    }
    */
}
