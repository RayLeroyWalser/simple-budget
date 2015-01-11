package kvarnsen.simplebudget.containers;

/**
 * Created by joshuapancho on 4/01/15.
 */

/*
    Holds info about an expense for a line item. Used by ItemHistoryAdapter
 */
public class Expense {

    public String name;
    public String date;
    public int amount;

    public Expense(String name, String date, int amount) {
        this.name = name;
        this.date = date;
        this.amount = amount;
    }

}
