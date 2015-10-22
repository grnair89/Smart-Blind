package test.rit.harsh.myapplication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import java.io.IOException;
import java.net.ServerSocket;


/**
 * Created by Harsh Patil on 10/7/2015.
 */
public class BGNotiService extends Service {
    private int current_temp = 0;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new serverStarter(this)).start();
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();

        return START_STICKY;
    }

    /*
    * Displays notification in the notification bar
    *
    */
    private void displayNotification(String s) {
        String[] display = s.split(",");
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Smart Blind Update")
                        .setContentText("Temp:" + display[0] + "F" + " Ambient:" + display[1]);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onUpdate(String s) {
        Intent i = new Intent("test.rit.harsh.myapplication.BGNotiService.RECEIVE_JSON");
        i.putExtra("value", s);
        String[] change = s.split(",");
        int previous_temp = current_temp;
        System.out.println(previous_temp);
        current_temp = Integer.parseInt(change[0].replace("\"", ""));

        if (current_temp < ((previous_temp) - 2) || (current_temp > (previous_temp + 2))) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
            displayNotification(s);
        }

    }

    /*
    *   Class which creates a new server instance in the background service
    *
     */
    public class serverStarter implements Runnable {
        BGNotiService obj;

        public serverStarter(BGNotiService obj) {
            this.obj = obj;
        }

        private static final int PORT = 2344;

        @Override
        public void run() {
            try {
                ServerSocket listener = new ServerSocket(PORT);
                System.out.println("Server is listening on " + listener.getLocalPort());

                while (true) {
                    new JsonRPCServer.Handler(listener.accept(), obj).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
