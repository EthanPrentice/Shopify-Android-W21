# Shopify-Android-W21


## Features
* Variable board size
* Supports any number of words, the word list at the bottom is scrollable
* Tracks which words the user has found, along with a total tally
* Randomizes where the words are placed
  * In the case where there are words generated twice, this is properly handled
* Slick, clean UI with animations
  * Also supports dark mode
* Supports portrait and landscape orientations
* User can swipe over words. The line color clearly indicates whether:
  * A word was found
  * A word was not found
  * A word that has already been found was found
* Game board supports tablets, but the UI isn't as refined for the word list and other components
* The board generation is done in a different thread
  * If the generation takes longer (older device / large board), a busy spinner will appear to make it clear to the user that work is being done

## Demo
Video: https://youtu.be/RKg9DmWjFz8
### Images
##### Please check out the demo too - it shows a lot of animations, orientation changes, and dark mode
No words found             |  Some words Found         |  Game over
:-------------------------:|:-------------------------:|:-------------------------:|
<img src="https://imgur.com/vCl5kgA.jpg" alt="No words found" width="300px">  |  <img src="https://imgur.com/fqfH16H.jpg" alt="Some words found" width="300px">   |  <img src="https://imgur.com/teIUHFc.jpg" alt="Game over" width="300px">
