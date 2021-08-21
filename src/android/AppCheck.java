package cordova.plugin.appcheck;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.ReferenceQueue;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.aceventura.appcheck.AppDatabase.getAppDatabase;

/**
 * Created by "Manoj Waghmare" on 31,Aug,2020
 **/


public class AppCheck {

    static String mAppCode = "";
    static String  installed_app_frequency = "";


    private static final String TAG = "UsageStatsActivity";
    public static AppsDetailsDatabaseHelper mAppsDetailsDatabaseHelper;
    public static AppsCodesDatabaseHelper mAppsCodesDatabaseHelper;
    public static AppDatabase ad;
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String KEY = "user_id";
    public static String value = "";


    static String frequencyTime = "0";
    public static int counter = 0;


    /*This function is used to send all the apps list present in the user's device */
    public static void sendInstalledAppsToServer(final String user_id, Context context) {
        UsageStatsManager mUsageStatsManager;
        PackageManager mPm;
        final ArrayList<String> installedAppsList = new ArrayList<>();
        final ArrayList<String> installedAppsList1 = new ArrayList<>();

        mPm = context.getPackageManager();
        mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        //permission code(23sept2020)
        /*List<UsageStats> statsNew = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0,
                System.currentTimeMillis());
        boolean isEmpty = statsNew.isEmpty();
        if (isEmpty) {
            Intent i = new Intent();
            i.setAction(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            i.addFlags(FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }*/

        final ArrayMap<String, String> mAppLabelMap = new ArrayMap<>();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -5); //-5 means 4 days before & current day(old => -5)

        final List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST,
                cal.getTimeInMillis(), System.currentTimeMillis());

        if (stats == null) {
            return;
        }

        ArrayMap<String, UsageStats> map = new ArrayMap<>();
        final int statCount = stats.size();

        for (int i = 0; i < statCount; i++) {
            final UsageStats pkgStats = stats.get(i);
            try {
                PackageInfo p = mPm.getPackageInfo(pkgStats.getPackageName(), 0);

                if ((!isSystemPackage(mPm.getPackageInfo(pkgStats.getPackageName(), 0)))) {

                    //pckg name
                    String packageName = p.packageName;

                    String appName = p.applicationInfo.loadLabel(context.getPackageManager()).toString();
                    Log.e("APPNAME", "NAMe-=" + appName);


                    //this is the old code for app name // TODO: 22-09-2020
                    String installedApp1 = "\"" + appName + "\"";
                    installedAppsList1.add(installedApp1);
                    //This is the new code for adding the package name of the apps // TODO: 22-09-2020
                    String installedApp = "\"" + packageName + "\"";
                    installedAppsList.add(installedApp);

                    mAppLabelMap.put(pkgStats.getPackageName(), appName);

                    UsageStats existingStats = map.get(pkgStats.getPackageName());

                    if (existingStats == null) {
                        map.put(pkgStats.getPackageName(), pkgStats);
                    } else {
                        existingStats.add(pkgStats);
                    }

                }
            } catch (PackageManager.NameNotFoundException e) {
                // This package may be gone.
            }
        }

        Log.e("installed_app_of_user", "Param>>" + "http://digiqualweb.hansaresearch.com" +
                "/backend_app/index.php/AdminApi/add_installed_app_of_user");

        //String add_installed_app_of_user = "http://digiqualweb.hansaresearch.com" + "/backend_app/index.php/AdminApi/add_installed_app_of_user";
        String add_installed_app_of_user = "https://evolvu.in/backend_app/index.php/AdminApi/add_installed_app_of_user";
