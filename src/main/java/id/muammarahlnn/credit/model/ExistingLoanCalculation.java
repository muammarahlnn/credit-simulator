package id.muammarahlnn.credit.model;

import java.util.List;

public class ExistingLoanCalculation {

    private final LoanRequest request;

    private final List<InstallmentYearResult> results;

    public ExistingLoanCalculation(LoanRequest request, List<InstallmentYearResult> results) {
        this.request = request;
        this.results = results;
    }

    public LoanRequest getRequest() {
        return request;
    }

    public List<InstallmentYearResult> getResults() {
        return results;
    }
}