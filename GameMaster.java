import java.text.DecimalFormat;
import java.util.Collections;
import java.util.ArrayList;

public class GameMaster {
    static ArrayList<Player> Players = new ArrayList<>();
    static ArrayList<Territory> Territories = new ArrayList<>();
    // Results of a given simulation, format is:
    // 0: Empty for the sake of consistency
    // 1: Number of winners who take North America first
    // 2: Number of winners who take South America first
    // 3: Number of winners who take Europe first
    // 4: Number of winners who take Africa first
    // 5: Number of winners who take Asia first
    // 6: Number of winners who take Australia first
    static int[] Results = new int[7];

    public static void main(String[] args) {
        initializePlayers();
        initializeTerritories();
        distributeTerritoriesToPlayers();
        distributeInitialTroops();

        int numOfSimulations = 500;
        runRiskSimulation(numOfSimulations);

        printResults(numOfSimulations);

    }

    private static void printResults(int numOfSimulations) {
        // Percentages
        DecimalFormat df = new DecimalFormat("00.00");

        String percentNA = df.format((Math.round(((double) (Results[1]) / numOfSimulations * 10000))/ 100.0));
        String percentSA = df.format((Math.round(((double) (Results[2]) / numOfSimulations * 10000))/ 100.0));
        String percentEU = df.format((Math.round(((double) (Results[3]) / numOfSimulations * 10000))/ 100.0));
        String percentAF = df.format((Math.round(((double) (Results[4]) / numOfSimulations * 10000))/ 100.0));
        String percentAS = df.format((Math.round(((double) (Results[5]) / numOfSimulations * 10000))/ 100.0));
        String percentAU = df.format((Math.round(((double) (Results[6]) / numOfSimulations * 10000))/ 100.0));

        System.out.println("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓\n" +
                "┃                                ┏━━━━━━━┓                                                                                                         ┃\n" +
                "┃                                ┗┓      ┃                                                                                                         ┃\n" +
                "┃                                 ┃     ┏┛                                                                      ┏━━━━━━━┓                          ┃\n" +
                "┃    ┏━━━━━━━━━━━━━━━━━┓          ┗━━━━━┛    ┏━━┓                                             ┏━━━━━━━━━━━━━━━━━┛       ┗━━━━━━━━━━━━━━━┓          ┃\n" +
                "┃    ┃                 ┃                     ┃  ┃                                        ┏━━━━┫                                        ┏┛          ┃\n" +
                "┃    ┃                 ┃ ┏━━━━┓              ┗━━┛    ┏━━┓       ┏━━━━┓          ┏━━━━━━━━┛   ┏┛                                       ┏┛           ┃\n" +
                "┃    ┗━━┓              ┗━┛    ┃                      ┃  ┃    ┏━━┛┏━┓ ┗┓    ┏━━━━┛           ┏┛                 Asia                  ┏┛            ┃\n" +
                "┃       ┃    North            ┃                      ┗┓ ┃    ┗━━━┛ ┃  ┃ ┏━━┛               ┏┛                  "+percentAS+"%                ┃             ┃\n" +
                "┃       ┃    America      ┏━━━┛                       ┗━┛┏━━━━━━━━━┛  ┗━┛                  ┃                                         ┗━┓           ┃\n" +
                "┃       ┃    "+percentNA+"%      ┏┛                          ┏━━━┛      Europe        ┏━━━━━━┓     ┃                                           ┃           ┃\n" +
                "┃       ┃                ┃                           ┃          "+percentEU+"%   ┏━━━━┛      ┣━━━━━┛                                          ┏┛           ┃\n" +
                "┃       ┃                ┃                           ┃  ┏━━━━┓ ┏━━━━┓┏━━━┛        ┏━━┛                                               ┏┛            ┃\n" +
                "┃       ┗┓              ┏┛                           ┗━━┛    ┗┓┗┓   ┃┃   ┏━━━━━━━━┛                                               ┏━━┛             ┃\n" +
                "┃        ┗━┓           ┏┛                                     ┗━┛   ┗┛   ┗━━━━━━━┓           ┏━━━━━━━┓                         ┏━━┛                ┃\n" +
                "┃          ┗━━┓  ┏━━━━━┛                                                         ┃           ┃       ┗━┓                       ┗━━┓                ┃\n" +
                "┃             ┗┓ ┃                                   ┏━━━━━━━━━┓                 ┃           ┃         ┗━━┓                       ┗━┓              ┃\n" +
                "┃              ┗━┗━┓                               ┏━┛         ┗━━━━┓            ┗┓        ┏━┛            ┗━━┓               ┏━━━━━━┛              ┃\n" +
                "┃                 ┏┻━━━━━━━━┓                    ┏━┛                ┗━━┓          ┗┓      ┏┛                 ┗━┓           ┏━┛                     ┃\n" +
                "┃                ┏┛         ┗━┓                 ┏┛                     ┃           ┃      ┃                    ┃         ┏━┛                       ┃\n" +
                "┃               ┏┛            ┗━━━┓             ┃        Africa        ┃           ┗┓    ┏┛                    ┗━┓       ┃                         ┃\n" +
                "┃              ┏┛    South        ┃             ┃        "+percentAF+"%        ┃            ┗━━━━┛                       ┗━┓    ┏┛                         ┃\n" +
                "┃             ┏┛     America      ┃             ┃                     ┏┛                                           ┗━┓ ┏┛                 ┏━┓      ┃\n" +
                "┃             ┃      "+percentSA+"%       ┃             ┗━━┓                  ┃                                              ┗━┛                 ┏┛ ┃      ┃\n" +
                "┃             ┗━┓               ┏━┛                ┗━━━━━━┓          ┏┛                                                          ┏━━━━┓  ┃  ┗┓     ┃\n" +
                "┃               ┗━┓           ┏━┛                         ┃         ┏┛                                                         ┏━┛    ┗━━┛   ┗━┓   ┃\n" +
                "┃                 ┗┓         ┏┛                           ┃         ┃                                                        ┏━┛   Australia   ┃   ┃\n" +
                "┃                  ┃        ┏┛                            ┗┓        ┃   ┏━━┓                                                 ┃      "+percentAU+"%    ┏┛   ┃\n" +
                "┃                  ┗┓      ┏┛                              ┃        ┃   ┃  ┃                                                 ┗━━┓     ┏┓     ┏┛    ┃\n" +
                "┃                   ┗┓    ┏┛                               ┃        ┃   ┗┓ ┃                                                    ┗━━━━━┛┗━┓   ┃     ┃\n" +
                "┃                    ┗┓  ┏┛                                ┗┓      ┏┛    ┗━┛                                                             ┗━━━┛     ┃\n" +
                "┃                     ┗━━┛                                  ┗━━━━━━┛                                                                               ┃\n" +
                "┃                                                                                                                                                  ┃\n" +
                "┃                                                                                                                                                  ┃\n" +
                "┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");

    }

