package id.muammarahlnn.credit.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.muammarahlnn.credit.model.LoanRequest;
import id.muammarahlnn.credit.model.enums.VehicleCondition;
import id.muammarahlnn.credit.model.enums.VehicleType;

import java.io.File;

public class FileLoanRepository implements LoanRepository {

    private final String filePath;
    private final ObjectMapper mapper;

    public FileLoanRepository(String filePath) {
        this.filePath = filePath;
        this.mapper = new ObjectMapper();
    }

    @Override
    public LoanRequest getLoan() throws Exception {
        File file = new File(filePath);

        if (!file.exists()) {
            throw new RuntimeException("File tidak ditemukan di path: " + file.getAbsolutePath());
        }

        JsonNode rootNode = mapper.readTree(file);

        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setVehicleType(VehicleType.fromString(rootNode.get("vehicleType").asText()));
        loanRequest.setCondition(VehicleCondition.fromString(rootNode.get("vehicleCondition").asText()));
        loanRequest.setVehicleYear(rootNode.get("vehicleYear").asInt());
        loanRequest.setTotalLoanAmount(rootNode.get("totalLoanAmount").asDouble());
        loanRequest.setLoanTenure(rootNode.get("loanTenure").asInt());
        loanRequest.setDownPayment(rootNode.get("downPayment").asDouble());

        return loanRequest;
    }
}