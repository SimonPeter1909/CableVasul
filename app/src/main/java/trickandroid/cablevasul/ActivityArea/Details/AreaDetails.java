package trickandroid.cablevasul.ActivityArea.Details;

/**
 * Created by Simon Peter on 31-Oct-17.
 */

public class AreaDetails {
    String areaName;
    int totalConnections, pendingConnections;

    public AreaDetails() {
    }

    public AreaDetails(String areaName, int totalConnections, int pendingConnections) {
        this.areaName = areaName;
        this.totalConnections = totalConnections;
        this.pendingConnections = pendingConnections;
    }

    public String getAreaNama() {
        return areaName;
    }

    public void setAreaNama(String areaNama) {
        this.areaName = areaNama;
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

    @Override
    public String toString() {
        return "AreaDetails{" +
                "areaNama='" + areaName + '\'' +
                ", totalConnections='" + totalConnections + '\'' +
                ", pendingConnections='" + pendingConnections + '\'' +
                '}';
    }
}