    public static void runRiskSimulation(int numOfSimulations) {
        // Run a selected number of simulations and enumerate the Results array elements depending on the continent taken first of the victor
        for (int i = 0; i < numOfSimulations; i++) {
            boolean isGameOver = false;
            while (!isGameOver) {
                for (Player player : Players) {
                    // DEBUG
                    System.out.println("It is now " + player + "'s turn");
                    player.addNewTroops();
                    player.updateBorderingTerritories();
                    player.combat();
                    player.redistribute();
                    // Check if game is over (all territories are owned by one player)
                    if (player.ownedTerritories.size() == 42) {
                        isGameOver = true;
                        switch (player.getFirstContinentTaken()) {
                            case 1:
                                Results[1]++;
                                break;
                            case 2:
                                Results[2]++;
                                break;
                            case 3:
                                Results[3]++;
                                break;
                            case 4:
                                Results[4]++;
                                break;
                            case 5:
                                Results[5]++;
                                break;
                            case 6:
                                Results[6]++;
                                break;
                        }
                        // Kinda DEBUG
                        System.out.println("The winner is Player" + player.getIdentifier());
                        break;
                    }
                }
            }
            Players.clear();
            Territories.clear();
            initializePlayers();
            initializeTerritories();
            distributeTerritoriesToPlayers();
            distributeInitialTroops();
        }
    }

    public static void distributeTerritoriesToPlayers() {
        // Randomly order the Territories ArrayList
        Collections.shuffle(Territories);

        // Because 42 territories divided by 4 doesn't go even, Player1 and Player2 will each have an extra territory
        Players.get(0).setOwnedTerritories(createSubArrayList(Territories, 0, 11));
        Players.get(1).setOwnedTerritories(createSubArrayList(Territories, 11, 22));
        Players.get(2).setOwnedTerritories(createSubArrayList(Territories, 22, 32));
        Players.get(3).setOwnedTerritories(createSubArrayList(Territories, 32, 42));

    }

