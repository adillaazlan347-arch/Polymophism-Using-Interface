class Product implements Price {
    private String type;
    private boolean buy;

    public Product(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

  
    public String toString() {
        return "The product is ";
    }


    public void getPrice(Customer c) {
        buy = c.getSalary() > 4000;
        System.out.println(this);   // calls the subclass toString()
        System.out.print("The customer's name is " + c.getName()
                + " with salary RM" + c.getSalary());
        if (buy) {
            System.out.println(" can buy this product.");
            System.out.println("Your salary is higher than the price's product.");
        } else {
            System.out.println(" cannot buy this product.");
            System.out.println("The product price is more than your salary.");
        }
    }
}
