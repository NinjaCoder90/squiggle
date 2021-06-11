
# Our Java Project

Based on RMI (Remote Method Invocation) implementation, this project lets the clients communicate among them through a Server.
RMI creates a connection between Client, Shared and Server, so that a client can draw a picture, and the other users/clients can enjoy guessing the correct corrisponding word.

- When a user enters the game, he/she will wait for the other user to join, so that they can play together.

- The Client that has temporary command is able to see the word and has some seconds to draw it as he/she wants to, using colors and the pad available.

- The others gamers have the goal to guess the right word corresponding to the picture that they see.
  The Client's control of drawing and guessing switches every round.
  The first one to guess will get 49 points; the second 39, and the third 33 points.

- The winner is the one who has accumulated more points at the end of the game.


## This is how you use my Project

NOTE: In order to run this project you will only need Java 15 or above.

There is a list of things to do:
1) git clone https://gitlab.inf.unibz.it/Oussama.Driouache/pp_202021_misterycrew_id36129.git
2) cd pp_202021_misterycrew_id36129
3) - Windows: mvnw.cmd clean install
   - Linux/OS: ./mvnw clean install
4) cd Server
5) java -jar target/Server-1.0-SNAPSHOT-jar-with-dependencies.jar
6) cd ApplicationFX
7) - Windows: mvnw.cmd javafx:run
   - Linux/OS: ./mvnw javafx:run

Now open another terminal and repeat step 6 and 7 to test the application
(Since this is a distributed Version).

REMEMBER: open the server and the ApplicationFX in different terminals
and in the order in which are specified in the "list of things to do".

P.s: To shut down the server hold Ctrl + C (please also make sure to 
close all the ApplicationFX before the server).

Enjoy the Game :)

## These are my dependencies

1. Java

# The contributors of this project are

1. Oussama Driouache
2. Emanuele Pippa
3. Emmanuel Scopelliti
