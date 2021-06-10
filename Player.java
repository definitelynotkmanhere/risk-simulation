import java.util.ArrayList;
import java.util.Random;

public class Player {
    private final int identifier;
    ArrayList<Territory> ownedTerritories = new ArrayList<>();
    ArrayList<Territory> borderingTerritoriesToAttack = new ArrayList<>();
    // -1 meaning no continent taken yet
    private int firstContinentTaken = -1;
    private int numOfArmies;
    private int numOfReinforcements;

    public Player(int identifier) {
        this.identifier = identifier;
    }

    public void updateBorderingTerritories() {
        // Clears last turn's bordering territories
        borderingTerritoriesToAttack.clear();

        // For every territory this player owns
        for (Territory territory : ownedTerritories) {
            // For every bordering territory of every territory this player owns
            for (int territoryNum : territory.getBorderTerritories()) {
                // Convert the current territoryNum to a Territory object
                Territory borderingTerritory = convertIdentifierToTerritory(territoryNum);
                if (borderingTerritory.getOwner() != identifier && borderingTerritory.getNumArmies() <= territory.getNumArmies()) {
                    borderingTerritoriesToAttack.add(borderingTerritory);
                }
            }
        }
    }

    public Territory convertIdentifierToTerritory(int identifier) {
        for (Territory territory : GameMaster.Territories) {
            if (territory.getIdentifier() == identifier) {
                return territory;
            }
        }
        // Should never get to this, but in case it does it will return null
        return null;
    }

    private void sortTerritoriesByMostArmies() {
        // Sort territories by num of armies
        // https://stackoverflow.com/questions/2784514/sort-arraylist-of-custom-objects-by-property

        // Don't really understand this, but it sorts ownedTerritories by the attribute largestArmies, so I'm happy
        // Returns -1 if t1 > t2
        // Returns 0 if t1 == t2
        // Returns 1 if t1 < t2

        ownedTerritories.sort((t1, t2) -> Integer.compare(t2.getNumArmies(), t1.getNumArmies()));
    }

    private void checkForEnemyTerritoriesInOwnedTerritories() {

        // This is intelliJ suggested code, I'm not sure how .removeIf works, but it does work.

        ownedTerritories.removeIf(territory -> territory.getOwner() != identifier);
    }

    public void combat() {
        checkForEnemyTerritoriesInOwnedTerritories();
        sortTerritoriesByMostArmies();

        // To avoid ConcurrentModificationException error (changing the ownedTerritories while the loop is running),
        // we add all territories won in battle to a temp array list to add onto ownedTerritories at the end.

        ArrayList<Territory> territoriesToAddToOwnedTerritories = new ArrayList<>();

        for (Territory territory : ownedTerritories) {
            // Look at bordering countries, and attack the one that has the least armies (and is still less than your territory's army size)
            int smallestNumOfArmies = territory.getNumArmies();
            Territory bestTerritoryToAttack = null;

            for (int territoryNum : territory.getBorderTerritories()) {
                Territory borderingTerritory = convertIdentifierToTerritory(territoryNum);
                if (borderingTerritory.getOwner() != this.identifier && borderingTerritory.getNumArmies() < smallestNumOfArmies) {
                    smallestNumOfArmies = borderingTerritory.getNumArmies();
                    bestTerritoryToAttack = borderingTerritory;
                }
            }
            // Now attack bestTerritoryToAttack
            if (bestTerritoryToAttack != null){
                // DEBUG/INDICATOR
                System.out.println("\nPlayer" + identifier + " is attacking " + bestTerritoryToAttack + " with " + territory);

                int[] resultOfCombat = combatLogic(bestTerritoryToAttack.getNumArmies(), territory.getNumArmies());

                // Copy and pasted from comments within the combatLogic method, but it is helpful to have here as well:
                // return array format: [winner, defense armies remaining, offense armies remaining]
                // winner is 1(attacker victory) or 0(defensive victory)

                if (resultOfCombat[0] == 1) {
                    // DEBUG/INDICATOR
                    System.out.println("The attacker (Player" + identifier + ") has won");

                    bestTerritoryToAttack.setOwner(identifier);
                    territoriesToAddToOwnedTerritories.add(bestTerritoryToAttack);

                    territory.setNumArmies(1);
                    bestTerritoryToAttack.setNumArmies(resultOfCombat[2] - 1);
                }
                else {
                    // DEBUG/INDICATOR
                    System.out.println("The defender (Player" + bestTerritoryToAttack.getOwner() + ") has won");

                    territory.setNumArmies(resultOfCombat[2]);
                    bestTerritoryToAttack.setNumArmies(resultOfCombat[1]);
                }
                // DEBUG/INDICATOR
                System.out.println(territory + " " + bestTerritoryToAttack);
            }
        }
        ownedTerritories.addAll(territoriesToAddToOwnedTerritories);
        territoriesToAddToOwnedTerritories.clear();
    }

