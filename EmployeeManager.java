public class EmployeeManager {

  private final SalaryCalculator salaryCalculator;

  public EmployeeManager() {
    this(new SalaryCalculator());
  }

  public EmployeeManager(SalaryCalculator salaryCalculator) {
    this.salaryCalculator = requireNonNull(salaryCalculator, "salaryCalculator");
  }

  public double calculateSalary(Employee employee) {
    requireEmployee(employee);
    return salaryCalculator.calculate(employee);
  }

  public void print(Employee employee) {
    requireEmployee(employee);

    double salary = calculateSalary(employee);
    System.out.printf("%s earns %.2f%n", employee.getName(), salary);
  }

  private static void requireEmployee(Employee employee) {
    if (employee == null) {
      throw new IllegalArgumentException("employee must not be null");
    }
  }

  private static <T> T requireNonNull(T value, String fieldName) {
    if (value == null) {
      throw new IllegalArgumentException(fieldName + " must not be null");
    }
    return value;
  }
}
