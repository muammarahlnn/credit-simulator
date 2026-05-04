package id.muammarahlnn.credit.model;

import id.muammarahlnn.credit.model.enums.VehicleCondition;
import id.muammarahlnn.credit.model.enums.VehicleType;
import id.muammarahlnn.credit.util.AppConstants;

import java.time.Year;

public class LoanRequest {

    private VehicleType vehicleType;

    private VehicleCondition condition;

    private int vehicleYear;

    private double totalLoanAmount;

    private int loanTenure;

    private double downPayment;

    public LoanRequest() {}

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public VehicleCondition getCondition() {
        return condition;
    }

    public void setCondition(VehicleCondition condition) {
        this.condition = condition;
    }

    public int getVehicleYear() {
        return vehicleYear;
    }

    public void setVehicleYear(int vehicleYear) {
        this.vehicleYear = vehicleYear;
    }

    public double getTotalLoanAmount() {
        return totalLoanAmount;
    }

    public void setTotalLoanAmount(double totalLoanAmount) {
        this.totalLoanAmount = totalLoanAmount;
    }

    public int getLoanTenure() {
        return loanTenure;
    }

    public void setLoanTenure(int loanTenure) {
        this.loanTenure = loanTenure;
    }

    public double getDownPayment() {
        return downPayment;
    }

    public void setDownPayment(double downPayment) {
        this.downPayment = downPayment;
    }

    public void validate() throws IllegalArgumentException {
        if (this.vehicleType == null) throw new IllegalArgumentException("Vehicle type cannot be null");
        if (this.condition == null) throw new IllegalArgumentException("Vehicle condition cannot be null");

        if (this.condition == VehicleCondition.NEW) {
            int currentYear = Year.now().getValue();
            if (this.vehicleYear < (currentYear - 1)) {
                throw new IllegalArgumentException("NEW vehicles cannot be older than year " + (currentYear - 1));
            }
        }

        if (this.loanTenure < 1 || this.loanTenure > AppConstants.MAX_TENURE_YEARS) {
            throw new IllegalArgumentException("Tenure must be between 1 and " + AppConstants.MAX_TENURE_YEARS + " years");
        }

        double dpRatio = this.downPayment / this.totalLoanAmount;
        if (this.condition == VehicleCondition.NEW && dpRatio < AppConstants.DP_THRESHOLD_NEW) {
            throw new IllegalArgumentException("NEW vehicle Down Payment must be at least 35% of the total loan");
        } else if (this.condition == VehicleCondition.USED && dpRatio < AppConstants.DP_THRESHOLD_USED) {
            throw new IllegalArgumentException("USED vehicle Down Payment must be at least 25% of the total loan");
        }

        if (this.totalLoanAmount > AppConstants.MAX_LOAN_AMOUNT) {
            throw new IllegalArgumentException("Loan amount exceeds the maximum limit of 1 Billion");
        }
    }
}