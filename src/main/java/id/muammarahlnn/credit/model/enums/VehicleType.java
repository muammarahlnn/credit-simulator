package id.muammarahlnn.credit.model.enums;

public enum VehicleType {
    CAR("Mobil"),
    MOTORCYCLE("Motor");

    private final String value;

    VehicleType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static VehicleType fromString(String text) {
        if (text == null) throw new IllegalArgumentException("Vehicle type cannot be null");

        for (VehicleType type : VehicleType.values()) {
            if (type.value.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid vehicle type: " + text);
    }
}