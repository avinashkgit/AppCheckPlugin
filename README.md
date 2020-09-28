

### Basic setup

`ionic cordova plugin add https://github.com/avinashkgit/AppCheckPlugin`


### SAMPLE CODE FOR USAGE

### getting user access permission

`getUserAccessPermission = () => {
      cordova.plugins.AppCheckPlugin.getUsageAccessPermissions();
}`


### using sendInstalledAppsToServer

`sendInstalledAppsToServer = () => {
    cordova.plugins.AppCheckPlugin.sendInstalledAppsToServer(
      'user_id',
      () => {},
      () => {}
    );
}`

### using sendUserDataToServer
`sendUserDataToServer = () => {
    cordova.plugins.AppCheckPlugin.sendUserDataToServer(
      'user_id',
      () => {},
      () => {}
    );
}`

### running background fetch
`runBackgroundFetch = () => {
    cordova.plugins.AppCheckPlugin.scheduleTask(
      'user_id',
      2,
      () => {},
      () => {}
    );
}`

