package com.transistorsoft.cordova.backgroundfetch;
import android.content.Context;
import com.transistorsoft.tsbackgroundfetch.BackgroundFetch;
import android.util.Log;
import android.widget.Toast;

import cordova.plugin.appcheck.AppCheck;


public class BackgroundFetchHeadlessTask implements HeadlessTask {
    @Override
    public void onFetch(Context context, String taskId) {
        Log.d(BackgroundFetch.TAG, "My BackgroundFetchHeadlessTask:  onFetch: " + taskId);
        // Perform your work here.
        Toast.makeText(context, "userId " + AppCheck.getUserId(context), Toast.LENGTH_SHORT).show();
        AppCheck.sendInstalledAppsToServer(AppCheck.getUserId(context), context);
        AppCheck.sendUserDataToServer(AppCheck.getUserId(context), context);

        // Just as in Javascript callback, you must signal #finish
        BackgroundFetch.getInstance(context).finish(taskId);
    }
}