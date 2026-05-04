package id.muammarahlnn.credit.repository;

import id.muammarahlnn.credit.model.LoanRequest;

public interface LoanRepository {

    LoanRequest getLoan() throws Exception;
}