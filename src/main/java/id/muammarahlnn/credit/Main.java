package id.muammarahlnn.credit;

import id.muammarahlnn.credit.controller.CreditSimulatorController;
import id.muammarahlnn.credit.repository.ApiLoanRepository;
import id.muammarahlnn.credit.repository.CalculationHistoryRepository;
import id.muammarahlnn.credit.repository.InMemoryCalculationHistoryRepository;
import id.muammarahlnn.credit.repository.LoanRepository;
import id.muammarahlnn.credit.service.LoanService;

public class Main {

    public static void main(String[] args) {
        LoanRepository apiRepository = new ApiLoanRepository();
        CalculationHistoryRepository historyRepository = new InMemoryCalculationHistoryRepository();
        LoanService loanService = new LoanService(apiRepository, historyRepository);
        CreditSimulatorController controller = new CreditSimulatorController(loanService);

        controller.run();
    }
}