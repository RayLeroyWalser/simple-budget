package kvarnsen.simplebudget.containers;

/**
 * Created by joshuapancho on 17/01/15.
 */

public class Goal {

    private String name;
    private int goal;
    private int deposited;

    public Goal(String name, int goal, int deposited) {
        this.name = name;
        this.goal = goal;
        this.deposited = deposited;
    }

    public String getName() {
        return name;
    }

    public int getGoal() {
        return goal;
    }

    public int getDeposited() {
        return deposited;
    }


}
