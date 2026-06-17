public class CarsAssemble {

  // Base number of cars produced per hour at a given assembly speed.
  private static final int BASE_CARS_PER_SPEED_UNIT = 221;

  // Efficiency multipliers applied when speed exceeds certain thresholds.
  private static final double EFFICIENCY_STANDARD = 1.0;
  private static final double EFFICIENCY_MODERATE = 0.9;
  private static final double EFFICIENCY_HIGH = 0.8;
  private static final double EFFICIENCY_MAX = 0.77;
  private static final double EFFICIENCY_NONE = 0.0;

  /**
   * Calculates how many cars are produced per hour at the given assembly speed.
   *
   * <p>Invalid speeds (0 or negative) produce no cars. For valid speeds, higher speeds
   * increase raw output, but efficiency drops once speed exceeds 4:
   * <ul>
   *   <li>Speed 0 or below: no production</li>
   *   <li>Speed 1–4: full efficiency</li>
   *   <li>Speed 5–8: 90% efficiency</li>
   *   <li>Speed 9: 80% efficiency</li>
   *   <li>Speed 10+: 77% efficiency</li>
   * </ul>
   */
  public double productionRatePerHour(int speed) {
    double baseRate = speed * BASE_CARS_PER_SPEED_UNIT;
    double efficiency = efficiencyForSpeed(speed);
    return baseRate * efficiency;
  }

  /**
   * Returns the number of fully assembled cars produced per minute.
   * Fractional cars are truncated (e.g. 4.9 becomes 4).
   */
  public int workingItemsPerMinute(int speed) {
    return (int) (productionRatePerHour(speed) / 60);
  }

  private double efficiencyForSpeed(int speed) {
    if (speed <= 0) {
      return EFFICIENCY_NONE;
    }
    if (speed <= 4) {
      return EFFICIENCY_STANDARD;
    }
    if (speed <= 8) {
      return EFFICIENCY_MODERATE;
    }
    if (speed == 9) {
      return EFFICIENCY_HIGH;
    }
    return EFFICIENCY_MAX;
  }
}
