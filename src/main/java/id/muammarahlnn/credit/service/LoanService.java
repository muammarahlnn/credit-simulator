package id.muammarahlnn.credit.service;

import id.muammarahlnn.credit.factory.CalculatorFactory;
import id.muammarahlnn.credit.model.ExistingLoanCalculation;
import id.muammarahlnn.credit.model.InstallmentYearResult;
import id.muammarahlnn.credit.model.LoanRequest;
import id.muammarahlnn.credit.repository.CalculationHistoryRepository;
import id.muammarahlnn.credit.repository.LoanRepository;

import java.util.List;
import java.util.Map;

public class LoanService {

    private final LoanRepository loanRepository;

    private final CalculationHistoryRepository historyRepository;

    public LoanService(LoanRepository loanRepository, CalculationHistoryRepository historyRepository) {
        this.loanRepository = loanRepository;
        this.historyRepository = historyRepository;
    }

    public ExistingLoanCalculation processLoan(LoanRequest request) {
        request.validate();

        LoanCalculator calculator = CalculatorFactory.getCalculator(request.getVehicleType());
        List<InstallmentYearResult> results = calculator.calculate(request);

        ExistingLoanCalculation calculation = new ExistingLoanCalculation(request, results);
        historyRepository.save(calculation);

        return calculation;
    }

    public ExistingLoanCalculation loadAndProcessExistingLoan() throws Exception {
        LoanRequest request = loanRepository.getLoan();
        return processLoan(request);
    }

    public Map<Integer, ExistingLoanCalculation> getCalculations() {
        return historyRepository.findAll();
    }

    public ExistingLoanCalculation getCalculationById(int id) {
        return historyRepository.findById(id);
    }
}