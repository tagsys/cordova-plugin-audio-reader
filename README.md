# cordova-plugin-audio-reader

<hr/>

This plugin provides the ability to read raw audio data from smartphone. It benefiting for the APP development using ionic framework.

NOTE: The current implementation only support Android. A future implementation will support iOS.

This plugin defines a gloabl <code>window.plugins.audioReader</code>

Despite in the global scope, it is not available until after the deviceready event.

<code>
document.addEventListener('deviceready', function(){
    console.log(window.plugins.audioReader)
}, false)
</code>

# Installation

<code>
cordova plugin add cordova-plugin-audio-reader
</code>

Supported Platforms

- Andriod

# Quick Example

<code>
   var audioReader = window.plugins.audioReader;
   //with defualt configuration
   audioReader.create(null, function(){
      console.log('It is successufl to create audio reader.');
   }, function(){
      console.log('It fails to create audio reader');
   });
   
   audioReader.start();
   
   audioReader.read(44100, 0, function(result){
      var leftChannel = result.leftChannel;
      var rightChannel = result.rightChannel;
      
      //handle the data read from left or right channel.
      ...
      
   }, function(){
      console.log('It fails to read audio data.');
   })
   
</code>
