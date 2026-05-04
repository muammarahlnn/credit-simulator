package id.muammarahlnn.credit.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.muammarahlnn.credit.model.LoanRequest;
import id.muammarahlnn.credit.model.enums.VehicleCondition;
import id.muammarahlnn.credit.model.enums.VehicleType;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiLoanRepository implements LoanRepository {

    private static final String GET_MOCK_LOAN_URL = "https://mocki.io/v1/04e4d7a3-2d0b-4620-923d-4a9497c6105f";

    private final HttpClient client;

    private final ObjectMapper mapper;

    public ApiLoanRepository() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.mapper = new ObjectMapper();
    }

    @Override
    public LoanRequest getLoan() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GET_MOCK_LOAN_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("API returned status code: " + response.statusCode());
        }

        JsonNode rootNode = mapper.readTree(response.body());

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