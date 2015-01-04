package kvarnsen.simplebudget.containers;

/**
 * Created by joshuapancho on 4/01/15.
 */

public class ItemHistory {

    public String name;
    public String date;
    public int amount;

    public ItemHistory(String name, String date, int amount) {
        this.name = name;
        this.date = date;
        this.amount = amount;
    }

}
