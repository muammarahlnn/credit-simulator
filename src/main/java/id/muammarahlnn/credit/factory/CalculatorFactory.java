package id.muammarahlnn.credit.factory;

import id.muammarahlnn.credit.model.enums.VehicleType;
import id.muammarahlnn.credit.service.LoanCalculator;
import id.muammarahlnn.credit.service.CarLoanCalculator;
import id.muammarahlnn.credit.service.MotorcycleLoanCalculator;

public class CalculatorFactory {

    public static LoanCalculator getCalculator(VehicleType type) {
        if (type == null) {
            throw new IllegalArgumentException("VehicleType cannot be null");
        }

        return switch (type) {
            case CAR -> new CarLoanCalculator();
            case MOTORCYCLE -> new MotorcycleLoanCalculator();
        };
    }
}