//http://digiqualweb.hansaresearch.com" + "/backend_app/index.php/AdminApi/add_installed_app_of_user"
        Log.e("AppCheke", "URL>>add_installed_app_of_user>" + add_installed_app_of_user);

        StringRequest stringRequestThree = new StringRequest(Request.Method.POST, "" + add_installed_app_of_user, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && !response.equals("") && !response.equals("null")) {
                    try {
                        Log.e("installed_app_of_user", "VALUE response>>" + response);
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        System.out.println(status);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i("get_app_usage:", "error1=> " + e.getMessage());
                    }
                } else {
                    Log.i("get_app_usage:", "error2=> " + response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.i("get_app_usage:", "error3=> " + error.getMessage());
            }
        }) {


            @Override
            protected Map<String, String> getParams() {

                AppDao dao = ad.appDao();

                List<AppData> dd = dao.getAllApps();
                for (int i = 0; i < dd.size(); i++) {
                    AppData model = dd.get(i);
                    value = "[" + dd.get(i).name + "]";

                }

                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                params.put("name", installedAppsList.size() == 0 ? "" : installedAppsList.toString());
                params.put("common_name", value);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                System.out.println(formatter.format(date));
                Log.e("installed_app_of_user", "DATE>>" + formatter.format(date));
                Log.e("installed_app_of_user", "VALUE APPNAME>>" + value);
                params.put("polling_date", "" + formatter.format(date).trim());//dd-mm-yyyy
                Log.e("installed_app_of_user", "Param>>" + "http://digiqualweb.hansaresearch.com" +
                        "/backend_app/index.php/AdminApi/add_installed_app_of_user");

                System.out.println(params);
                Log.e("installed_app_of_user", "Param>>" + params);
                Log.e("AppCheke", "URL>params>add_installed_app_of_user>" + params);

                return params;

            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(stringRequestThree);
    }

    /*This function is used to send the apps ,only if that app is present in the master database,
     * then only send the usage details of that app to server through API.   */
    public static void sendUserDataToServer(final String user_id, Context context) {
        Log.e("add_deatils_of_app", "Inmare0");
        UsageStatsManager mUsageStatsManager;
        PackageManager mPm;
        final AppsDetailsDatabaseHelper mAppsDetailsDatabaseHelper;
        int poll_date_difference = 0;
        AppDatabase ad;
        String poll_date, todaysDate;
        float daysBetweenTwoPoll;
        int differenceNew = 0;

        //Today's date
        Date d = Calendar.getInstance().getTime();
        SimpleDateFormat df1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        final String formattedDate = df1.format(d);

        System.out.println("SYSTEM_DATE" + formattedDate);
        /*
        //Saving date of poll
        SharedPreferences.Editor editor = context.getSharedPreferences("PREF", MODE_PRIVATE).edit();
        editor.putString("poll_date", formattedDate);
        editor.apply();


        //Today's Date
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        todaysDate = df.format(c);

        //Retrieving poll_date from sharedPref
        SharedPreferences prefs = context.getSharedPreferences("PREF", MODE_PRIVATE);
        poll_date = prefs.getString("poll_date", "NO DATE");

        if (poll_date != null) {
            if (poll_date.equals("NO DATE")) {
                differenceNew = 0;
                System.out.println("Number of Days between two Polls: " + differenceNew);
                return;
            } else {
                //Compare poll_date & today's date and find the difference
                try {
                    Date dateBefore = df.parse(poll_date);//Previous poll
                    Date dateAfter = df.parse(todaysDate);//latest poll date
                    long difference = 0;
                    if (dateAfter != null) {
                        if (dateBefore != null) {
                            difference = dateAfter.getTime() - dateBefore.getTime();
                        }
                    }
                    daysBetweenTwoPoll = difference / (1000 * 60 * 60 * 24);
                    differenceNew = Math.round(daysBetweenTwoPoll);
                    System.out.println("Number of Days between two Polls: " + differenceNew);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
*/

        mPm = context.getPackageManager();
        mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        ad = AppDatabase.getAppDatabase(context);
        mAppsDetailsDatabaseHelper = new AppsDetailsDatabaseHelper(context);

        //permission code removed(23sept2020)
        /*List<UsageStats> statsNew = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0,
                System.currentTimeMillis());
        boolean isEmpty = statsNew.isEmpty();

        if (isEmpty) {
            Intent i = new Intent();
            i.setAction(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            i.addFlags(FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }*/

        getApplicationNameCode(context);

        final ArrayMap<String, String> mAppLabelMap = new ArrayMap<>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -5); //-1 means yesterday & today(old => -5)

        final List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST,
                cal.getTimeInMillis(), System.currentTimeMillis());

        if (stats == null) {
            return;
        }

        ArrayMap<String, UsageStats> map = new ArrayMap<>();
        final int statCount = stats.size();
        for (int i = 0; i < statCount; i++) {
            final UsageStats pkgStats = stats.get(i);
            try {
                PackageInfo p = mPm.getPackageInfo(pkgStats.getPackageName(), 0);

                if ((!isSystemPackage(mPm.getPackageInfo(pkgStats.getPackageName(), 0)))) {

                    //pckg name jjjj
                    String packageName = p.packageName;


                    String appName = p.applicationInfo.loadLabel(context.getPackageManager()).toString();

                    /*String installedApp = "\"" + appName + "\"";
                    installedAppsList.add(installedApp);*/

                    mAppLabelMap.put(pkgStats.getPackageName(), appName);

                    UsageStats existingStats = map.get(pkgStats.getPackageName());

                    if (existingStats == null) {
                        map.put(pkgStats.getPackageName(), pkgStats);
                    } else {
                        existingStats.add(pkgStats);
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                // This package may be gone.
            }
        }
        Log.e("add_deatils_of_app", "Inmare1");
        final ArrayList<UsageStats> mPackageStats = new ArrayList<>(map.values());
        for (int index = 0; index < mPackageStats.size(); index++) {
            Log.e("add_deatils_of_app", "Inmare2");
            UsageStats pkgStats = mPackageStats.get(index);
            if (pkgStats != null) {
                final String label = mAppLabelMap.get(pkgStats.getPackageName());
                Log.e("add_deatils_of_app", "Inmare3");
                Log.e("Values", "Dtat" + label);
                //pckg name
                String packageName = pkgStats.getPackageName();

                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd "
                        + "HH:mm:ss");
                final String lastTimeUsed = formatter.format(new Date(pkgStats.getLastTimeUsed()));
                final String usageTime = String.valueOf(pkgStats.getTotalTimeInForeground() / 60000); //todo milisecond to minit  1 minit
                Log.e("MinitValue", "UsageTime" + usageTime);
                //final String usageTime = String.valueOf(pkgStats.getTotalTimeForegroundServiceUsed() / 60000);

                //passing data to api...
                if (checkConnection(context)) {
                    Log.e("add_deatils_of_app", "Inmare4");
                    int data_count = mAppsDetailsDatabaseHelper.getRowCount();
                    mAppsDetailsDatabaseHelper.close();
                    Log.e("add_deatils_of_app", "data_count?" + data_count);
                    //checking if offline db is having data or not

                    if (data_count > 0) {
                        Log.e("add_deatils_of_app", "Inmare5");
                        for (int i = 0; i <= data_count; i++) {
                            Log.e("add_deatils_of_app", "Inmare6");

                            try {
                                try {
                                    String dbLabel = mAppsDetailsDatabaseHelper.getAppName(i + 1);
                                    //get appCode by App Name

                                    AppDao db = ad.appDao();
                                    AppData appData = db.getAppByName(dbLabel);//get all details i.e name & code
                                    mAppCode = appData.code;
                                    Log.e("add_deatils_of_app00", "Inm appData.code>>>" + appData.code);
                                    String mAppName = appData.name;
                                    Log.e("add_deatils_of_app", "Inmare7");
                                    Log.d("FETCH", mAppName + " - " + mAppCode);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                // String add_deatils_of_app = "http://digiqualweb.hansaresearch.com/backend_app/index.php/AdminApi/add_deatils_of_app";
                                //"http://digiqualweb.hansaresearch.com/backend_app/index.php/AdminApi/add_deatils_of_app"
                                //  String add_deatils_of_app = "http://digiqualweb.hansaresearch.com/backend_app/index.php/AdminApi/add_deatils_of_app";
                                String add_deatils_of_app = "https://evolvu.in/backend_app/index.php/AdminApi/add_deatils_of_app";
                                //"https://evolvu.in/backend_app/index.php/AdminApi/add_deatils_of_app"
                                Log.e("AppCheke", "URL>>>" + add_deatils_of_app);

                                StringRequest stringRequestTwo = new StringRequest(Request.Method.POST, add_deatils_of_app,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                Log.e("add_deatils_of_app", "response>>>" + response);
                                                if (response != null && !response.equals("") && !response.equals("null")) {
                                                    try {
                                                        JSONObject jsonObject = new JSONObject(response);
                                                        String status = jsonObject.getString("status");
                                                        if (status.equals("true")) {
                                                            System.out.println("Mj" + status);
                                                            mAppsDetailsDatabaseHelper.clearData();
                                                            mAppsDetailsDatabaseHelper.close();
                                                            Log.d("SENT", status);
                                                        } else {
                                                            System.out.println(status);
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                        Log.i("get_app_usage:", "error1=> " + e.getMessage());
                                                    }
                                                } else {
                                                    Log.i("get_app_usage:", "error2=> " + response);
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                        Log.i("get_app_usage:", "error3=> " + error.getMessage());
                                    }
                                }) {
                                    @Override
                                    protected Map<String, String> getParams() {
                                        Map<String, String> params = new HashMap<>();
                                        params.put("code", mAppCode);
                                        params.put("user_id", user_id);

                                        //days duration from last poll
                                        //params.put("no_of_times_app_opened", String.valueOf(finalDifferenceNew));

                                        params.put("no_of_times_app_opened", formattedDate);
                                        params.put("last_apps_time", lastTimeUsed);
                                        params.put("time_spend_on_app", usageTime);
                                        System.out.println("MASTER_TABLE_APPS" + params);
                                        Log.e("add_deatils_of_app", "Param>>>" + params);
                                        return params;
                                    }
                                };
                                RequestHandler.getInstance(context).addToRequestQueue(stringRequestTwo);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        //get appCode by App Name
                        AppDao db = ad.appDao();
                        AppData appData = db.getAppByName(label);//get all details i.e name & code

                        if (appData != null) {
                            final String mAppCode = appData.code;
                            String mAppName = appData.name;
                            Log.d("FETCH", mAppName + " - " + mAppCode);


                            //String add_deatils_of_app="http://digiqualweb.hansaresearch.com/backend_app/index.php/AdminApi/add_deatils_of_app";
                            String add_deatils_of_app = "https://evolvu.in/backend_app/index.php/AdminApi/add_deatils_of_app";

                            Log.e("AppCheke", "URL>>add_deatils_of_app>" + add_deatils_of_app);

                            //"https://evolvu.in/backend_app/index.php/AdminApi/add_deatils_of_app
                            StringRequest stringRequestTwo = new StringRequest(Request.Method.POST, add_deatils_of_app,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            if (response != null && !response.equals("") && !response.equals("null")) {
                                                try {
                                                    JSONObject jsonObject = new JSONObject(response);
                                                    String status = jsonObject.getString("status");
                                                    if (status.equals("true")) {
                                                        System.out.println("Mj" + status);
                                                        Log.d("SENT", status);
                                                    } else {
                                                        System.out.println(status);
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    Log.i("get_app_usage:", "error1=> " + e.getMessage());
                                                }
                                            } else {
                                                Log.i("get_app_usage:", "error2=> " + response);
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    error.printStackTrace();
                                    Log.i("get_app_usage:", "error3=> " + error.getMessage());
                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("code", mAppCode);
                                    params.put("user_id", user_id);
                                    //days duration from last poll
//                                  params.put("no_of_times_app_opened", String.valueOf(finalDifferenceNew));
                                    params.put("no_of_times_app_opened", formattedDate);
                                    params.put("last_apps_time", lastTimeUsed);
                                    params.put("time_spend_on_app", usageTime);
                                    System.out.println("MASTER_TABLE_APPS" + params);
                                    Log.e("add_deatils_of_app", "Para>" + params);
                                    Log.e("AppCheke", "URL>params>add_deatils_of_app>" + params);

                                    return params;

                                }
                            };
                            RequestHandler.getInstance(context).addToRequestQueue(stringRequestTwo);

                        } else {
                            Log.d("FETCH", "App not found in master db" + label);
                        }
                    }
                } else {
                    //saving data to database
                    Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show();
                    mAppsDetailsDatabaseHelper.saveAppsDetails(label, lastTimeUsed, usageTime);
                    mAppsDetailsDatabaseHelper.close();
                }
            } else {
                Log.w(TAG, "No usage stats info for package:" + index);
            }
        }
    }

    /*This method is used to identify whether the app is System app or not, we don't want the system app
     * for our use case*/
    private static boolean isSystemPackage(PackageInfo pkgInfo) {
        return (pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }

    /*CHECK WHETHER INTERNET CONNECTION IS AVAILABLE OR NOT*/
    private static boolean checkConnection(Context context) {
        final ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = null;

        if (connMgr != null) {
            activeNetworkInfo = connMgr.getActiveNetworkInfo();
        }

        // connected to the internet
        if (activeNetworkInfo != null) {
            // connected to the mobile provider's data plan
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return true;
            } else return activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    /*Getting app application code saved in local db, which we are taking from Database through API
     * We only send the app code not the names of the app*/
    public static void getApplicationNameCode(final Context context) {
        Log.e("master_apps", "sein1");
        ad = AppDatabase.getAppDatabase(context);
        mAppsDetailsDatabaseHelper = new AppsDetailsDatabaseHelper(context);
        mAppsCodesDatabaseHelper = new AppsCodesDatabaseHelper(context);

        /*getting codeData from api and saving int local database to get the app code from
        our server */

        //String master_apps= "http://digiqualweb.hansaresearch.com/backend_app/index.php/AdminApi/master_apps";
        // String master_apps= "http://digiqualweb.hansaresearch.com/backend_app/index.php/AdminApi/master_apps";
        String master_apps = "https://evolvu.in/backend_app/index.php/AdminApi/master_apps";
        Log.e("AppCheke", "URL>>master_apps>" + master_apps);

        StringRequest stringRequestNews = new StringRequest(Request.Method.POST, master_apps, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && !response.equals("") && !response.equals("null")) {
                    try {
                        Log.e("master_apps", "response>>" + response);
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("true")) {
                            JSONArray appsArray = jsonObject.getJSONArray("apps");
                            for (int index = 0; index < appsArray.length(); index++) {
                                JSONObject appsObject = appsArray.getJSONObject(index);
                                String code = appsObject.getString("code");
                                String name = appsObject.getString("name");//packagename

                                AppData appData = new AppData();
                                appData.name = name;
                                appData.code = code;

                                //init dao and perform operation
                                AppDao dao = ad.appDao();
                                dao.insert(appData);
                                Log.d("INSERT", name);

                                mAppsCodesDatabaseHelper.saveAppsCodes(code, name);
                                mAppsCodesDatabaseHelper.close();
                            }
                        } else {
                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i("get_app_usage:", "error=> " + e.getMessage());
                    }
                } else {
                    Log.i("get_app_usage:", "error=> " + response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e("master_apps", "seineeeroroo" + error.getMessage());

                Log.i("get_app_usage:", "error=> " + error.getMessage());
            }
        });
        RequestHandler.getInstance(context).addToRequestQueue(stringRequestNews);
    }

    /*Getting permission from user to get the usage stats*/
    public static void getPermissions(Context context) {
        UsageStatsManager mUsageStatsManager;
        List<UsageStats> statsNew = null;

        mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        if (mUsageStatsManager != null) {
            statsNew = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0,
                    System.currentTimeMillis());
        }
        boolean isEmpty = statsNew.isEmpty();

        if (isEmpty) {
            Intent i = new Intent();
            i.setAction(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            i.addFlags(FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

    /*Getting User Id from user and saving it in the sharedPref*/
    public static void setUserId(Context context, String user_id) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY, user_id);
        editor.apply();
    }

    /*To retrieve the User Id from the sharedPrefs*/
    public static String getUserId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String user_id = sharedPreferences.getString(KEY, "");
        return user_id;
    }


    public static void setFrequency(Context context, String frequency){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("frequency", Integer.parseInt(frequency));
        editor.apply();
    }

    public static int getFrequency(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        int user_id = sharedPreferences.getInt("frequency", -1);
        return user_id;
    }

    public static String get_installed_app_frequency_post(final Context mcontext, final String userId) {
        RequestQueue referenceQueue = Volley.newRequestQueue(mcontext);

        // String get_installed_app_frequency = "https://evolvu.in/backend_app/index.php/AdminApi/get_installed_app_frequency";
        String get_installed_app_frequency = "https://evolvu.in/backend_app/index.php/AdminApi/get_installed_app_frequency";
        Log.e("app_frequency", "counter" + counter);

        Log.e("app_frequency", "URL>>get_installed_app_frequency_pos>" + get_installed_app_frequency);

        final StringRequest frequency = new StringRequest(Request.Method.POST, get_installed_app_frequency, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("app_frequency", "Responce" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("true")) {
                        Log.e("AppCheke", "Call All Method");
                        JSONArray app_frequency = jsonObject.getJSONArray("installed_app_frequency");
                        for (int i = 0; i < app_frequency.length(); i++) {
                            JSONObject all_app_frequency = app_frequency.getJSONObject(i);
                            installed_app_frequency= all_app_frequency.getString("installed_app_frequency");

                        }

                        JSONArray array = jsonObject.getJSONArray("installed_app_frequency");

                        Timer timer = new Timer();
                        TimerTask hourlyTask = new TimerTask() {
                            @Override
                            public void run() {
                                Log.e("cocounter", "counter" + counter+"<>>>>>"+600000*Integer.valueOf(installed_app_frequency));
                                // your code here...


                                AppCheck.getApplicationNameCode(mcontext);   // todo master list   1
                                AppCheck.getPermissions(mcontext);// get permition
                                AppCheck.setUserId(mcontext, userId);//todo user id  passing
                                //work
                                AppCheck.sendInstalledAppsToServer(userId, mcontext);//todo app list    2
                                AppCheck.sendUserDataToServer(userId, mcontext);// todo app list details in appliction 3 polling time date and time spend on app listing
                                ad = getAppDatabase(mcontext);

                            }

                        };
                        // 600000*frequ //todo formula setting time

                        ;
                        //String Type  2 ,5 10
// schedule the task to run starting now and then every hour...
                    int   iaf= 600000*Integer.valueOf(installed_app_frequency);
                        Log.e("VAluesDFD","Values>>>>>>>>>>>>>>>>>>>"+(600000*Integer.valueOf(installed_app_frequency)));
                        timer.schedule(hourlyTask, 0l, iaf);//2 minit  1000 *60*60


                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("AppCheke", "frequency_pos" + error.getMessage());
            }
        });
        referenceQueue.add(frequency);
        //RequestHandler.getInstance(mcontext).addToRequestQueue(frequency);
        return frequencyTime;
    }
}
