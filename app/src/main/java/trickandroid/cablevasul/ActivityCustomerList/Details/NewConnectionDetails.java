package trickandroid.cablevasul.ActivityCustomerList.Details;

/**
 * Created by Simon Peter on 06-Nov-17.
 */

public class NewConnectionDetails {

    String date, name, areaName, monthlyAmount, connectionNumber, mobileNumber, aadharNumber, cafNumber, setUpBoxSerial, paidDate, paid, monthAndYear;
    int intDate;

    public NewConnectionDetails() {
    }

    public NewConnectionDetails(String date, String name, String areaName, String monthlyAmount, String connectionNumber, String mobileNumber, String aadharNumber, String cafNumber, String setUpBoxSerial, String paidDate, String paid, String monthAndYear, int intDate) {
        this.date = date;
        this.name = name;
        this.areaName = areaName;
        this.monthlyAmount = monthlyAmount;
        this.connectionNumber = connectionNumber;
        this.mobileNumber = mobileNumber;
        this.aadharNumber = aadharNumber;
        this.cafNumber = cafNumber;
        this.setUpBoxSerial = setUpBoxSerial;
        this.paidDate = paidDate;
        this.paid = paid;
        this.monthAndYear = monthAndYear;
        this.intDate = intDate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getMonthlyAmount() {
        return monthlyAmount;
    }

    public void setMonthlyAmount(String monthlyAmount) {
        this.monthlyAmount = monthlyAmount;
    }

    public String getConnectionNumber() {
        return connectionNumber;
    }

    public void setConnectionNumber(String connectionNumber) {
        this.connectionNumber = connectionNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    public String getCafNumber() {
        return cafNumber;
    }

    public void setCafNumber(String cafNumber) {
        this.cafNumber = cafNumber;
    }

    public String getSetUpBoxSerial() {
        return setUpBoxSerial;
    }

    public void setSetUpBoxSerial(String setUpBoxSerial) {
        this.setUpBoxSerial = setUpBoxSerial;
    }

    public String getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(String paidDate) {
        this.paidDate = paidDate;
    }

    public String getPaid() {
        return paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public String getMonthAndYear() {
        return monthAndYear;
    }

    public void setMonthAndYear(String monthAndYear) {
        this.monthAndYear = monthAndYear;
    }

    public int getIntDate() {
        return intDate;
    }

    public void setIntDate(int intDate) {
        this.intDate = intDate;
    }

    @Override
    public String toString() {
        return "NewConnectionDetails{" +
                "date='" + date + '\'' +
                ", name='" + name + '\'' +
                ", areaName='" + areaName + '\'' +
                ", monthlyAmount='" + monthlyAmount + '\'' +
                ", connectionNumber='" + connectionNumber + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", aadharNumber='" + aadharNumber + '\'' +
                ", cafNumber='" + cafNumber + '\'' +
                ", setUpBoxSerial='" + setUpBoxSerial + '\'' +
                ", paidDate='" + paidDate + '\'' +
                ", paid='" + paid + '\'' +
                ", monthAndYear='" + monthAndYear + '\'' +
                ", intDate=" + intDate +
                '}';
    }
}