    // To make play more realistic, players will evenly redistribute all their armies across all territories
    public void redistribute() {
        // DEBUG/INDICATOR
        System.out.println("Player" + identifier + " is now redistributing");

        int numOfTotalArmies = 0;

        for (Territory territory : ownedTerritories) {
            numOfTotalArmies += territory.getNumArmies();
            territory.setNumArmies(0);
        }

        for (int i = 0; i < numOfTotalArmies; i++) {
            ownedTerritories.get(i % ownedTerritories.size()).addOneArmy();
        }

        // DEBUG/INDICATOR
        System.out.println(ownedTerritories);
    }

    public void addNewTroops() {
        int continentBonus = calculateContinentBonus();
        // DEBUG/INDICATOR
        if (continentBonus != 0) {
            System.out.println("CONTINENT BONUS HAS BEEN ALLOTTED");
        }
        this.numOfReinforcements = (ownedTerritories.size() / 3) + continentBonus;
        distributeTroops();
    }

    public int calculateContinentBonus() {
        /*
        Checking to see if the player has:
        - Four Australian (Continent #6) Territories, if so receiving a bonus of 2 and/or
        - Twelve Asian (Continent #5) Territories, if so receiving a bonus of 7 and/or
        - Six African (Continent #4) Territories, if so receiving a bonus of 3 and/or
        - Seven European (Continent #3) Territories, if so receiving a bonus of 5 and/or
        - Four South American (Continent #2) Territories, if so receiving a bonus of 2 and/or
        - Nine North American (Continent #1) Territories, if so receiving a bonus of 5
         */
        int continentBonus = 0;

        int numOfNorthAmericanTerritories = 0;
        int numOfSouthAmericanTerritories = 0;
        int numOfEuropeanTerritories = 0;
        int numOfAfricanTerritories = 0;
        int numOfAsianTerritories = 0;
        int numOfAustralianTerritories = 0;

        for (Territory territory : ownedTerritories) {
            switch(territory.getContinent()) {
                case 1:
                    numOfNorthAmericanTerritories++;
                    break;
                case 2:
                    numOfSouthAmericanTerritories++;
                    break;
                case 3:
                    numOfEuropeanTerritories++;
                    break;
                case 4:
                    numOfAfricanTerritories++;
                    break;
                case 5:
                    numOfAsianTerritories++;
                    break;
                case 6:
                    numOfAustralianTerritories++;
                    break;
            }
        }
        if (numOfNorthAmericanTerritories == 9) {
            continentBonus += 5;
            if (firstContinentTaken == -1) {
                firstContinentTaken = 1;
            }
        }
        if (numOfSouthAmericanTerritories == 4) {
            continentBonus += 2;
            if (firstContinentTaken == -1) {
                firstContinentTaken = 2;
            }
        }
        if (numOfEuropeanTerritories == 7) {
            continentBonus += 5;
            if (firstContinentTaken == -1) {
                firstContinentTaken = 3;
            }
        }
        if (numOfAfricanTerritories == 6) {
            continentBonus += 3;
            if (firstContinentTaken == -1) {
                firstContinentTaken = 4;
            }
        }
        if (numOfAsianTerritories == 12) {
            continentBonus += 7;
            if (firstContinentTaken == -1) {
                firstContinentTaken = 5;
            }
        }
        if (numOfAustralianTerritories == 4) {
            if (firstContinentTaken == -1) {
                firstContinentTaken = 6;
            }
        }
        return continentBonus;
    }

    public void distributeTroops() {
        // Disperses troops evenly among all territories
        for (Territory territory : ownedTerritories) {
            if (numOfReinforcements > 0) {
                territory.addArmies(1);
                numOfReinforcements--;
            }
        }
    }

    public int getIdentifier() {
        return identifier;
    }

    public int getFirstContinentTaken() {
        return firstContinentTaken;
    }

    public void setOwnedTerritories(ArrayList<Territory> ownedTerritories) {
        for (Territory territory : ownedTerritories) {
            territory.setOwner(this.identifier);
        }
        this.ownedTerritories = ownedTerritories;
    }

    public void removeOneArmy() {
        this.numOfArmies--;
    }

    public int getNumArmies() {
        return this.numOfArmies;
    }
    public void setNumOfArmies(int numOfArmies) {
        this.numOfArmies = numOfArmies;
    }

    @Override
    public String toString() {
        return "Player" + identifier;
    }

