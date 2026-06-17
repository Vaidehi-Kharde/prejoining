public enum EmployeeType {
  FULL_TIME("FULLTIME"),
  PART_TIME("PARTTIME");

  private final String code;

  EmployeeType(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  public static EmployeeType fromCode(String code) {
    if (code == null) {
      throw new IllegalArgumentException("type must not be null");
    }
    for (EmployeeType type : values()) {
      if (type.code.equals(code)) {
        return type;
      }
    }
    throw new IllegalArgumentException(
        "type must be " + FULL_TIME.code + " or " + PART_TIME.code);
  }
}
