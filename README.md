# cordova-plugin-audio-reader


This plugin provides the ability to read raw audio data from smartphone, benefiting for the APP development using ionic framework.

NOTE: The current implementation only support Android. A future implementation will support iOS. Our plugin is different from

- <code>cordova-plugin-media</code>: This plugin stores the data in a file instead of reporting to upper layer, while our plugin provides more low-level and direct access to low-level raw audio data.
- <code>cordova-plugin-audio-recorder-api</code>: We borrow the basic idea from this plugin.However, this plugin still stores the audio data in a file.



## Supported Platforms

- [Ionic](http://ionicframework.com/) + Andriod

## Installation

```js
cordova plugin add cordova-plugin-audio-reader
```

## Quick Example

This plugin defines a gloabl variable <code>window.plugins.audioReader</code>. Despite in the global scope, it is not available until after the deviceready event.

```js
document.addEventListener('deviceready', function(){
    window.plugins.audioReader.init();
}, false)
```

A quick example is given as follows:
```js

    var config = {
    	source: AudioSource.DEFAULT,
  		channel: AudioChannel.CHANNEL_IN_MONO,
  		format: AudioFormat.ENCODING_PCM_16BIT,
  		sampleRate:44100 // sample frequency
    }
    
	document.addEventListener('deviceready', function(){
        window.plugins.audioReader.init();
    }, false)

    $scope.start = function(){
    
    	windows.plugins.audioReader.start();
    }
   
   $scope.stop = function(){
   		
        windows.plugins.audioReader.stop();
   }
   
   $scope.record = function(){
   		
       setInterval(function(){
       
        	windows.plugins.audioReader.read(44100,0,function(result){
            	var leftChannel = result.leftChannel;
      			var rightChannel = result.rightChannel;
                //handle the data read from left or right channel.
                ....
            })
       	
       },function(error){
       		console.log(error)
       },
       1000)
   }
      
```

## Constants and Configuration

The following constants are used for configuration

Audio Source
- AudioSouce.DEFAULT = 0
- AudioSource.MIC = 1

Audio Channel
- AudioChannel.CHANNEL_IN_MONO = 16
- AudioChannel.CHANNEL_STEREO = 12

Audio Format
- AudioFormat.ENCODING_PCM_16BIT = 2
- AudioFormat.ENCODING_PCM_8BIT = 3
- AudioFormat.ENCODING_PCM_FLOAT = 4

Default configuration:
```
var defaultConfig = {
	source: AudioSource.DEFAULT,
    channel: AudioChannel.CHANNEL_IN_MONO,
    format: AudioChannel.ENCODING_PCM_16BIT,
    sampleRate:44100
    
}
```

You could define your own configuration as a parameter of <code>init</code> function.
```js
document.addEventListener('deviceready', function(){
        window.plugins.audioReader.init(yourConfig);
    }, false)
```

## Methods

- <code>audioReader.init</code>: Initailizes the <code>audioReader</code> with customized configuration.
- <code>audioReader.start</code>: Starts the recording.
- <code>audioReader.stop</code>: Stops the recording.
- <code>audioReader.read</code>: Reads the raw data from audio channels.
- <code>audioReader.clear</code>: Clears the raw data from buffers.

### audioReader.init
Initializes the <code>audioReader</code>
```js
 audioReader.init(config, successCallback, errorCallback)
```

### audioReader.start
Starts the recording. Note that all the raw data are stored in buffer.
```js
 audioReader.start(successCallback, errorCallback)
```

### audioReader.stop
Stops the recording. 
```js
 audioReader.stop(successCallback, errorCallback)
```

### audioReader.clear
Clears the data stored in the buffers.
```javascript
 audioReader.clear()
```

### audioReader.read
Reads raw audio data from buffers. in buffer.
```js
audioReader.read(length, channel, successCallback, errorCallback)
```
Parameters:
- length: how many data do you want to read.
- channel: which channel do you want to read from. 0 - both, 1- left channel, 2- right channel.
- successCallback: the passed parameter contains two variables: <code>leftChannel</code> and <code>rightChannel</code>

For example:
```
  audioReader.read(44100, 0, function(result){
     //return an array including audio data read from left channel.
  	 var leftArray = result.leftChannel;
     //reutrn an array including audio data read from right channel.
     var rightArray = result.rightChannel;
  })
```
