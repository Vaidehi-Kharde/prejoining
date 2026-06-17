public class Sort {

  /**
   * Sorts the array in ascending order in-place using merge sort.
   * Time complexity: O(n log n). Space complexity: O(n).
   */
  public static void sort(int[] array) {
    if (array == null || array.length <= 1) {
      return;
    }
    mergeSort(array, 0, array.length - 1);
  }

  private static void mergeSort(int[] array, int left, int right) {
    if (left >= right) {
      return;
    }

    int mid = left + (right - left) / 2;
    mergeSort(array, left, mid);
    mergeSort(array, mid + 1, right);
    merge(array, left, mid, right);
  }

  private static void merge(int[] array, int left, int mid, int right) {
    int length = right - left + 1;
    int[] temp = new int[length];

    int i = left;
    int j = mid + 1;
    int k = 0;

    while (i <= mid && j <= right) {
      if (array[i] <= array[j]) {
        temp[k++] = array[i++];
      } else {
        temp[k++] = array[j++];
      }
    }

    while (i <= mid) {
      temp[k++] = array[i++];
    }

    while (j <= right) {
      temp[k++] = array[j++];
    }

    System.arraycopy(temp, 0, array, left, length);
  }

  public static void main(String[] args) {
    int[] numbers = {5, 2, 9, 1, 7, 3};
    sort(numbers);

    for (int value : numbers) {
      System.out.print(value + " ");
    }
    // Output: 1 2 3 5 7 9
  }
}