    private int[] combatLogic(int defense, int offense) {
        //array for return statement
        int[] returnArray = new int[3];
        Random rand = new Random();
        int x = 1;
        ArrayList<Integer> offenseDice = new ArrayList<>();
        ArrayList<Integer> defenseDice = new ArrayList<>();
        //ensures combat loop
        while (x == 1) {
            //how many dice?
            int defenseDie = diceCounter(defense, true);
            int offenseDie = diceCounter(offense, false);
            //rolls the dice
            for (int i = 1; i <= defenseDie; i++) {
                Integer defenseRoll = rand.nextInt(6) + 1;
                defenseDice.add(defenseRoll);
            }
            for (int i = 1; i <= offenseDie; i++) {
                int offenseRoll = rand.nextInt(6) + 1;
                offenseDice.add(offenseRoll);
            }
            int offenseLeastDie = lowestDie(offenseDice);
            //if offense has 3 die, then removes lowest dice roll
            if (offenseDie == 3) {
                offenseDice.remove(offenseLeastDie);
            }
            //value of offense's best die
            int greatestOffenseVal = 0;
            //index of offense's best die in the array
            int offenseBestDieIndex = 0;

            for (int i = 0; i < offenseDice.size(); i++) {
                Integer die;
                die = offenseDice.get(i);
                //gets index of the greatest dice roll
                if (die >= greatestOffenseVal) {
                    greatestOffenseVal = die;
                    offenseBestDieIndex = i;
                }
            }

            //determines highest and lowest die for defense
            //Best value of defense's die
            int greatestDefenseVal = 0;
            //index of defense's best die
            int defenseBestDieIndex = 0;

            for (int i = 0; i < defenseDice.size(); i++) {
                Integer die;
                die = defenseDice.get(i);
                //gets index of the greatest dice roll
                if (die >= greatestDefenseVal) {
                    greatestDefenseVal = die;
                    defenseBestDieIndex = i;
                }

            }
            //compares two best dice
            int engagement1 = diceCompare(greatestOffenseVal, greatestDefenseVal);

            //vars for convenience
            int offenseSecondDieVal;
            int defenseSecondDieVal;
            if (defenseDice.size() == 2) {
                offenseSecondDieVal = offenseDice.get(offenseDice.size() - offenseBestDieIndex - 1);
                defenseSecondDieVal = defenseDice.get(defenseDice.size() - defenseBestDieIndex - 1);
            }
            else {
                offenseSecondDieVal = offenseDice.get(offenseDice.size() - offenseBestDieIndex - 1);
                defenseSecondDieVal = defenseDice.get(defenseBestDieIndex);
            }
            //compares two secondary dice
            int engagement2 = diceCompare(offenseSecondDieVal,defenseSecondDieVal);
            //checks to see who won each engagement and subtract armies accordingly
            if(engagement1 == greatestOffenseVal){
                defense = defense - 1;
            }
            if(engagement1 == greatestDefenseVal) {
                offense = offense - 1;
            }
            if(engagement2 == offenseSecondDieVal){
                defense = defense - 1;
            }
            if(engagement2 == defenseSecondDieVal){
                offense = offense - 1;
            }
            //clears arrays for next loop
            offenseDice.clear();
            defenseDice.clear();
            //designates total destruction, defensive victory

            if(defense == 0 && offense == 0){
                x = 0;
                defense = 0;
                returnArray[0] = 1; // sets attacker as winner
                //returns remaining defensive armies
                //returns remaining offensive armies
            }
            if(defense <= 0){
                x = 0;
                defense = 0;
                returnArray[0] = 1; // sets attacker as winner
                //returns remaining defensive armies
                returnArray[2] = offense; //returns remaining offensive armies
            }
            if(offense <= 1){
                offense = 1;
                x = 0;
                returnArray[0] = 0; // sets defense as winner
                returnArray[1] = defense; //returns remaining defensive armies
                returnArray[2] = offense; //returns remaining offensive armies
            }
        }
        //outside of while loop
        //return array format: [winner, defense armies remaining, offense armies remaining]
        //winner is 1(attacker victory) or 0(defensive victory)
        return returnArray;
    }

    private static int diceCompare(int die1, int die2){
        //indicates a tie
        if(die1 == die2){
            return 0;
        }
        return Math.max(die1, die2);
        //indicates an error
    }

    //determines how many dice an army group gets to roll
    private static int diceCounter(int armies, boolean defender) {
        //determines if the player is an attacker or defender
        if (defender) {
            if (armies >= 2) {
                return 2;
            }
            else {
                return 1;
            }
        }
        else {
            if (armies > 3) {
                return 3;
            } else if (armies > 2) {
                return 2;
            } else {
                return 1;
            }
        }
    }
    // returns -1 if diceList has no items
    private static int lowestDie(ArrayList<Integer> diceList) {
        int leastDieVal = 6;
        int worstDie = -1;

        for (int i = 0; i < diceList.size(); i++) {
            Integer die;
            die = diceList.get(i);
            //gets index of the greatest dice roll
            if (die <= leastDieVal) {
                leastDieVal = die;
                worstDie = i;
            }
        }
        return worstDie;
    }
}