class BankAccount implements Measurable {
    private int accNumber;
    private double balance;
    private static int lastAssignedNumber = 1000;

    public BankAccount() {
        this(0);
    }

    public BankAccount(double balance) {
        this.balance = balance;
        accNumber = ++lastAssignedNumber;
    }

    public int getAccountNumber() {
        return accNumber;
    }

    public double getMeasure() {
        return balance;
    }

    public String getName() {
        return "Account " + accNumber;
    }
}
