package com.transistorsoft.cordova.backgroundfetch;
import android.content.Context;
import com.transistorsoft.tsbackgroundfetch.BackgroundFetch;
import android.util.Log;
import android.widget.Toast;
import android.content.SharedPreferences;
import static android.content.Context.MODE_PRIVATE;



import cordova.plugin.appcheck.AppCheck;


public class BackgroundFetchHeadlessTask implements HeadlessTask {
    @Override
    public void onFetch(Context context, String taskId) {
        // Perform your work here.

        long frequencyInMilli = AppCheck.getFrequency(context) * 60000;
        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime - getLastExecutedTime(context);

        Log.d(BackgroundFetch.TAG, "My BackgroundFetchHeadlessTask:  frequencyInMilli: " + frequencyInMilli);
        Log.d(BackgroundFetch.TAG, "My BackgroundFetchHeadlessTask:  timeDiff: " + timeDiff);

        if( timeDiff > frequencyInMilli) {
            Log.d(BackgroundFetch.TAG, "My BackgroundFetchHeadlessTask:  Executed: " + AppCheck.getUserId(context));
            AppCheck.sendInstalledAppsToServer(AppCheck.getUserId(context), context);
            AppCheck.sendUserDataToServer(AppCheck.getUserId(context), context);
            setLastExecutedTime(System.currentTimeMillis(), context);
        } else {
            Log.d(BackgroundFetch.TAG, "My BackgroundFetchHeadlessTask:  Not Executed: " + AppCheck.getUserId(context));
        }

        // Just as in Javascript callback, you must signal #finish
        BackgroundFetch.getInstance(context).finish(taskId);
    }

    private static void setLastExecutedTime(long time, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("lastExecutedTime", time);
        editor.apply();
    }

    private static long getLastExecutedTime(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        long user_id = sharedPreferences.getLong("lastExecutedTime", -1);
        return user_id;
    }
}