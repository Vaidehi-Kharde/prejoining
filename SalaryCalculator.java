public class SalaryCalculator {

  private static final int STANDARD_WEEKLY_HOURS = 40;
  private static final double FULL_TIME_BONUS = 1000.0;
  private static final double OVERTIME_PAY_MULTIPLIER = 0.5;

  public double calculate(Employee employee) {
    double basePay = employee.getHours() * employee.getRate();

    return switch (employee.getType()) {
      case FULL_TIME -> basePay + FULL_TIME_BONUS;
      case PART_TIME -> basePay + overtimePay(employee);
    };
  }

  private double overtimePay(Employee employee) {
    int hoursWorked = employee.getHours();
    if (hoursWorked <= STANDARD_WEEKLY_HOURS) {
      return 0;
    }

    int overtimeHours = hoursWorked - STANDARD_WEEKLY_HOURS;
    return overtimeHours * employee.getRate() * OVERTIME_PAY_MULTIPLIER;
  }
}
