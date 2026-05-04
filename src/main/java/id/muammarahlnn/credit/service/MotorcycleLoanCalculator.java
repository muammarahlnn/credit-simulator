package id.muammarahlnn.credit.service;

import id.muammarahlnn.credit.util.AppConstants;

public class MotorcycleLoanCalculator extends AbstractLoanCalculator {

    @Override
    protected double getBaseInterestRate() {
        return AppConstants.BASE_RATE_MOTORCYCLE;
    }
}