package id.muammarahlnn.credit.service;

import id.muammarahlnn.credit.factory.CalculatorFactory;
import id.muammarahlnn.credit.model.ExistingLoanCalculation;
import id.muammarahlnn.credit.model.InstallmentYearResult;
import id.muammarahlnn.credit.model.LoanRequest;
import id.muammarahlnn.credit.repository.LoanRepository;

import java.util.List;

public class LoanService {

    private final LoanRepository loanRepository;

    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public List<InstallmentYearResult> processLoan(LoanRequest request) {
        request.validate();
        LoanCalculator calculator = CalculatorFactory.getCalculator(request.getVehicleType());
        return calculator.calculate(request);
    }

    public ExistingLoanCalculation loadAndProcessExistingLoan() throws Exception {
        LoanRequest request = loanRepository.getLoan();
        request.validate();

        LoanCalculator calculator = CalculatorFactory.getCalculator(request.getVehicleType());
        List<InstallmentYearResult> results = calculator.calculate(request);

        return new ExistingLoanCalculation(request, results);
    }
}