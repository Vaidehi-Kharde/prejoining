public class Employee {

  private final String name;
  private final EmployeeType type;
  private final int hours;
  private final double rate;

  public Employee(String name, EmployeeType type, int hours, double rate) {
    this.name = requireNonBlank(name, "name");
    this.type = requireNonNull(type, "type");
    this.hours = requireNonNegative(hours, "hours");
    this.rate = requirePositive(rate, "rate");
  }

  public Employee(String name, String typeCode, int hours, double rate) {
    this(name, EmployeeType.fromCode(typeCode), hours, rate);
  }

  public String getName() {
    return name;
  }

  public EmployeeType getType() {
    return type;
  }

  public int getHours() {
    return hours;
  }

  public double getRate() {
    return rate;
  }

  private static String requireNonBlank(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(fieldName + " must not be blank");
    }
    return value.trim();
  }

  private static <T> T requireNonNull(T value, String fieldName) {
    if (value == null) {
      throw new IllegalArgumentException(fieldName + " must not be null");
    }
    return value;
  }

  private static int requireNonNegative(int value, String fieldName) {
    if (value < 0) {
      throw new IllegalArgumentException(fieldName + " must not be negative");
    }
    return value;
  }

  private static double requirePositive(double value, String fieldName) {
    if (value <= 0) {
      throw new IllegalArgumentException(fieldName + " must be greater than zero");
    }
    return value;
  }
}
