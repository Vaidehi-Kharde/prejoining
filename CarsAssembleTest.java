public class CarsAssembleTest {

  private static int passed = 0;
  private static int failed = 0;

  public static void main(String[] args) {
    CarsAssemble assembler = new CarsAssemble();

    testProductionRateStandardEfficiency(assembler);
    testProductionRateModerateEfficiency(assembler);
    testProductionRateHighEfficiency(assembler);
    testProductionRateMaxEfficiency(assembler);
    testWorkingItemsPerMinute(assembler);
    testEfficiencyTierBoundaries(assembler);
    testInvalidSpeed(assembler);

    System.out.printf("%n%d passed, %d failed%n", passed, failed);
    if (failed > 0) {
      System.exit(1);
    }
  }

  // Speeds 1–4: full efficiency (multiplier 1.0).
  private static void testProductionRateStandardEfficiency(CarsAssemble assembler) {
    assertRate(assembler, 1, 221.0);
    assertRate(assembler, 2, 442.0);
    assertRate(assembler, 3, 663.0);
    assertRate(assembler, 4, 884.0);
  }

  // Speeds 5–8: 90% efficiency.
  private static void testProductionRateModerateEfficiency(CarsAssemble assembler) {
    assertRate(assembler, 5, 994.5);
    assertRate(assembler, 6, 1193.4);
    assertRate(assembler, 7, 1392.3);
    assertRate(assembler, 8, 1591.2);
  }

  // Speed 9: 80% efficiency.
  private static void testProductionRateHighEfficiency(CarsAssemble assembler) {
    assertRate(assembler, 9, 1591.2);
  }

  // Speed 10+: 77% efficiency.
  private static void testProductionRateMaxEfficiency(CarsAssemble assembler) {
    assertRate(assembler, 10, 1701.7);
    assertRate(assembler, 12, 2042.04); // 12 * 221 * 0.77
  }

  private static void testWorkingItemsPerMinute(CarsAssemble assembler) {
    assertPerMinute(assembler, 1, 3);
    assertPerMinute(assembler, 2, 7);
    assertPerMinute(assembler, 3, 11);
    assertPerMinute(assembler, 4, 14);
    assertPerMinute(assembler, 5, 16);
    assertPerMinute(assembler, 6, 19);
    assertPerMinute(assembler, 7, 23);
    assertPerMinute(assembler, 8, 26);
    assertPerMinute(assembler, 9, 26);
    assertPerMinute(assembler, 10, 28);
  }

  // Speed 0 or negative: no production (efficiency is zero).
  private static void testInvalidSpeed(CarsAssemble assembler) {
    assertRate(assembler, 0, 0.0);
    assertRate(assembler, -1, 0.0);
    assertRate(assembler, -5, 0.0);
    assertPerMinute(assembler, 0, 0);
    assertPerMinute(assembler, -1, 0);
    assertPerMinute(assembler, -10, 0);
  }

  // Verify the first speed in each tier uses the expected multiplier.
  private static void testEfficiencyTierBoundaries(CarsAssemble assembler) {
    assertRate(assembler, 4, 4 * 221 * 1.0);
    assertRate(assembler, 5, 5 * 221 * 0.9);
    assertRate(assembler, 8, 8 * 221 * 0.9);
    assertRate(assembler, 9, 9 * 221 * 0.8);
    assertRate(assembler, 10, 10 * 221 * 0.77);
  }

  private static void assertRate(CarsAssemble assembler, int speed, double expected) {
    double actual = assembler.productionRatePerHour(speed);
    if (Math.abs(expected - actual) < 0.001) {
      passed++;
      System.out.printf("PASS productionRatePerHour(%d) = %.1f%n", speed, actual);
    } else {
      failed++;
      System.out.printf(
          "FAIL productionRatePerHour(%d): expected %.1f, got %.1f%n",
          speed, expected, actual);
    }
  }

  private static void assertPerMinute(CarsAssemble assembler, int speed, int expected) {
    int actual = assembler.workingItemsPerMinute(speed);
    if (expected == actual) {
      passed++;
      System.out.printf("PASS workingItemsPerMinute(%d) = %d%n", speed, actual);
    } else {
      failed++;
      System.out.printf(
          "FAIL workingItemsPerMinute(%d): expected %d, got %d%n",
          speed, expected, actual);
    }
  }
}
