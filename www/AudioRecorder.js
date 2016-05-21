
var _extend = function(destination, source) {

  return destination;
}

/**Audio Source ***/
var AudioSource = {
  DEFAULT : 0,
  MIC : 1,
  VOICE_UPLINK : 2,
  VOICE_DOWNLINK : 3,
  VOICE_CALL : 4,
  CAMCORDER : 5,
  VOICE_RECOGNITION : 6,
  VOICE_COMMUNICATION : 7,
  REMOTE_SUBMIX : 8
}

/**Channel mode***/
var AudioChannel={
  CHANNEL_IN_MONO : 16,
  CHANNEL_IN_STEREO : 12,
}

/***Audio format***/
var AudioFormat = {
  ENCODING_PCM_16BIT : 2,
  ENCODING_PCM_8BIT : 3,
  ENCODING_PCM_FLOAT : 4
}

/***Default config****/
var DEFAULT_CONFIG = {
    source: AudioSource.MIC,
    channel: AudioChannel.CHANNEL_IN_STEREO,
    format: AudioFormat.ENCODING_PCM_16BIT,
    sampleRate:44100, // sample frequency
    frameLength:0.09*44100 //the min length read from the device
}

var AudioRecorder = function(){
}

AudioRecorder.prototype.create = function(successCallback, errorCallback, config){

  console.log("create AudioRecorder");

  var _config = DEFAULT_CONFIG;

  if(config){
    for (var property in config) {
      _config[property] = config[property];
    }
  }

  console.log("Audio recorder configuration:");

  cordova.exec(successCallback, errorCallback, "AudioRecorder", "create", [_config]);

}

AudioRecorder.prototype.start = function (successCallback, errorCallback) {
  console.log("It is going to start.");
  cordova.exec(successCallback, errorCallback, "AudioRecorder", "start", []);
};

AudioRecorder.prototype.stop = function (successCallback, errorCallback) {
  console.log("It is going to stop.");
  cordova.exec(successCallback, errorCallback, "AudioRecorder", "stop", []);
};

AudioRecorder.prototype.read = function (length, successCallback, errorCallback) {

  console.log("It is going to read:"+length);
  if(!length){
    length = 128;
  }
  cordova.exec(successCallback, errorCallback, "AudioRecorder", "read", [length]);
};

AudioRecorder.prototype.clear = function (successCallback, errorCallback) {

  cordova.exec(successCallback, errorCallback, "AudioRecorder", "clear",[]);
};

AudioRecorder.install = function () {
  if (!window.plugins) {
    window.plugins = {};
  }
  window.plugins.audioRecorder = new AudioRecorder();

  return window.plugins.audioRecorder;
};

cordova.addConstructor(AudioRecorder.install);
