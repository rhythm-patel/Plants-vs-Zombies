# Plants v/s Zombies

We have cloned Plants v/s zombies with much of the same functionalities as in the original version of the game. Our game offers 5 different levels (increasing in order of difficulty) with 5 different plants and 4 different zombies.

The game uses Java as its base with JavaFX as the software platform. Some of the graphical components has been developed using CSS.

### Features of our game:
* Difficulty level progessively increases as one plays the game 
* Plants are unlocked as one passes the levels
* Sun drops at regular interval
* Smooth graphics
* The game can be saved and loaded anytime
* The game consists of an almanac - serves as an encyclopedia for the plants and zombies
* Simple and user interactive GUI
* Plants have their own refresh time, and can only be planted after some intervals
* Suns are used as tokens for buying plants
* Has the same music as the original version

### Resources and modules used
We used Timehandler class to setup different timelines for different objects. The class effectively handles the different threads created in the program, and help us maintain our focus on the much bigger picture.

All the pictures and gifs used in the game have been taken from Plants v/s zombies fandom [page](https://plantsvszombies.fandom.com/wiki/Main_Page). The images have been edited to better suit the functionalities for our game. 

### Required modules for running the game:
The game was developed using Java 13 and Javafx 13 SDK, but works well on 13 and above. I am attaching links to the newer versions of these modules.
* [Java SE 14](https://www.oracle.com/java/technologies/javase/jdk14-archive-downloads.html)
* [Javafx-sdk-14](https://gluonhq.com/products/javafx/)

### Modifying the code to play the background Music
If you do not want to output a lot of runtime errors while running the game, you will have to modify the path of the folder in the java program. It's a really simple fix, but if you do not mind not hearing the sweet melody of the Plants vs Zombies music, you can skip to "Running the game" section.

But if you want to hear it, here's what you need to do.
1. Open the Java file named main.java
2. Jump to line number 2225
3. It looks somewhat like this: 
```
AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File("/Users/rhythm/IdeaProjects/Plants-vs-Zombies/PvZmusic.wav"));
Clip clip = AudioSystem.getClip();
clip.open(audioIn);
clip.start();
clip.loop(Clip.LOOP_CONTINUOUSLY);
```
4. Change the path in the new File() method to the actual path where your music file "PvZmusic.wav" is present and you are done!

### Running the game
For now, the application of our game is not available. We may put it up in the future. Fellow developers can directly pull the code using this command to a local repository:
<br>

	git init // For initializing the local repository
	git pull https://github.com/rhythm-patel/Plants-vs-Zombies.git	
<br>The project can be opened using any IDE and compiled easily. Before compiling, please set the JRE to Java 14, SDK path to your Java 14 Folder and add all the Javafx 14 lib to libraries section of project settings.

Also you will need to setup the VM Option as follows (It shud be present in the run configurations of the IDE):

	--module-path "*Path/of/your/JavaFx14/lib*" --add-modules javafx.controls,javafx.fxml
	
Now you can simply run the main class to run the game.  

### Use Case and UML Diagrams:
You can find the  [UML diagram](https://github.com/rhythm-patel/Plants-vs-Zombies/blob/master/Plants%20vs%20Zombies%20UML%20Class%20Diagram.pdf) and the Use case over here.  

![use case](https://github.com/rhythm-patel/Plants-vs-Zombies/blob/master/Use%20case.png?raw=true).

Hope this helps our fellow coders and developers!!
