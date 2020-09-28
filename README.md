

### Basic setup

`ionic cordova plugin add https://github.com/avinashkgit/AppCheckPlugin`

### Add domain to your network_security_config.xml 
resources/android/xml/network_security_config.xml 
` <domain includeSubdomains="true">evolvu.in</domain>`

### SAMPLE CODE FOR USAGE

### Getting user access permission

`getUserAccessPermission = () => {
      cordova.plugins.AppCheckPlugin.getUsageAccessPermissions();
}`


### Using Send Installed Apps To Server

`sendInstalledAppsToServer = () => {
    cordova.plugins.AppCheckPlugin.sendInstalledAppsToServer(
      'user_id',
      () => {},
      () => {}
    );
}`

### Using Send User Data To Server
`sendUserDataToServer = () => {
    cordova.plugins.AppCheckPlugin.sendUserDataToServer(
      'user_id',
      () => {},
      () => {}
    );
}`

### Running background fetch
Note: Duration to be in minutes
`runBackgroundFetch = () => {
    cordova.plugins.AppCheckPlugin.scheduleTask(
      'user_id',
      2,
      () => {},
      () => {}
    );
}`

