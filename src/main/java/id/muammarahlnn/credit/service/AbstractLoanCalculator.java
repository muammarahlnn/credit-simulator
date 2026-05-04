package id.muammarahlnn.credit.service;

import id.muammarahlnn.credit.model.LoanRequest;
import id.muammarahlnn.credit.model.InstallmentYearResult;
import id.muammarahlnn.credit.util.AppConstants;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLoanCalculator implements LoanCalculator {

    protected abstract double getBaseInterestRate();

    @Override
    public List<InstallmentYearResult> calculate(LoanRequest request) {
        List<InstallmentYearResult> results = new ArrayList<>();

        double principalAmount = request.getTotalLoanAmount() - request.getDownPayment();
        double currentRate = getBaseInterestRate();

        for (int year = 1; year <= request.getLoanTenure(); year++) {
            if (year > 1) {
                if (year % 2 == 0) {
                    currentRate += AppConstants.RATE_INC_YEARLY;
                } else {
                    currentRate += AppConstants.RATE_INC_BIENNIAL;
                }
            }

            double totalDebtForYear = principalAmount * (1 + currentRate);
            int remainingTenure = request.getLoanTenure() - year + 1;

            double yearlyInstallment = totalDebtForYear / remainingTenure;
            double monthlyInstallment = yearlyInstallment / 12;

            results.add(new InstallmentYearResult(year, monthlyInstallment, currentRate));
            principalAmount = totalDebtForYear - yearlyInstallment;
        }

        return results;
    }
}