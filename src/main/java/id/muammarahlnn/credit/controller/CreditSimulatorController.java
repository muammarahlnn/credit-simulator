package id.muammarahlnn.credit.controller;

import id.muammarahlnn.credit.model.ExistingLoanCalculation;
import id.muammarahlnn.credit.model.InstallmentYearResult;
import id.muammarahlnn.credit.model.LoanRequest;
import id.muammarahlnn.credit.model.enums.VehicleCondition;
import id.muammarahlnn.credit.model.enums.VehicleType;
import id.muammarahlnn.credit.service.LoanService;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CreditSimulatorController {

    private final Scanner scanner;

    private final LoanService loanService;

    public CreditSimulatorController(LoanService loanService) {
        this.scanner = new Scanner(System.in);
        this.loanService = loanService;
    }

    public void runInteractiveMode() {
        startInteractiveMode();
    }

    public void runFileMode() {
        System.out.println("=== Memproses data dari file ===");
        try {
            ExistingLoanCalculation calculation = loanService.loadAndProcessExistingLoan();

            LoanRequest request = calculation.getRequest();
            System.out.println("\n--- Data successfully loaded! ---");
            printRequestSummary(request);

            printResults(calculation.getResults());
        } catch (IllegalArgumentException e) {
            System.err.println("File Data violates business rules: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Failed to read or parse file: " + e.getMessage());
        }
    }

    private void startInteractiveMode() {
        System.out.println("=== Welcome to Vehicle Credit Simulator ===");
        System.out.println("Type 'show' to see available commands.");

        boolean isRunning = true;
        while (isRunning) {
            System.out.print("\ncredit-simulator> ");
            String[] input = scanner.nextLine().trim().toLowerCase().split("\\s+");
            String command = input[0];

            if (command.isEmpty()) continue;

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
                    case "history":
                        handleShowHistory();
                        break;
                    case "switch":
                        if (input.length < 2) {
                            System.out.println("Error: Silakan masukkan ID kalkulasi. Format: switch <id>");
                        } else {
                            handleSwitchSheet(Integer.parseInt(input[1]));
                        }
                        break;
                    case "exit":
                        isRunning = false;
                        System.out.println("Exiting application. Goodbye!");
                        break;
                    default:
                        System.out.println("Unknown command. Type 'show' for a list of commands.");
                }
            } catch (NumberFormatException e) {
                System.err.println("Error: ID harus berupa angka.");
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private void printCommands() {
        System.out.println("\n--- Available Commands ---");
        System.out.println("show        : Menampilkan semua command yang tersedia");
        System.out.println("manual      : Input data kalkulasi secara manual");
        System.out.println("load        : Load data dari Web Service API");
        System.out.println("history     : Lihat daftar kalkulasi yang tersimpan");
        System.out.println("switch <id> : Tampilkan kembali kalkulasi sebelumnya (ex: switch 1)");
        System.out.println("exit        : Keluar dari aplikasi");
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

        ExistingLoanCalculation calculation = loanService.processLoan(request);
        printResults(calculation.getResults());
    }

    private void handleLoadExisting() {
        try {
            System.out.println("Fetching and processing data from API...");
            ExistingLoanCalculation calculation = loanService.loadAndProcessExistingLoan();

            LoanRequest request = calculation.getRequest();
            List<InstallmentYearResult> results = calculation.getResults();

            System.out.println("\n--- Data successfully fetched! ---");
            printRequestSummary(request);

            printResults(results);
        } catch (IllegalArgumentException e) {
            System.err.println("API Data violates business rules: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Failed to load or parse API data: " + e.getMessage());
        }
    }

    private void handleShowHistory() {
        Map<Integer, ExistingLoanCalculation> history = loanService.getCalculations();

        if (history.isEmpty()) {
            System.out.println("Belum ada data kalkulasi yang tersimpan.");
            return;
        }

        System.out.println("\n--- History Kalkulasi ---");
        for (Map.Entry<Integer, ExistingLoanCalculation> entry : history.entrySet()) {
            LoanRequest req = entry.getValue().getRequest();
            String summary = String.format("[%d] %s %s Tahun %d - Pinjaman: Rp %,.0f",
                    entry.getKey(),
                    req.getVehicleType().getValue(),
                    req.getCondition().getValue(),
                    req.getVehicleYear(),
                    req.getTotalLoanAmount());
            System.out.println(summary);
        }
    }

    private void handleSwitchSheet(int id) {
        ExistingLoanCalculation calculation = loanService.getCalculationById(id);

        if (calculation == null) {
            System.out.println("Error: Data dengan ID " + id + " tidak ditemukan.");
            return;
        }

        LoanRequest request = calculation.getRequest();

        System.out.println("\n--- Membuka Data [ID: " + id + "] ---");
        printRequestSummary(request);

        printResults(calculation.getResults());
    }

    private void printRequestSummary(LoanRequest request) {
        System.out.println("Kendaraan : " + request.getVehicleType().getValue() + " " + request.getCondition().getValue());
        System.out.println("Tahun     : " + request.getVehicleYear());
        System.out.println("Pinjaman  : Rp " + String.format("%,.0f", request.getTotalLoanAmount()));
        System.out.println("Tenor     : " + request.getLoanTenure() + " tahun");
        System.out.println("DP        : Rp " + String.format("%,.0f", request.getDownPayment()));
        System.out.println("---------------------------------");
    }

    private void printResults(List<InstallmentYearResult> results) {
        System.out.println("\nOutput Jumlah Cicilan Perbulan:");
        for (InstallmentYearResult result : results) {
            System.out.println(result.toString());
        }
        System.out.println();
    }
}