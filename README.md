# cordova-plugin-audio-reader


This plugin provides the ability to read raw audio data from smartphone, benefiting for the APP development using ionic framework.

NOTE: The current implementation only support Android. A future implementation will support iOS.

This plugin defines a gloabl variable <code>window.plugins.audioReader</code>. Despite in the global scope, it is not available until after the deviceready event.

```js
document.addEventListener('deviceready', function(){
    window.plugins.audioReader.init();
}, false)
```

## Supported Platforms

- [Ionic](http://ionicframework.com/) + Andriod

## Installation

```js
cordova plugin add cordova-plugin-audio-reader
```

## Quick Example

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
