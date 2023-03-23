# MorseTorch
Repositroy of pet android project. Here we are trying to build an app to transmit morse code using flashlight feature.

The app is published and can be found in https://play.google.com/store/apps/details?id=com.bitweaver.morsetorch

Write a text in the text area and press start to transmit the word as morse code using the flashlight

Morse code contains dot and dash. I have represnted dot with light flashing and dash is same with light flashing but three times longer
Only english letter support. Dot dash code taken from https://en.wikipedia.org/wiki/Morse_code

Features:
* Indication of upto what portion of message has been transmitted
* Transmission type select
  * One shot: message will be transmitted once
  * Repeat: message will be transmitted in loop
* control length of flashing for dot, dash will be 3x of that
* UI colorization is flexible for dark and light theme, button color changes according to transmission off or ongoing