    public static void distributeInitialTroops() {
        // "30 armies each if four players" rules taken from https://risk.fandom.com/wiki/Risk
        for (Player player : Players) {
            player.setNumOfArmies(30);
        }

        // Spread out the armies to every territory the player owns
        for (Player player : Players) {
            // Fill every territory a player owns with troops
            for (int i = 0; player.getNumArmies() > 0; i++) {
                player.ownedTerritories.get(i % player.ownedTerritories.size()).addOneArmy();
                player.removeOneArmy();
            }
        }
    }

    public static void initializePlayers() {
        Player Player1 = new Player(1);
        Players.add(Player1);
        Player Player2 = new Player(2);
        Players.add(Player2);
        Player Player3 = new Player(3);
        Players.add(Player3);
        Player Player4 = new Player(4);
        Players.add(Player4);
    }

    public static void initializeTerritories() {
        Territory Territory1 = new Territory(1, 1, "Alaska", borderedTerritories(2, 4, 30, 0, 0, 0));
        Territory Territory2 = new Territory(1, 2, "Northwest Territory", borderedTerritories(1, 3, 4, 5, 0, 0));
        Territory Territory3 = new Territory(1, 3, "Greenland", borderedTerritories(2, 5, 6, 14, 0, 0));
        Territory Territory4 = new Territory(1, 4, "Alberta", borderedTerritories(1, 2, 5, 7, 0, 0));
        Territory Territory5 = new Territory(1, 5, "Ontario", borderedTerritories(2, 3, 4, 6, 7, 8));
        Territory Territory6 = new Territory(1, 6, "Quebec", borderedTerritories(3, 5, 8, 0, 0, 0));
        Territory Territory7 = new Territory(1, 7, "Western United States", borderedTerritories(4, 5, 8, 9, 0, 0));
        Territory Territory8 = new Territory(1, 8, "Eastern United States", borderedTerritories(5, 6, 7, 9, 0, 0));
        Territory Territory9 = new Territory(1, 9, "Central America", borderedTerritories(7, 8, 10, 0, 0, 0));
        Territory Territory10 = new Territory(2, 10, "Venezuela", borderedTerritories(9, 11, 12, 0, 0, 0));
        Territory Territory11 = new Territory(2, 11, "Peru", borderedTerritories(10, 12, 13, 0, 0, 0));
        Territory Territory12 = new Territory(2, 12, "Brazil", borderedTerritories(10, 11, 13, 21, 0, 0));
        Territory Territory13 = new Territory(2, 13, "Argentina", borderedTerritories(11, 12, 0, 0, 0, 0));
        Territory Territory14 = new Territory(3, 14, "Iceland", borderedTerritories(3, 15, 17, 0, 0, 0));
        Territory Territory15 = new Territory(3, 15, "Scandinavia", borderedTerritories(14, 16, 17, 18, 0, 0));
        Territory Territory16 = new Territory(3, 16, "Eastern Europe", borderedTerritories(15, 18, 20, 27, 32, 35));
        Territory Territory17 = new Territory(3, 17, "Great Britain", borderedTerritories(14, 15, 18, 19, 0, 0));
        Territory Territory18 = new Territory(3, 18, "Northern Europe", borderedTerritories(15, 16, 17, 19, 20, 0));
        Territory Territory19 = new Territory(3, 19, "Western Europe", borderedTerritories(17, 18, 20, 21, 0, 0));
        Territory Territory20 = new Territory(3, 20, "Southern Europe", borderedTerritories(16, 18, 19, 21, 22, 35));
        Territory Territory21 = new Territory(4, 21, "North Africa", borderedTerritories(12, 19, 20, 22, 24, 23));
        Territory Territory22 = new Territory(4, 22, "Egypt", borderedTerritories(20, 21, 24, 35, 0, 0));
        Territory Territory23 = new Territory(4, 23, "Congo", borderedTerritories(21, 24, 25, 0, 0, 0));
        Territory Territory24 = new Territory(4, 24, "East Africa", borderedTerritories(21, 22, 23, 25, 26, 35));
        Territory Territory25 = new Territory(4, 25, "South Africa", borderedTerritories(23, 24, 26, 0, 0, 0));
        Territory Territory26 = new Territory(4, 26, "Madagascar", borderedTerritories(24, 25, 0, 0, 0, 0));
        Territory Territory27 = new Territory(5, 27, "Ural", borderedTerritories(16, 28, 32, 37, 0, 0));
        Territory Territory28 = new Territory(5, 28, "Siberia", borderedTerritories(27, 29, 31, 33, 37, 0));
        Territory Territory29 = new Territory(5, 29, "Yakutsk", borderedTerritories(28, 30, 31, 0, 0, 0));
        Territory Territory30 = new Territory(5, 30, "Kamchatka", borderedTerritories(1, 29, 31, 33, 34, 0));
        Territory Territory31 = new Territory(5, 31, "Irkutsk", borderedTerritories(28, 29, 30, 33, 0, 0));
        Territory Territory32 = new Territory(5, 32, "Afghanistan", borderedTerritories(16, 27, 35, 36, 37, 0));
        Territory Territory33 = new Territory(5, 33, "Mongolia", borderedTerritories(28, 30, 32, 34, 37, 0));
        Territory Territory34 = new Territory(5, 34, "Japan", borderedTerritories(33, 30, 0, 0, 0, 0));
        Territory Territory35 = new Territory(5, 35, "Middle East", borderedTerritories(16, 22, 32, 36, 0, 0));
        Territory Territory36 = new Territory(5, 36, "India", borderedTerritories(32, 35, 37, 38, 0, 0));
        Territory Territory37 = new Territory(5, 37, "China", borderedTerritories(32, 33, 36, 38, 0, 0));
        Territory Territory38 = new Territory(5, 38, "Southeast Asia", borderedTerritories(36, 37, 39, 0, 0, 0));
        Territory Territory39 = new Territory(6, 39, "Indonesia", borderedTerritories(38, 40, 41, 0, 0, 0));
        Territory Territory40 = new Territory(6, 40, "New Guinea", borderedTerritories(39, 41, 42, 0, 0, 0));
        Territory Territory41 = new Territory(6, 41, "Western Australia", borderedTerritories(39, 40, 42, 0, 0, 0));
        Territory Territory42 = new Territory(6, 42, "Eastern Australia", borderedTerritories(40, 41, 0, 0, 0, 0));
        Territories.add(Territory1);
        Territories.add(Territory2);
        Territories.add(Territory3);
        Territories.add(Territory4);
        Territories.add(Territory5);
        Territories.add(Territory6);
        Territories.add(Territory7);
        Territories.add(Territory8);
        Territories.add(Territory9);
        Territories.add(Territory10);
        Territories.add(Territory11);
        Territories.add(Territory12);
        Territories.add(Territory13);
        Territories.add(Territory14);
        Territories.add(Territory15);
        Territories.add(Territory16);
        Territories.add(Territory17);
        Territories.add(Territory18);
        Territories.add(Territory19);
        Territories.add(Territory20);
        Territories.add(Territory21);
        Territories.add(Territory22);
        Territories.add(Territory23);
        Territories.add(Territory24);
        Territories.add(Territory25);
        Territories.add(Territory26);
        Territories.add(Territory27);
        Territories.add(Territory28);
        Territories.add(Territory29);
        Territories.add(Territory30);
        Territories.add(Territory31);
        Territories.add(Territory32);
        Territories.add(Territory33);
        Territories.add(Territory34);
        Territories.add(Territory35);
        Territories.add(Territory36);
        Territories.add(Territory37);
        Territories.add(Territory38);
        Territories.add(Territory39);
        Territories.add(Territory40);
        Territories.add(Territory41);
        Territories.add(Territory42);
    }

    public static ArrayList<Territory> createSubArrayList(ArrayList<Territory> startingArrayList, int fromIndex, int toIndex) {
        ArrayList<Territory> endArrayList = new ArrayList<>();
        for (int i = fromIndex; i < toIndex; i++) {
            endArrayList.add(startingArrayList.get(i));
        }
        return endArrayList;
    }

    public static int[] borderedTerritories(int num1, int num2, int num3, int num4, int num5, int num6){
        int[] list = {num1, num2, num3, num4, num5, num6};
        int size = 1;
        for (int i = 1; i < list.length; i++){
            if (list[i] != 0){
                size++;
            }
        }
        int[] newList = new int[size];
        int count = 0;
        for (int j : list) {
            if (j != 0) {
                newList[count++] = j;
            }
        }
        return newList;
    }
}
