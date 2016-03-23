##DiceCheck

####An Android app to check dice fairness.

Basic usage: select number of sides on dice, tap buttons to enter values you roll (with real dice or coins). When enough rolls have occurred to get a useful sampling then view statistics and bar charts on **Stats** screen. Save roll sets to **Log** file for later use - allows continually evaluating the same dice over time.

- supports 2 to 20 side dice / coins
- can show coin faces for 2 sides, dots for 6 sides
- can set hexadecimal mode for 16 sides
- optional sounds / vibration for entries
- performs basic Chi-Squared statistical evaluation
- has a nifty biased/fair LED-like bar chart
- can log rolls to file for continuation later
- can rename, merge, delete roll files

See my [blog entry](http://www.neocogent.com/blog/2015/04/dicecheck-release.html) for more background info and sample screens.

This project was built using Eclipse with Android SDK installed as described on the Android Developers web site. 

This git repo should be cloned under the workspace directory in that tree arrangement. Load up the Android IDE and build. 

This app can also be built from the command line as follows:

- add correct Android sdk/tools to PATH 
- you probably need to "android update project --path . --subprojects --target android-19" for this and the two subprojects (gridlayout_v7 and appcompat_v7_3) before starting. 
- install ant if not already, eg. sudo apt-get install ant
- make sure JAVA_HOME is set for correct jdk path, eg. "export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-i386/" worked for me
- change to workspace/DiceCheck directory
- run "ant clean debug"


I've included a final, signed apk file here as well for anyone who just wants to install without building.

Currently this app is only available (other than here) on the Amazon App Store. It was my first attempt at Android programming after more than 12 years away from doing any Java at all.

Enjoy.


