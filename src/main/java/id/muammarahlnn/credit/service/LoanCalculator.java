package id.muammarahlnn.credit.service;

import id.muammarahlnn.credit.model.LoanRequest;
import id.muammarahlnn.credit.model.InstallmentYearResult;
import java.util.List;

public interface LoanCalculator {

    List<InstallmentYearResult> calculate(LoanRequest request);
}