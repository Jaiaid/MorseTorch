# MorseTorch

Repository of pet android project. Here we are trying to build an app to transmit morse code using flashlight feature.

The app is published and can be found in https://play.google.com/store/apps/details?id=com.bitweaver.morsetorch

Write a text in the text area and press start to transmit the word as morse code using the flashlight

Morse code contains dot and dash. I have represented dot with light flashing and dash is same with light flashing but three times longer
Only english letters are supported. Dot dash code taken from https://en.wikipedia.org/wiki/Morse_code

## Features (Version 3):
* Indication of upto what portion of message has been transmitted
* Transmission type select
  * One shot: message will be transmitted once
  * Repeat: message will be transmitted in loop
* control length of flashing for dot, dash will be 3x of that. Dot length can be from 500ms to 4s
* UI colorization is flexible for dark and light theme, button color changes according to transmission off or ongoing

## Privacy Policy
* No personally identifiable information is stored
* No data is transmitted to anywhere, infact no usage of Wifi, Bluetooth or anyother connectivity features.
* No data usage. Therefore delation of user data is not applicable
* Requested permissions
  * flashlight permission (```android.permission.FLASHLIGHT```), camera permission not needed as app targets minSDK version 23
  * Requested permission is to operate flash light to transmit morse code using flashlight
* For clarification/report security vulnerability, contact ```bitpatternweaver@gmail.com```