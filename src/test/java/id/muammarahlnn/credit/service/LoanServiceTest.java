package id.muammarahlnn.credit.service;

import id.muammarahlnn.credit.model.ExistingLoanCalculation;
import id.muammarahlnn.credit.model.LoanRequest;
import id.muammarahlnn.credit.model.enums.VehicleCondition;
import id.muammarahlnn.credit.model.enums.VehicleType;
import id.muammarahlnn.credit.repository.CalculationHistoryRepository;
import id.muammarahlnn.credit.repository.LoanRepository;
import id.muammarahlnn.credit.util.AppConstants;
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

    private LoanRequest createValidBaseRequest() {
        LoanRequest request = new LoanRequest();
        request.setVehicleType(VehicleType.CAR);
        request.setCondition(VehicleCondition.NEW);
        request.setVehicleYear(Year.now().getValue());
        request.setTotalLoanAmount(500_000_000.0);
        request.setLoanTenure(5);
        request.setDownPayment(200_000_000.0); // 40% DP (Valid)
        return request;
    }

    @Test
    @DisplayName("Should successfully process NEW CAR loan and save to history")
    void testProcessLoanSuccessNewCar() {
        // Given
        LoanRequest request = createValidBaseRequest();

        // When
        ExistingLoanCalculation result = loanService.processLoan(request);

        // Then
        assertNotNull(result, "Result should not be null");
        assertEquals(5, result.getResults().size(), "Should have 5 years of installment results");
        verify(historyRepository, times(1)).save(any(ExistingLoanCalculation.class));
    }

    @Test
    @DisplayName("Should successfully process USED MOTORCYCLE loan")
    void testProcessLoanSuccessUsedMotorcycle() {
        // Given
        LoanRequest request = createValidBaseRequest();
        request.setVehicleType(VehicleType.MOTORCYCLE);
        request.setCondition(VehicleCondition.USED);
        request.setVehicleYear(Year.now().getValue() - 3); // 3 years old
        request.setTotalLoanAmount(20_000_000.0);
        request.setDownPayment(6_000_000.0); // 30% DP (Valid for USED)

        // When
        ExistingLoanCalculation result = loanService.processLoan(request);

        // Then
        assertNotNull(result);
        verify(historyRepository, times(1)).save(any(ExistingLoanCalculation.class));
    }

    @Test
    @DisplayName("Should throw exception when Vehicle Type is null")
    void testProcessLoanNullVehicleType() {
        // Given
        LoanRequest request = createValidBaseRequest();
        request.setVehicleType(null);

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> loanService.processLoan(request));

        // Then
        assertTrue(exception.getMessage().contains("Jenis kendaraan tidak boleh kosong"));
        verify(historyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when Vehicle Condition is null")
    void testProcessLoanNullVehicleCondition() {
        // Given
        LoanRequest request = createValidBaseRequest();
        request.setCondition(null);

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> loanService.processLoan(request));

        // Then
        assertTrue(exception.getMessage().contains("Kondisi kendaraan tidak boleh kosong"));
        verify(historyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when Vehicle Year is in the future")
    void testProcessLoanFutureYear() {
        // Given
        LoanRequest request = createValidBaseRequest();
        request.setVehicleYear(Year.now().getValue() + 1);

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> loanService.processLoan(request));

        // Then
        assertTrue(exception.getMessage().contains("tidak boleh lebih dari tahun saat ini"));
        verify(historyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when NEW vehicle is older than 1 year")
    void testProcessLoanNewVehicleTooOld() {
        // Given
        LoanRequest request = createValidBaseRequest();
        request.setCondition(VehicleCondition.NEW);
        request.setVehicleYear(Year.now().getValue() - 2); // 2 years old

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> loanService.processLoan(request));

        // Then
        assertTrue(exception.getMessage().contains("tidak boleh lebih lama dari tahun"));
        verify(historyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when Loan Tenure is less than 1")
    void testProcessLoanTenureTooShort() {
        // Given
        LoanRequest request = createValidBaseRequest();
        request.setLoanTenure(0);

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> loanService.processLoan(request));

        // Then
        assertTrue(exception.getMessage().contains("Tenor harus antara 1 sampai"));
        verify(historyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when Loan Tenure exceeds maximum limit")
    void testProcessLoanTenureTooLong() {
        // Given
        LoanRequest request = createValidBaseRequest();
        request.setLoanTenure(AppConstants.MAX_TENURE_YEARS + 1);

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> loanService.processLoan(request));

        // Then
        assertTrue(exception.getMessage().contains("Tenor harus antara 1 sampai"));
        verify(historyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when NEW vehicle down payment is below 35%")
    void testProcessLoanInvalidNewCarDP() {
        // Given
        LoanRequest request = createValidBaseRequest();
        request.setTotalLoanAmount(100_000_000.0);
        request.setDownPayment(30_000_000.0); // 30%

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> loanService.processLoan(request));

        // Then
        assertTrue(exception.getMessage().contains("minimal 35.0%"));
        verify(historyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when USED vehicle down payment is below 25%")
    void testProcessLoanInvalidUsedVehicleDP() {
        // Given
        LoanRequest request = createValidBaseRequest();
        request.setCondition(VehicleCondition.USED);
        request.setVehicleYear(Year.now().getValue() - 2);
        request.setTotalLoanAmount(100_000_000.0);
        request.setDownPayment(20_000_000.0); // 20%

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> loanService.processLoan(request));

        // Then
        assertTrue(exception.getMessage().contains("minimal 25.0%"));
        verify(historyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when loan amount exceeds max limit")
    void testProcessLoanExceedsLimit() {
        // Given
        LoanRequest request = createValidBaseRequest();
        request.setTotalLoanAmount(AppConstants.MAX_LOAN_AMOUNT + 1_000_000.0);
        request.setDownPayment(request.getTotalLoanAmount() * 0.5); // DP is fine, but total is too high

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> loanService.processLoan(request));

        // Then
        assertTrue(exception.getMessage().contains("melebihi batas maksimal"));
        verify(historyRepository, never()).save(any());
    }
}