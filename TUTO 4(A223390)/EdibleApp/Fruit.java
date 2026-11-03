class Fruit implements Edible {
    protected String name;

    public Fruit(String name) {
        this.name = name;
    }

    public String howToEat() {
        return name + ". " + name + " can be eaten fresh.";
    }
}