package id.muammarahlnn.credit.model.enums;

public enum VehicleCondition {
    NEW("Baru"),
    USED("Bekas");

    private final String value;

    VehicleCondition(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static VehicleCondition fromString(String text) {
        if (text == null) throw new IllegalArgumentException("Condition cannot be null");

        for (VehicleCondition condition : VehicleCondition.values()) {
            if (condition.value.equalsIgnoreCase(text)) {
                return condition;
            }
        }
        throw new IllegalArgumentException("Invalid vehicle condition: " + text);
    }
}