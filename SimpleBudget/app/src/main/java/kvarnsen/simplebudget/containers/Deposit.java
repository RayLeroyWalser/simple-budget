package kvarnsen.simplebudget.containers;

/**
 * Created by joshuapancho on 17/01/15.
 */

public class Deposit {

    private String name;
    private String date;
    private int amount;

    public Deposit(String name, String date, int amount) {
        this.name = name;
        this.date = date;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public int getAmount() {
        return amount;
    }

}
