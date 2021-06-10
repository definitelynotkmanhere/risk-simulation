public class Territory {
    private final int continent;
    private final int[] borderTerritories;
    private final int identifier;
    private final String name;
    private int owner;
    private int numArmies;

    public Territory(int continent, int identity, String name, int[] borderTerritories){
        this.continent = continent;
        this.identifier = identity;
        this.name = name;
        this.borderTerritories = borderTerritories;
        this.owner = 0;
    }
    // the toString method doubles as both a getter method for String name and a normal toString override
    public String toString() {
        return (name + " (" + numArmies + " armies)");
    }
    public void setOwner(int player){
        this.owner = player;
    }
    public int getIdentifier() {
        return identifier;
    }
    public int getContinent(){
        return this.continent;
    }
    public int getOwner() {
        return owner;
    }
    public int getNumArmies() {
        return numArmies;
    }
    public void setNumArmies(int numArmies) {
        this.numArmies = numArmies;
    }
    public void addOneArmy() {
        this.numArmies++;
    }
    public void addArmies(int armyAdd){
        this.numArmies = this.numArmies + armyAdd;
    }
    public int[] getBorderTerritories() {
        return borderTerritories;
    }
}