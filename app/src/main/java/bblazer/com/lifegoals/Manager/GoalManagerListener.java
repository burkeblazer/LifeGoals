package bblazer.com.lifegoals.Manager;

public interface GoalManagerListener {
    void goalsUpdated(int numUpdated, int numDeleted, int numAdded);
}
