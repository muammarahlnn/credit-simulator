package id.muammarahlnn.credit.service;

import id.muammarahlnn.credit.model.ExistingLoanCalculation;
import id.muammarahlnn.credit.model.LoanRequest;
import id.muammarahlnn.credit.model.enums.VehicleCondition;
import id.muammarahlnn.credit.model.enums.VehicleType;
import id.muammarahlnn.credit.repository.CalculationHistoryRepository;
import id.muammarahlnn.credit.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private CalculationHistoryRepository historyRepository;

    private LoanService loanService;

    @BeforeEach
    void setUp() {
        loanService = new LoanService(loanRepository, historyRepository);
    }

    @Test
    @DisplayName("Should successfully process loan and save to history when request is valid")
    void testProcessLoanSuccess() {
        // Given
        LoanRequest request = new LoanRequest();
        request.setVehicleType(VehicleType.CAR);
        request.setCondition(VehicleCondition.NEW);
        request.setVehicleYear(Year.now().getValue());
        request.setTotalLoanAmount(500_000_000.0);
        request.setLoanTenure(5);
        request.setDownPayment(200_000_000.0); // 40% (Above 35% threshold)

        // When
        ExistingLoanCalculation result = loanService.processLoan(request);

        // Then
        assertNotNull(result, "Result should not be null");
        assertEquals(5, result.getResults().size(), "Should have 5 years of installment results");
        verify(historyRepository, times(1)).save(any(ExistingLoanCalculation.class));
    }

    @Test
    @DisplayName("Should throw exception when NEW car down payment is below 35%")
    void testProcessLoanInvalidNewCarDP() {
        // Given
        LoanRequest request = new LoanRequest();
        request.setVehicleType(VehicleType.CAR);
        request.setCondition(VehicleCondition.NEW);
        request.setVehicleYear(Year.now().getValue());
        request.setTotalLoanAmount(100_000_000.0);
        request.setLoanTenure(3);
        request.setDownPayment(30_000_000.0); // 30% (Threshold is 35%)

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> loanService.processLoan(request));

        // Then
        assertTrue(exception.getMessage().contains("at least 35%"));
        verify(historyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when USED vehicle down payment is below 25%")
    void testProcessLoanInvalidUsedVehicleDP() {
        // Given
        LoanRequest request = new LoanRequest();
        request.setVehicleType(VehicleType.MOTORCYCLE);
        request.setCondition(VehicleCondition.USED);
        request.setVehicleYear(Year.now().getValue() - 2);
        request.setTotalLoanAmount(20_000_000.0);
        request.setLoanTenure(2);
        request.setDownPayment(4_000_000.0); // 20% (Threshold is 25%)

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> loanService.processLoan(request));

        // Then
        assertTrue(exception.getMessage().contains("at least 25%"));
        verify(historyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when loan amount exceeds 1 Billion")
    void testProcessLoanExceedsLimit() {
        // Given
        LoanRequest request = new LoanRequest();
        request.setVehicleType(VehicleType.CAR);
        request.setCondition(VehicleCondition.NEW);
        request.setVehicleYear(Year.now().getValue());
        request.setTotalLoanAmount(1_500_000_000.0); // 1.5 Billion
        request.setLoanTenure(1);
        request.setDownPayment(600_000_000.0);

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> loanService.processLoan(request));

        // Then
        assertTrue(exception.getMessage().contains("exceeds the maximum limit"));
        verify(historyRepository, never()).save(any());
    }
}