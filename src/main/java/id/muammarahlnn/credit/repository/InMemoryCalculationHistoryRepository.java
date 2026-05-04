package id.muammarahlnn.credit.repository;

import id.muammarahlnn.credit.model.ExistingLoanCalculation;

import java.util.LinkedHashMap;
import java.util.Map;

public class InMemoryCalculationHistoryRepository implements CalculationHistoryRepository {

    private final Map<Integer, ExistingLoanCalculation> storage = new LinkedHashMap<>();

    private int currentSequenceId = 1;

    @Override
    public int save(ExistingLoanCalculation calculation) {
        int id = currentSequenceId++;
        storage.put(id, calculation);
        return id;
    }

    @Override
    public Map<Integer, ExistingLoanCalculation> findAll() {
        return storage;
    }

    @Override
    public ExistingLoanCalculation findById(int id) {
        return storage.get(id);
    }
}