class Car implements Price {
    private String brand;
    private double priceCar;
    private boolean buy;

    public Car(String brand, double priceCar) {
        this.brand = brand;
        this.priceCar = priceCar;
    }

    public String getBrand() {
        return brand;
    }

    public String toString() {
        return "The car brand is " + brand + " with price RM" + priceCar + ".";
    }

    public void getPrice(Customer c) {
        buy = c.getSalary() > 8000;
        System.out.println(this);
        System.out.print("The customer's name is " + c.getName()
                + " with salary RM" + c.getSalary());
        if (buy) {
            System.out.println(" can buy this car. Your salary is higher than the price's car.");
        } else {
            System.out.println(" cannot buy this car. The car price is more than your salary.");
        }
    }
}