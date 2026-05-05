package id.muammarahlnn.credit.controller;

import id.muammarahlnn.credit.model.ExistingLoanCalculation;
import id.muammarahlnn.credit.model.InstallmentYearResult;
import id.muammarahlnn.credit.model.LoanRequest;
import id.muammarahlnn.credit.model.enums.VehicleCondition;
import id.muammarahlnn.credit.model.enums.VehicleType;
import id.muammarahlnn.credit.service.LoanService;
import id.muammarahlnn.credit.util.AppConstants;

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
        System.out.println("\n--- Mode Input Manual ---");
        LoanRequest request = new LoanRequest();

        int currentYear = java.time.Year.now().getValue();

        VehicleType type = readVehicleType();
        request.setVehicleType(type);

        VehicleCondition condition = readVehicleCondition();
        request.setCondition(condition);

        request.setVehicleYear(readVehicleYear(condition, currentYear));

        double totalLoan = readTotalLoanAmount();
        request.setTotalLoanAmount(totalLoan);

        request.setLoanTenure(readLoanTenure());

        request.setDownPayment(readDownPayment(condition, totalLoan));

        try {
            ExistingLoanCalculation calculation = loanService.processLoan(request);
            System.out.println("\n--- Kalkulasi Berhasil ---");
            printRequestSummary(request);
            printResults(calculation.getResults());
        } catch (IllegalArgumentException e) {
            System.err.println("\nValidasi Sistem Gagal: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("\nTerjadi kesalahan saat memproses data: " + e.getMessage());
        }
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

    private VehicleType readVehicleType() {
        while (true) {
            System.out.print("Input Jenis Kendaraan (Mobil|Motor): ");
            String input = scanner.nextLine().trim();
            try {
                return VehicleType.fromString(input);
            } catch (IllegalArgumentException e) {
                System.err.println("Error: Jenis kendaraan tidak valid. Ketik 'Mobil' atau 'Motor'.");
            }
        }
    }

    private VehicleCondition readVehicleCondition() {
        while (true) {
            System.out.print("Input Kondisi Kendaraan (Baru|Bekas): ");
            String input = scanner.nextLine().trim();
            try {
                return VehicleCondition.fromString(input);
            } catch (IllegalArgumentException e) {
                System.err.println("Error: Kondisi kendaraan tidak valid. Ketik 'Baru' atau 'Bekas'.");
            }
        }
    }

    private int readVehicleYear(VehicleCondition condition, int currentYear) {
        while (true) {
            System.out.print("Input Tahun Kendaraan (ex: 2023): ");
            String input = scanner.nextLine().trim();
            try {
                int year = Integer.parseInt(input);

                if (year < 1000 || year > currentYear) {
                    System.err.println("Error: Tahun kendaraan harus 4 digit dan tidak boleh lebih dari " + currentYear + ".");
                } else if (condition == VehicleCondition.NEW && year < (currentYear - 1)) {
                    System.err.println("Error: Kendaraan BARU tidak boleh lebih lama dari tahun " + (currentYear - 1) + ".");
                } else {
                    return year;
                }
            } catch (NumberFormatException e) {
                System.err.println("Error: Input tidak valid. Masukkan angka bulat.");
            }
        }
    }
    private double readTotalLoanAmount() {
        while (true) {
            System.out.print("Input Jumlah Pinjaman Total (Max " + String.format("%,.0f", AppConstants.MAX_LOAN_AMOUNT) + "): ");
            String input = scanner.nextLine().trim();
            try {
                double amount = Double.parseDouble(input);
                if (amount > 0 && amount <= AppConstants.MAX_LOAN_AMOUNT) {
                    return amount;
                } else {
                    System.err.println("Error: Jumlah pinjaman harus > 0 dan maksimal Rp " +
                            String.format("%,.0f", AppConstants.MAX_LOAN_AMOUNT));
                }
            } catch (NumberFormatException e) {
                System.err.println("Error: Input tidak valid. Masukkan angka.");
            }
        }
    }

    private int readLoanTenure() {
        while (true) {
            System.out.print("Input Tenor Pinjaman (1-" + AppConstants.MAX_TENURE_YEARS + " thn): ");
            String input = scanner.nextLine().trim();
            try {
                int tenure = Integer.parseInt(input);
                if (tenure >= 1 && tenure <= AppConstants.MAX_TENURE_YEARS) {
                    return tenure;
                } else {
                    System.err.println("Error: Tenor pinjaman harus antara 1 sampai " + AppConstants.MAX_TENURE_YEARS + " tahun.");
                }
            } catch (NumberFormatException e) {
                System.err.println("Error: Input tidak valid. Masukkan angka bulat.");
            }
        }
    }

    private double readDownPayment(VehicleCondition condition, double totalLoan) {
        double threshold = (condition == VehicleCondition.NEW) ?
                AppConstants.DP_THRESHOLD_NEW : AppConstants.DP_THRESHOLD_USED;

        double minDpAmount = totalLoan * threshold;

        while (true) {
            System.out.print("Input Jumlah DP (Min " + (threshold * 100) + "% = Rp " +
                    String.format("%,.0f", minDpAmount) + "): ");
            String input = scanner.nextLine().trim();
            try {
                double dp = Double.parseDouble(input);
                if (dp >= minDpAmount && dp < totalLoan) {
                    return dp;
                } else if (dp >= totalLoan) {
                    System.err.println("Error: DP tidak boleh lebih besar atau sama dengan total pinjaman.");
                } else {
                    System.err.println("Error: DP terlalu rendah. Minimal " + (threshold * 100) +
                            "% (Rp " + String.format("%,.0f", minDpAmount) + ").");
                }
            } catch (NumberFormatException e) {
                System.err.println("Error: Input tidak valid. Masukkan angka.");
            }
        }
    }
}