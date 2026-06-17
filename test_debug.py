import unittest

from debug import divide


class TestDivide(unittest.TestCase):
    def test_normal_division(self):
        self.assertEqual(divide(10, 2), 5.0)

    def test_float_result(self):
        self.assertEqual(divide(7, 2), 3.5)

    def test_negative_numbers(self):
        self.assertEqual(divide(-10, 2), -5.0)

    def test_divide_by_zero(self):
        self.assertEqual(divide(10, 0), "Cannot divide by zero")


if __name__ == "__main__":
    unittest.main()
