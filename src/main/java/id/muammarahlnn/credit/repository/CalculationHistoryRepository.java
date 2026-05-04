package id.muammarahlnn.credit.repository;

import id.muammarahlnn.credit.model.ExistingLoanCalculation;
import java.util.Map;

public interface CalculationHistoryRepository {

    int save(ExistingLoanCalculation calculation);

    Map<Integer, ExistingLoanCalculation> findAll();

    ExistingLoanCalculation findById(int id);
}