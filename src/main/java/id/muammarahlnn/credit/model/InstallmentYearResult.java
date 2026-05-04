package id.muammarahlnn.credit.model;

public record InstallmentYearResult(int year, double monthlyInstallment, double interestRate) {

    @Override
    public String toString() {
        return String.format("tahun %d : Rp. %,.2f/bln , Suku Bunga : %s%%",
                year,
                monthlyInstallment,
                formatInterestRate(interestRate));
    }

    private String formatInterestRate(double rate) {
        double percentage = rate * 100;
        if (percentage == (long) percentage) {
            return String.format("%d", (long) percentage);
        } else {
            return String.format("%.1f", percentage).replace('.', ',');
        }
    }
}