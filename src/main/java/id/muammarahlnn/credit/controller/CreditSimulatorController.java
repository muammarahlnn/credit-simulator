package id.muammarahlnn.credit.controller;

import id.muammarahlnn.credit.model.ExistingLoanCalculation;
import id.muammarahlnn.credit.model.InstallmentYearResult;
import id.muammarahlnn.credit.model.LoanRequest;
import id.muammarahlnn.credit.model.enums.VehicleCondition;
import id.muammarahlnn.credit.model.enums.VehicleType;
import id.muammarahlnn.credit.service.LoanService;

import java.util.List;
import java.util.Scanner;

public class CreditSimulatorController {

    private final Scanner scanner;
    private final LoanService loanService; // Controller only knows the Service

    public CreditSimulatorController(LoanService loanService) {
        this.scanner = new Scanner(System.in);
        this.loanService = loanService;
    }

    public void run() {
        startInteractiveMode();
    }

    private void startInteractiveMode() {
        System.out.println("=== Welcome to Vehicle Credit Simulator ===");
        System.out.println("Type 'show' to see available commands.");

        boolean isRunning = true;
        while (isRunning) {
            System.out.print("\ncredit-simulator> ");
            String command = scanner.nextLine().trim().toLowerCase();

            try {
                switch (command) {
                    case "show":
                        printCommands();
                        break;
                    case "manual":
                        handleManualInput();
                        break;
                    case "load":
                        handleLoadExisting();
                        break;
                    case "exit":
                        isRunning = false;
                        System.out.println("Exiting application. Goodbye!");
                        break;
                    default:
                        System.out.println("Unknown command. Type 'show' for a list of commands.");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private void printCommands() {
        System.out.println("\n--- Available Commands ---");
        System.out.println("show   : Menampilkan semua command yang tersedia");
        System.out.println("manual : Input data kalkulasi secara manual step-by-step");
        System.out.println("load   : Load data dari Web Service API");
        System.out.println("exit   : Keluar dari aplikasi");
        System.out.println("--------------------------");
    }

    private void handleManualInput() {
        LoanRequest request = new LoanRequest();

        System.out.print("Input Jenis Kendaraan (Mobil|Motor): ");
        request.setVehicleType(VehicleType.fromString(scanner.nextLine().trim()));

        System.out.print("Input Kondisi Kendaraan (Baru|Bekas): ");
        request.setCondition(VehicleCondition.fromString(scanner.nextLine().trim()));

        System.out.print("Input Tahun Kendaraan (ex: 2023): ");
        request.setVehicleYear(Integer.parseInt(scanner.nextLine().trim()));

        System.out.print("Input Jumlah Pinjaman Total (Max 1 Miliar): ");
        request.setTotalLoanAmount(Double.parseDouble(scanner.nextLine().trim()));

        System.out.print("Input Tenor Pinjaman (1-6 thn): ");
        request.setLoanTenure(Integer.parseInt(scanner.nextLine().trim()));

        System.out.print("Input Jumlah DP: ");
        request.setDownPayment(Double.parseDouble(scanner.nextLine().trim()));

        List<InstallmentYearResult> results = loanService.processLoan(request);
        printResults(results);
    }

    private void handleLoadExisting() {
        try {
            System.out.println("Fetching and processing data from API...");
            ExistingLoanCalculation calculation = loanService.loadAndProcessExistingLoan();

            LoanRequest request = calculation.getRequest();
            List<InstallmentYearResult> results = calculation.getResults();

            System.out.println("\n--- Data successfully loaded! ---");
            System.out.println("Kendaraan : " + request.getVehicleType().getValue() + " " + request.getCondition().getValue());
            System.out.println("Tahun     : " + request.getVehicleYear());
            System.out.println("Pinjaman  : Rp " + String.format("%,.0f", request.getTotalLoanAmount()));
            System.out.println("Tenor     : " + request.getLoanTenure() + " tahun");
            System.out.println("DP        : Rp " + String.format("%,.0f", request.getDownPayment()));
            System.out.println("---------------------------------");

            printResults(results);
        } catch (IllegalArgumentException e) {
            System.err.println("API Data violates business rules: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Failed to load or parse API data: " + e.getMessage());
        }
    }

    private void printResults(List<InstallmentYearResult> results) {
        System.out.println("\nOutput Jumlah Cicilan Perbulan:");
        for (InstallmentYearResult result : results) {
            System.out.println(result.toString());
        }
        System.out.println();
    }
}