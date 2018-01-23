package trickandroid.cablevasul.ActivityArea.Details;

/**
 * Created by Simon Peter on 31-Oct-17.
 */

public class VasulDetails {
    int totalConnections, pendingConnections, totalAmount, amountCollected;

    public VasulDetails() {
    }

    public VasulDetails(int totalConnections, int pendingConnections, int totalAmount, int amountCollected) {
        this.totalConnections = totalConnections;
        this.pendingConnections = pendingConnections;
        this.totalAmount = totalAmount;
        this.amountCollected = amountCollected;
    }

    @Override
    public String toString() {
        return "VasulDetails{" +
                "totalConnections=" + totalConnections +
                ", pendingConnections=" + pendingConnections +
                ", totalAmount=" + totalAmount +
                ", amountCollected=" + amountCollected +
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
}
