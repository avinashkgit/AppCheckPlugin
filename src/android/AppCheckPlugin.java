package cordova.plugin.appcheck;

import android.widget.Toast;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class AppCheckPlugin extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        switch (action) {
            case "sendInstalledAppsToServer": {
                String userId = args.getString(0);
                this.sendInstalledAppsToServer(userId, callbackContext);
                return true;
            }
            case "sendUserDataToServer": {
                String userId = args.getString(0);
                this.sendUserDataToServer(userId, callbackContext);
                break;
            }
            case "getPermissions": {
                this.getPermissions(callbackContext);
                break;
            }
            case "setUserId": {
                String userId = args.getString(0);
                this.setUserId(userId, callbackContext);
                break;
            }
            case "setFrequency": {
                String frequency = args.getString(0);
                this.setFrequency(frequency, callbackContext);
                break;
            }
        }
        return false;
    }

    private void sendInstalledAppsToServer(String userId, CallbackContext callbackContext) {
        if (userId != null && userId.length() > 0) {
            cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    AppCheck.sendInstalledAppsToServer(userId, cordova.getContext());
                }
            });
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void sendUserDataToServer(String userId, CallbackContext callbackContext) {
        if (userId != null && userId.length() > 0) {
            cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    AppCheck.sendUserDataToServer(userId, cordova.getContext());
                }
            });
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void getPermissions(CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                AppCheck.getPermissions(cordova.getContext());
            }
        });
    }

    private void setUserId(String userId, CallbackContext callbackContext) {
        if (userId != null && userId.length() > 0) {
            cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    AppCheck.setUserId(cordova.getContext(), userId);
                }
            });
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void setFrequency(String userId, CallbackContext callbackContext) {
        if (userId != null && userId.length() > 0) {
            cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    AppCheck.setFrequency(cordova.getContext(), userId);
                }
            });
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
}
