package kvarnsen.simplebudget.containers;

/**
 * Created by joshuapancho on 3/01/15.
 */

/*
    Holds info about an item.
 */
public class LineItem {

    private int id, budgeted, spent, remaining;
    private String name;

    public LineItem(int id, String name, int budgeted, int spent, int remaining) {

        this.id = id;
        this.name = name;
        this.budgeted = budgeted;
        this.spent = spent;
        this.remaining = remaining;

    }

    public int getId() {
        return id;
    }

    public int getBudget() {
        return budgeted;
    }

    public int getSpent() {
        return spent;
    }

    public int getRemaining() {
        return remaining;
    }

    public String getName() {
        return name;
    }

}
