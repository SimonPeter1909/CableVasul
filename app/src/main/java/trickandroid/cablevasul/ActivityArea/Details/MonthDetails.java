package trickandroid.cablevasul.ActivityArea.Details;

/**
 * Created by Simon Peter on 31-Oct-17.
 */

public class MonthDetails {
    String month;
    int totalConnections, pendingConnections, totalAmount, amountCollected;

    public MonthDetails() {
    }

    @Override
    public String toString() {
        return "MonthDetails{" +
                "areaName='" + month + '\'' +
                ", totalConnections=" + totalConnections +
                ", pendingConnections=" + pendingConnections +
                ", totalAmount=" + totalAmount +
                ", amountCollected=" + amountCollected +
                '}';
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String areaName) {
        this.month = areaName;
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

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getAmountCollected() {
        return amountCollected;
    }

    public void setAmountCollected(int amountCollected) {
        this.amountCollected = amountCollected;
    }

    public MonthDetails(String month, int totalConnections, int pendingConnections, int totalAmount, int amountCollected) {
        this.month = month;
        this.totalConnections = totalConnections;
        this.pendingConnections = pendingConnections;
        this.totalAmount = totalAmount;
        this.amountCollected = amountCollected;
    }
}
