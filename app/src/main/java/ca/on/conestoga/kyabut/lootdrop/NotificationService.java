package ca.on.conestoga.kyabut.lootdrop;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {

    private static final String TAG = "NotificationService";

    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate(){
        final Timer timer = new Timer(true);

        final NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //Connects to the internet
        final ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (networkInfo !=null &&networkInfo.isConnected())
        {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //Try catch statement
                    try{
                        URL url = new URL("https://quotes.rest/qod?language=en");
                        InputStream in = url.openStream();

                        //Gets entire result
                        Scanner s = new Scanner(in).useDelimiter("\\A");
                        String result = s.hasNext()
                                ? s.next()
                                : "";

                        final Notification notification = new Notification.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.ic_diamond)
                                .setContentTitle(getString(R.string.app_name))
                                .setContentText(new JSONObject(result)
                                        .getJSONObject("contents")
                                        .getJSONArray("quotes")
                                        .getJSONObject(0)
                                        .getString("quote"))
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent)
                                .build();

                        manager.notify(1, notification);
                    }
                    catch (MalformedURLException ex)
                    {

                    }
                    catch (IOException ex)
                    {

                    }
                    catch (JSONException ex)
                    {

                    }
                    finally{
                        timer.cancel();
                        stopSelf();
                    }
                }
            }, 5000);

            //super.onCreate();
        }
        else
        {
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
