function AudioRecorder() {
}

AudioRecorder.prototype.record = function (successCallback, errorCallback, duration) {
  cordova.exec(successCallback, errorCallback, "AudioRecorder", "record", duration ? [duration] : []);
};

AudioRecorder.prototype.stop = function (successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "AudioRecorder", "stop", []);
};

AudioRecorder.prototype.playback = function (successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "AudioRecorder", "playback", []);
};

AudioRecorder.install = function () {
  if (!window.plugins) {
    window.plugins = {};
  }
  window.plugins.audioRecorder = new AudioRecorder();
  return window.plugins.audioRecorder;
};

cordova.addConstructor(AudioRecorder.install);
