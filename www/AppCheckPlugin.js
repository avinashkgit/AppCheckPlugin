var exec = require('cordova/exec');
var EMPTY_FN = function () {};

module.exports = {
  STATUS_RESTRICTED: 0,
  STATUS_DENIED: 1,
  STATUS_AVAILABLE: 2,

  FETCH_RESULT_NEW_DATA: 0,
  FETCH_RESULT_NO_DATA: 1,
  FETCH_RESULT_FAILED: 2,

  NETWORK_TYPE_NONE: 0,
  NETWORK_TYPE_ANY: 1,
  NETWORK_TYPE_UNMETERED: 2,
  NETWORK_TYPE_NOT_ROAMING: 3,
  NETWORK_TYPE_CELLULAR: 4,

  configure: function (callback, failure, config) {
    if (typeof callback !== 'function') {
      throw 'BackgroundFetch configure error:  You must provide a callback function as 1st argument';
    }
    config = config || {};
    failure = failure || EMPTY_FN;
    exec(callback, failure, 'BackgroundFetch', 'configure', [config]);
  },

  finish: function (taskId, success, failure) {
    if (typeof taskId !== 'string') {
      throw 'BackgroundGeolocation.finish now requires a String taskId as first argument';
    }
    success = success || EMPTY_FN;
    failure = failure || EMPTY_FN;
    exec(success, failure, 'BackgroundFetch', 'finish', [taskId]);
  },

  start: function (success, failure) {
    success = success || EMPTY_FN;
    failure = failure || EMPTY_FN;
    exec(success, failure, 'BackgroundFetch', 'start', []);
  },

  stop: function (success, failure) {
    success = success || EMPTY_FN;
    failure = failure || EMPTY_FN;
    exec(success, failure, 'BackgroundFetch', 'stop', []);
  },

  scheduleTask: function (userId, frequency, success, failure) {
    var config = {
        taskId: '001',
        delay: 1000, // milliseconds
        forceAlarmManager: true,
        periodic: true,
        stopOnTerminate: false,
        startOnBoot: true,
        enableHeadless: true,
        requiredNetworkType: cordova.plugins.AppCheckPlugin.NETWORK_TYPE_ANY
      }
    if (userId) {
      if (typeof config !== 'object')
        throw '[BackgroundFetch stopTask] ERROR:  The 1st argument to scheduleTask is a config {}';
      success = success || EMPTY_FN;
      failure = failure || EMPTY_FN;
      exec(success, failure, 'AppCheckPlugin', 'setFrequency', [frequency]);
      exec(success, failure, 'AppCheckPlugin', 'setUserId', [userId]);
      exec(success, failure, 'BackgroundFetch', 'scheduleTask', [config]);
    } else {
      throw 'User id required';
    }
  },

  stopTask: function (taskId, success, failure) {
    if (typeof taskId !== 'string')
      throw '[BackgroundFetch stopTask] ERROR: The 1st argument must be a taskId:String';
    success = success || EMPTY_FN;
    failure = failure || EMPTY_FN;
    exec(success, failure, 'BackgroundFetch', 'stop', [taskId]);
  },

  status: function (success, failure) {
    success = success || EMPTY_FN;
    failure = failure || EMPTY_FN;
    exec(success, failure, 'BackgroundFetch', 'status', []);
  },

  sendInstalledAppsToServer: function (arg0, success, error) {
    exec(success, error, 'AppCheckPlugin', 'sendInstalledAppsToServer', [arg0]);
  },

  sendUserDataToServer: function (arg0, success, error) {
    exec(success, error, 'AppCheckPlugin', 'sendUserDataToServer', [arg0]);
  },

  getUsageAccessPermissions: function (success, error) {
    exec(success, error, 'AppCheckPlugin', 'getPermissions');
  }
};
