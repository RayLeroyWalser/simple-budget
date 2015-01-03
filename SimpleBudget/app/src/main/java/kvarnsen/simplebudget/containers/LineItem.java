package kvarnsen.simplebudget.containers;

/**
 * Created by joshuapancho on 3/01/15.
 */

public class LineItem {

    public int id, budgeted, spent, remaining;
    public String name;

    public LineItem(int id, String name, int budgeted, int spent, int remaining) {

        this.id = id;
        this.name = name;
        this.budgeted = budgeted;
        this.spent = spent;
        this.remaining = remaining;

    }

}
