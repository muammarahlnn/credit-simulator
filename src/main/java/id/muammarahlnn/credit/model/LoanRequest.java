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
        if (this.vehicleType == null) throw new IllegalArgumentException("Jenis kendaraan tidak boleh kosong");
        if (this.condition == null) throw new IllegalArgumentException("Kondisi kendaraan tidak boleh kosong");

        int currentYear = Year.now().getValue();
        if (this.vehicleYear > currentYear) {
            throw new IllegalArgumentException("Tahun kendaraan tidak boleh lebih dari tahun saat ini (" + currentYear + ")");
        }
        if (this.condition == VehicleCondition.NEW) {
            if (this.vehicleYear < (currentYear - 1)) {
                throw new IllegalArgumentException("Kendaraan BARU tidak boleh lebih lama dari tahun " + (currentYear - 1));
            }
        }

        if (this.loanTenure < 1 || this.loanTenure > AppConstants.MAX_TENURE_YEARS) {
            throw new IllegalArgumentException("Tenor harus antara 1 sampai " + AppConstants.MAX_TENURE_YEARS + " tahun");
        }

        double dpRatio = this.downPayment / this.totalLoanAmount;
        if (this.condition == VehicleCondition.NEW && dpRatio < AppConstants.DP_THRESHOLD_NEW) {
            throw new IllegalArgumentException("DP kendaraan BARU minimal " + (AppConstants.DP_THRESHOLD_NEW * 100) + "% dari total pinjaman");
        } else if (this.condition == VehicleCondition.USED && dpRatio < AppConstants.DP_THRESHOLD_USED) {
            throw new IllegalArgumentException("DP kendaraan BEKAS minimal " + (AppConstants.DP_THRESHOLD_USED * 100) + "% dari total pinjaman");
        }

        if (this.totalLoanAmount > AppConstants.MAX_LOAN_AMOUNT) {
            throw new IllegalArgumentException("Jumlah pinjaman melebihi batas maksimal Rp " + String.format("%,.0f", AppConstants.MAX_LOAN_AMOUNT));
        }
    }
}