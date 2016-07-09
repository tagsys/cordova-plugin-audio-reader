
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
  source: AudioSource.DEFAULT,
  channel: AudioChannel.CHANNEL_IN_MONO,
  format: AudioFormat.ENCODING_PCM_16BIT,
  sampleRate:44100 // sample frequency
}

var AudioReader = function(){

}

AudioReader.prototype.init = function( config, successCallback, errorCallback){

  var _config = DEFAULT_CONFIG;

  if(config){
    for (var property in config) {
      _config[property] = config[property];
    }
  }

  console.log("Audio recorder configuration:");

  cordova.exec(successCallback, errorCallback, "AudioReader", "init", [_config]);

}

AudioReader.prototype.start = function (successCallback, errorCallback) {
  console.log("It is going to start.");
  cordova.exec(successCallback, errorCallback, "AudioReader", "start", []);
};

AudioReader.prototype.stop = function (successCallback, errorCallback) {
  console.log("It is going to stop.");
  cordova.exec(successCallback, errorCallback, "AudioReader", "stop", []);
};

AudioReader.prototype.read = function (length, channel, successCallback, errorCallback) {

  console.log("It is going to read:"+length);
  if(!length)  length = 128;
  if(!channel) channel = 0;
  cordova.exec(successCallback, errorCallback, "AudioReader", "read", [length,channel]);
};

AudioReader.prototype.clear = function (successCallback, errorCallback) {

  cordova.exec(successCallback, errorCallback, "AudioReader", "clear",[]);
};

AudioReader.install = function () {

  if (!window.plugins) {
    window.plugins = {};
  }
  window.plugins.audioReader = new AudioReader();

  return window.plugins.audioReader;
};

cordova.addConstructor(AudioReader.install);