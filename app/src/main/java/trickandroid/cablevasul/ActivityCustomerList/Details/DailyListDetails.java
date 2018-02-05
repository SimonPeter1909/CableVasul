package trickandroid.cablevasul.ActivityCustomerList.Details;

/**
 * Created by Simon on 2/3/2018.
 */

public class DailyListDetails {

    int totalConnections, pendingConnections;

    public DailyListDetails() {
    }

    public DailyListDetails(int totalConnections, int pendingConnections) {
        this.totalConnections = totalConnections;
        this.pendingConnections = pendingConnections;
    }

    @Override
    public String toString() {
        return "DailyListDetails{" +
                "totalConnections=" + totalConnections +
                ", pendingConnections=" + pendingConnections +
                '}';
    }

    public int getTotalConnections() {
        return totalConnections;
    }

    public void setTotalConnections(int totalConnections) {
        this.totalConnections = totalConnections;
    }

    public int getPendingConnections() {
        return pendingConnections;
    }

    public void setPendingConnections(int pendingConnections) {
        this.pendingConnections = pendingConnections;
    }
}
