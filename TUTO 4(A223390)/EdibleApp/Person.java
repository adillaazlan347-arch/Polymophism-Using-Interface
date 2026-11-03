class Person {
    private String name;

    public Person(String name) {
        this.name = name;
    }

    public String eating(Edible eat) {
        return name + " is eating " + eat.howToEat();
    }
}