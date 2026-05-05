package id.muammarahlnn.credit;

import id.muammarahlnn.credit.controller.CreditSimulatorController;
import id.muammarahlnn.credit.repository.ApiLoanRepository;
import id.muammarahlnn.credit.repository.CalculationHistoryRepository;
import id.muammarahlnn.credit.repository.FileLoanRepository;
import id.muammarahlnn.credit.repository.InMemoryCalculationHistoryRepository;
import id.muammarahlnn.credit.repository.LoanRepository;
import id.muammarahlnn.credit.service.LoanService;

public class Main {

    public static void main(String[] args) {
        CalculationHistoryRepository historyRepository = new InMemoryCalculationHistoryRepository();

        if (args.length > 0) {
            String filePath = args[0];
            LoanRepository fileRepository = new FileLoanRepository(filePath);
            LoanService fileService = new LoanService(fileRepository, historyRepository);
            CreditSimulatorController controller = new CreditSimulatorController(fileService);

            controller.runFileMode();
        } else {
            LoanRepository apiRepository = new ApiLoanRepository();
            LoanService apiService = new LoanService(apiRepository, historyRepository);
            CreditSimulatorController controller = new CreditSimulatorController(apiService);

            controller.runInteractiveMode();
        }
    }
}