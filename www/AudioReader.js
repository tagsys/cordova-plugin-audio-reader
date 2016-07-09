

var argscheck = require('cordova/argscheck');
var utils = require('cordova/utils');
var exec = require('cordova/exec');


//Audio source
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

//Audio channel
var AudioChannel={
  CHANNEL_IN_MONO : 16,
  CHANNEL_IN_STEREO : 12,
}

//Audio format
var AudioFormat = {
  ENCODING_PCM_16BIT : 2,
  ENCODING_PCM_8BIT : 3,
  ENCODING_PCM_FLOAT : 4
}

//default configuration
var defaultConfig = {
  source: AudioSource.DEFAULT,
  channel: AudioChannel.CHANNEL_IN_MONO,
  format: AudioFormat.ENCODING_PCM_16BIT,
  sampleRate:44100 // sample frequency
}


var audioReaderObjects = {};


/***
 * This class provides access to the audio reader, interfaces to audio.
 *
 */
var AudioReader = function(config, successCallback, errorCallback){

  argscheck.checkArgs('OFFF','AudioReader', arguments);
  audioReaderObjects[this.id] = this;

  var _config = defaultConfig;

  if(config){
    for (var property in config) {
      _config[property] = config[property];
    }
  }

  exec(successCallback, errorCallback, "AudioReader", "create", [_config]);
}


//"static" function to return existing objects.
AudioReader.get = function(id){
  return audioReaderObjects[id];
}


/**
 * start recording
 * @param successCallback The callback to be called when starting is successfully done.
 * @param errorCallback The callbacl to be called when starting failed.
 */
AudioReader.prototype.start = function (successCallback, errorCallback) {
  console.log("It is going to start.");
  exec(successCallback, errorCallback, "AudioReader", "start", []);
};


AudioReader.prototype.stop = function(successCallback, errorCallback){
  console.log("It is going to stop.");
  exec(successCallback, errorCallback, "AudioReader", "stop", []);
}

AudioReader.prototype.read = function (length, channel, successCallback, errorCallback) {

  console.log("It is going to read:"+length);
  if(!length)  length = 128;
  if(!channel) channel = 0;

  exec(successCallback, errorCallback, "AudioReader", "read", [length,channel]);
};

AudioReader.prototype.clear = function (successCallback, errorCallback) {

  exec(successCallback, errorCallback, "AudioReader", "clear",[]);
};

module.exports = AudioReader;


// AudioReader.install = function () {
//
//   if (!window.plugins) {
//     window.plugins = {};
//   }
//   window.plugins.audioReader = new AudioReader();
//
//   return window.plugins.audioReader;
// };
//
// cordova.addConstructor(AudioReader.install);
