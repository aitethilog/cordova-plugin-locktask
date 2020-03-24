module.exports = {
  startLockTask: function (successCallback, errorCallback, adminClassName) {
    if (adminClassName == null) {
      adminClassName = '';
    }
    cordova.exec(successCallback, errorCallback, "LockTask", "startLockTask", [adminClassName]);
  },
  stopLockTask: function (successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "LockTask", "stopLockTask", []);
  },
  isInKiosk: function (callback) {
	  cordova.exec(function (out) {
		  callback(out == "true");
	  }, function (error) {
		  alert("LockTask.isInKiosk failed: " + error);
	  }, "LockTask", "isInKiosk", []);
  },
  removeDeviceOwner: function (successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "LockTask", "removeDeviceOwner", []);
  },
  isSetAsLauncher: function (callback) {
    if(/ios|iphone|ipod|ipad/i.test(navigator.userAgent)) {
      callback(false); // ios not supported - cannot be in kiosk
      return;
    }
    cordova.exec(function (out) {
      callback(out == "true");
    }, function (error) {
      alert("LockTask.isSetAsLauncher failed: " + error);
    }, "LockTask", "isSetAsLauncher", []);
  },
  exitLauncher: function () {
	cordova.exec(function () {}, function (error) {
		alert("LockTask.exitLauncher failed: " + error);
	}, "LockTask", "exitLauncher", []);
  },
  selectLauncher: function () {
	cordova.exec(function () {}, function (error) {
		alert("LockTask.selectLauncher failed: " + error);
	}, "LockTask", "selectLauncher", []);
  },
  removeLauncher: function () {
	cordova.exec(function () {}, function (error) {
		alert("LockTask.removeLauncher failed: " + error);
	}, "LockTask", "removeLauncher", []);
  }
};
