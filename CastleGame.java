// Mal Hoti
// 22/11/2022
// VERSION 8
// BRIEF OVERVIEW OF PURPOSE
/*
A castle game where the player is stuck inside
 They need to gather enough coins by taking items scattered all over the castle
 some of the items are guarded by invisible traps
 player can buy perks to either:
 - disable traps
 - reveal how many traps there are in a room
 
once 30 coins have been accumulated then the player can buy the castle key and win the game
if player reaches 0 coins they lose the game
player can load a previous game by saving

*/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

class Map 
{
String[] castleLayout;
int [] itemsPerRoom;
int[] trapsPerRoom;
int[] scannerBoughtPerRoom;  //1 = scanner was bought 0 = scanner wasnt
int maxNumberOfItemsPerRoom; 
int maxNumberOfTrapsPerRoom;
int pointsTakenFromTrap;
int costOfScanner;
int costToDisableTrap;
int costOfCastleKey;
int roomNumToExit;
}

class Player
{
String name;
int coins;
int positionOnMap;
boolean hasCastleKey;
boolean gameEnded;
}

public class CastleGame {

public static void main(String[] args) throws IOException{
    preGameMenu();

    return;
}

// This is the menu that is shown before the game starts
public static void preGameMenu() throws IOException {

    Scanner input = new Scanner(System.in);

    boolean running = true;

    while(running) {
        System.out.println("\t ESCAPE THE CASTLE\n");
        System.out.println("1) New Game\n2) Load Game\n3) Instructions\n4) Exit");

        boolean done = false;
         while( !done) { //Exception handling
             done = true;

             System.out.print(": ");
             String userOption = input.nextLine();
             clearPage();

             if (userOption.equals("1")) {
                 newGame();
             }
             else if (userOption.equals("2")) {
                 loadGame();
             }

             else if (userOption.equals("3")) {
                 displayInstructions();
             }
             else if (userOption.equals("4")) {
                 running = false;
             }
             else {
                 System.out.println("Invalid input entered");
                 done = false;
             }
         }
    }
     return;
}

//new game create new records of the map and player
public static void newGame() throws IOException {
    Map map = createMap();
    Player p = createPlayer();
    p.name = askName();

    runGame(map, p);
}

// if user decides to laod up a previosu saved file, then it will be loaded up
/*
 * FORMAT TO SAVE
 * integer showing how many rooms are there in the castle
 * - that many rows of the number of items in reach room
 * - that many rows of the number of traps in each room
 * - that many rows to tell if player bought scanner for that room
 * player name
 * player coins
 * which room the player was in when he saved the game
 * whether or not he has the castle key
 */
public static void loadGame() throws IOException {

    Map map = createMap();
    Player p = createPlayer();


    BufferedReader inputStream = new BufferedReader (new FileReader("savegame.txt"));


    int numberOfRooms = Integer.parseInt(inputStream.readLine());

    for(int i = 0; i <numberOfRooms; i++)
    {
        int line = Integer.parseInt(inputStream.readLine());
        map.itemsPerRoom[i] = line;
    }
    for(int i = 0; i <numberOfRooms; i++)
    {
        int line = Integer.parseInt(inputStream.readLine());
        map.trapsPerRoom[i] = line;
    }
    for(int i = 0; i <numberOfRooms; i++)
    {
        int line = Integer.parseInt(inputStream.readLine());
        map.scannerBoughtPerRoom[i] = line;
    }

    p.name = inputStream.readLine();
    p.coins = Integer.parseInt(inputStream.readLine());
    p.positionOnMap = Integer.parseInt(inputStream.readLine());
    boolean hasCastleKey = Boolean.parseBoolean(inputStream.readLine());
    p.hasCastleKey = hasCastleKey;

    inputStream.close();
    runGame(map, p);
}
public static void saveGame(Player p, Map map) throws IOException {


    String fileName = "savegame.txt";

    PrintWriter file = new PrintWriter (new FileWriter(fileName));
    int numberOfRooms = map.castleLayout.length;
    file.println(numberOfRooms);

    for (int i = 0; i <numberOfRooms; i++) {
        file.println(map.itemsPerRoom[i]);
    }
    for (int i = 0; i <numberOfRooms; i++) {
        file.println(map.trapsPerRoom[i]);
    }
    for (int i = 0; i <numberOfRooms; i++) {
        file.println(map.scannerBoughtPerRoom[i]);
    }
    file.println(p.name);
    file.println(p.coins);
    file.println(p.positionOnMap);
    file.println(p.hasCastleKey);

    file.close();

    return;

}

//a small method to just clear the page so it is more user friendly
public static void clearPage() {
    System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
}

// method to display the aim of the game
public static void displayInstructions() throws IOException {
    System.out.println("\n\n");
    System.out.println("\tESCAPE THE CASTLE");
    System.out.println("Aim of the game:");
    System.out.println("-You are stuck in a castle and need to escape");
    System.out.println("-There are multiple rooms with many items to collect each");
    System.out.println("\tworth random amount of coins");
    System.out.println("-However there are an unknown number of traps in each room");
    System.out.println("\tand some of the items have traps on them");
    System.out.println("-Trying to take an item that has a trap will take some of your coins");
    System.out.println("");
    System.out.println("-You can spend your coins on perks:");
    System.out.println("-Disable - can disable a trap in one of the castles rooms"); 
    System.out.println("\t(you will lose this perk even if you spend it on a room with no traps)");
    System.out.println("-Scanner - reveals the number of traps in a room ");
    System.out.println("");
    System.out.println("-Once 30 coins have been collected you can purchase the key to escape the castle from the main gate");
    System.out.println("-You will lose if a trap leaves you with 0 coins left");
    System.out.println("Good luck!");
    System.out.println();

    return;

}

// asks name from user and returns result
public static String askName() {
    Scanner input = new Scanner(System.in);

    System.out.print("What is your character name? : ");
    String name = input.nextLine();

    return name;

}

// function which randomly generate a number between 1-6
public static int rollDice() {
    double r = Math.random();
    int dicethrow = (int) (r * 6 + 1);

    return dicethrow;
}


// creates the map for a new game
public static Map createMap() {

    Map map = new Map();
    map.maxNumberOfItemsPerRoom = 10;
    map.maxNumberOfTrapsPerRoom = 3;
    map.pointsTakenFromTrap = 7;
    map.costToDisableTrap = 8;
    map.costOfScanner = 8;
    map.costOfCastleKey = 30;
    map.roomNumToExit = 7;
    map.castleLayout = new String[]{"Kitchen",  "Dining Hall","Library",
                                    "Storeroom", "Hallway", "Lounge",
                                    "Bedroom", "Main Gate", "Bathroom"};
    map.itemsPerRoom = generateItemsInMap(map);
    map.trapsPerRoom = generateTrapsInMap(map);
    map.scannerBoughtPerRoom = generateScannerInMap(map);

    return map;
}


public static int[] generateItemsInMap(Map map) {

    Random r = new Random();
    int[] itemsPerRoom = new int[map.castleLayout.length];
    for (int i = 0 ; i<map.castleLayout.length; i++) {

        int numberOfItems = r.nextInt(map.maxNumberOfItemsPerRoom-map.maxNumberOfTrapsPerRoom) + map.maxNumberOfTrapsPerRoom; //random number between 10 -3;
        itemsPerRoom[i] = numberOfItems;
    }
    return itemsPerRoom;

}


public static int[] generateTrapsInMap(Map map) {

    Random r = new Random();
    int[] trapsPerRoom = new int[map.castleLayout.length];
    for (int i = 0 ; i<map.castleLayout.length; i++) {
        int numberOfItems = r.nextInt(map.maxNumberOfTrapsPerRoom+1); //generates a maximum of 3 traps per room
        trapsPerRoom[i] = numberOfItems;
    }
    return trapsPerRoom;

}

public static int[] generateScannerInMap(Map map) {

    int[] scannerPerRoom = new int[map.castleLayout.length];

    return scannerPerRoom;
}

// creates a new player for a new game
public static Player createPlayer() {
     Player p = new Player();
     p.coins = 10;
     p.positionOnMap = 7;
     p.hasCastleKey = false;
     p.gameEnded = false;

     return p;

}

// function to check whether the player can purchase an item
public static boolean canBuyPerk(Player p, int cost) {

    boolean canBuy = true;
    if(p.coins - cost < 0 ) {
        canBuy = false;
    }

    return canBuy;
}

// checks whether the player has lost the game or reaches minus coins
public static void checkIfPlayerLost(Player p) {

    if(p.coins<= 0 ) {
        gameOver(false, p);
        p.coins = 0;
    }
    return;
}


// Displays the stats of the player
/*
 *  - Room which they are in
 *  - the amount of coins the player has
 *  - objects in that room
 *  - number of traps (can be unknown if scanner isnt bought)
 */
public static void displayInfo(Player p, Map map) {
    System.out.println("\n");
    System.out.println("\t" + map.castleLayout[p.positionOnMap].toUpperCase());
    System.out.println("Points: " + p.coins);
    System.out.println("Objects left: " + map.itemsPerRoom[p.positionOnMap]);

    if (map.scannerBoughtPerRoom[p.positionOnMap] ==1) {
        System.out.println("Traps in this room: " + map.trapsPerRoom[p.positionOnMap] );
    }
    else {
        System.out.println("Number of traps is unknown in the " + map.castleLayout[p.positionOnMap]);
    }

}





// gives user options to what they want to do in the game

public static void askMainActions(Player p , Map map) throws IOException {
    Scanner input = new Scanner(System.in);
    displayInfo(p, map);
    System.out.println("\n\nOPTIONS:\n"
            + "1) Action\n"
            + "2) Change Room\n"
            + "3) Escape the Castle\n"
            + "4) Save Game and Exit");

    String user_option = "";

    boolean done = false;
    while (!done) { // this handles invalid input
        done = true;
        System.out.print(": ");

        user_option =  input.nextLine();
        clearPage();


        if (user_option.equals("1")) {
            askActions(p , map);
        }
        else if (user_option.equals("2")) {
            changeRoomAction(p, map);

        }
        else if (user_option.equals("3")) {
            escapeCastle(p, map);

        }
        else if (user_option.equals("4")) {
            saveGame(p, map);
            p.gameEnded = true;
        }

        else {
            done = false;   
            System.out.println("Invalid action entered (click enter)");
            input.nextLine();
            clearPage();
        }
    }
    return;

}


// this is the second menu slide if the user decides to pick Actions in the main slide
// and is introduced to the actions they can take
public static void askActions(Player p , Map map) {
    Scanner input = new Scanner(System.in);
    boolean stayOnThisMenu = true;
    while (stayOnThisMenu) {
        displayInfo(p, map);
        System.out.println("\n\nSEARCH ROOM ACTION:\n"
                + "1) Take Object\n"
                + "2) Disable Trap ("+ map.costToDisableTrap+" coins)\n"
                + "3) Buy Scanner ("+ map.costOfScanner+" coins)\n"
                + "4) Purchase Key ("+ map.costOfCastleKey+" coins)\n"
                + "5) Back");

        String user_option = "";
        boolean done = false;
        while (!done) { // this handles invalid input
            done = true;
            System.out.print(": ");

            user_option =  input.nextLine();
            clearPage();


            if (user_option.equals("1")) {
                takeObject(p, map);
                // if the game has ended it should terminate the whole game without this check, 
                //the user will be able to carry on playing if they still stay on this menu slide
                if (p.gameEnded) { 
                    stayOnThisMenu = false;
                }
            }
            else if (user_option.equals("2")) {
                disableTrap(p, map);

            }
            else if (user_option.equals("3")) {
                buyScanner(p, map);

            }
            else if (user_option.equals("4")) {
                purchaseKey(p, map);


            }
            else if (user_option.equals("5")) {
                stayOnThisMenu = false; // go back on the menu

            }

            else {
                done = false;   
                System.out.println("Invalid action entered (click enter)");
                input.nextLine();
                clearPage();
            }
        }

    }
    return;

}



// function that is called when player decides to take an object from the room
public static void takeObject(Player p, Map map) {


    final String[] objects = {"goblet","talisman","golden ring","emerald necklace"}; // a set of random objects the player could take from the room


    Random r = new Random();

    int indexObject = r.nextInt(objects.length);

    if (map.itemsPerRoom[p.positionOnMap]>0) {

        // the chance is calculated as each object can picked with equal chance
        // the more objects the more chance to pick a non trap item
        // the less objects the chances of it being a trap also increases

        int chanceOfTrap = r.nextInt(map.itemsPerRoom[p.positionOnMap]);  

        if (chanceOfTrap >= map.trapsPerRoom[p.positionOnMap]) {
            int pointswon = rollDice();
            System.out.println("Nice one! You found a " + objects[indexObject] + ". You won " + pointswon + " coin(s)");
            map.itemsPerRoom[p.positionOnMap] -= 1; // minus one item on this room
            p.coins += pointswon;
        }
        else {

            System.out.println("You were caught in the trap and the " + objects[indexObject] + " was destroyed. -"+ map.pointsTakenFromTrap + " coins");
            map.itemsPerRoom[p.positionOnMap] -= 1;
            map.trapsPerRoom[p.positionOnMap] -= 1;
            p.coins -= map.pointsTakenFromTrap;
            checkIfPlayerLost(p);
            return;
        }
    }
    else {
        System.out.println("There are no more items to collect in this room!");
    }


}
// method which is called when player decide to disable a trap
public static void disableTrap( Player p, Map map) {

    if (canBuyPerk(p, map.costToDisableTrap)) {
        System.out.println("A trap has been disabled");
        System.out.println(map.costToDisableTrap + " coins have been spent!");

        map.trapsPerRoom[p.positionOnMap] -= 1;
        p.coins -= map.costToDisableTrap;
    }
    else {
        System.out.println("You do not have enough coins to disable a trap!");
    }




}

// user can buy scanner to reveal how many traps are in the room
public static void buyScanner(Player p, Map map) {

    if (canBuyPerk(p, map.costOfScanner)) {
        if (map.scannerBoughtPerRoom[p.positionOnMap] == 1) {
            System.out.println("Scanner was already bought for this room");

        }
        else {
            System.out.println("Scanner was bought. " + map.costOfScanner + " coins were spent!");
            map.scannerBoughtPerRoom[p.positionOnMap] = 1;
            p.coins -= map.costOfScanner;
        }
    }

    else {
        System.out.println("You do not have enough coins to disable a trap!");
    }



}

// if player has enough money they can purchase the key and exit through the main gate
public static void purchaseKey(Player p, Map map) {
    if (canBuyPerk(p, map.costOfCastleKey)) {
        System.out.println("You have purchased the Castle Key\nHead to the "+ map.castleLayout[map.roomNumToExit]);
        p.coins -= map.costOfCastleKey;
        p.hasCastleKey = true;
    }
    else {
        System.out.println("You do not have enough coins to purchase the Castle Key");
    }

}

// gives player option to move to different rooms
public static void changeRoomAction(Player p, Map map) {

    System.out.println("\n\nWhich room do you want to move to?\n:");

    for (int i = 0; i < map.castleLayout.length; i++) {
        System.out.println(i+1+ ") " + map.castleLayout[i]);
    }
    System.out.print(map.castleLayout.length+1 + ") Back\n:");

    Scanner input = new Scanner(System.in);

    boolean done = false;

    while (!done) { // handling exceptions

        int user_choice = input.nextInt();
        clearPage();

        if (user_choice < 1 || user_choice > map.castleLayout.length + 1) { //handling exceptions +1 for the back menu
            System.out.println("Invalid action entered (click enter)");
            input.nextLine();
            clearPage();
        }
        else if (user_choice == map.castleLayout.length +1){

            done = true;

        }
        else {
            p.positionOnMap = user_choice-1;

            done = true;
        }

    }

}
// method which gives user option to escape if they meet the requirments, must have key and must be at the main gate to open the door
public static void escapeCastle(Player p, Map map) {

    if (!p.hasCastleKey) {
        System.out.println("You need to purchase the Caslte Key (" + map.costOfCastleKey +" coins)");

    }
    else if (p.positionOnMap != map.roomNumToExit) {
        System.out.println("Make your way to the "+ map.castleLayout[map.roomNumToExit]);
    }
    else {
        gameOver(true,p);
    }
    return;
}

// method called when the game is over either loss or victory
public static void gameOver(boolean victory,Player p) {
    if (victory) {
        System.out.println("Congratulations " + p.name + "!\nYou have escaped the Castle");
        p.gameEnded = true;

    }
    else {

        System.out.println( p.name + ", you have been trapped in the castle");
        p.gameEnded = true;
    }

}

// this initiates the game
public static void runGame(Map map , Player p) throws IOException {


    while (!p.gameEnded){

        askMainActions(p, map);

        }

    return;
}
}