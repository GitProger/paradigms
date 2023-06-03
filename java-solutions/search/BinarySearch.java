package search;

public class BinarySearch {
    // :NOTE: не вызывается iterative; надо вызывать оба варианта, сравнивать ответы, если не равны, то exception
    //:note: not full pre
    // Pred: args != null
    // Post: true
    public static void main(String[] args) {
        // n := args.length
        // n > 0 && args[0] - correct integer
        int x = Integer.parseInt(args[0]);
        int[] a = new int[args.length - 1];
        int curI = 1;
        while (curI < args.length) {
            // 0 <= cur - 1 < n - 1 && 1 <= curI < n
            a[curI - 1] = Integer.parseInt(args[curI]);
            // curI + 1 <= n  (curI < n)
            curI++;
            // curI <= n
        }
        // curI == n

        int res1 = recursiveBinarySearch(a, x);
        int res2 = iterativeBinarySearch(a, x);
        if (res1 != res2) {
            throw new AssertionError("Different results.");
        }
        System.out.println(res1);
    }

    // Pred: a_i >= a_i+1 for i from 0..|a|-1 && a != null
    // Post: (a[R] <= x && (a[R - 1] > x || R == 0)) || (R == |a| if no a[i] <= x found) && any R' >= R
    private static int recursiveBinarySearch(int[] a, int x) {
        return recursiveBinarySearchStaff(x, a, 0, a.length);
    }

    // Pred: a_i >= a_i+1 for i from 0..|a|-1 && 0 <= l <= r <= |a| && a != null
    // Post: (a[R] <= x && (a[R - 1] > x || R == 0)) || (R == |a| if no a[i] <= x found) && any R' >= R
    private static int recursiveBinarySearchStaff(int x, int[] a, int l, int r) {
        if (l >= r) {
            // l >= r
            return l;
        }
        // l < r && (2 * m' == l + r || 2 * m' + 1 == l + r)
        //:note: overflow
        int m = (l + r) / 2;
        // 0 <= m < n && l <= m < r
        if (a[m] > x) {
            // a[m + 1] > x && 0 <= m < n && l <= m < r && a[l..m-1] > a[m] > x
            return recursiveBinarySearchStaff(x, a, m + 1, r);
        } else {
            // 0 <= m < n && a[m] <= x && l <= m < r && a[m] <= x && a[m - 1] > x
            return recursiveBinarySearchStaff(x, a, l, m);
        }
    }

    // Pred: a_i >= a_i+1 for i from 0..|a|-1 && a != null
    // Post: (a[R] <= x && (a[R - 1] > x || R == 0)) || (R == |a| if no a[i] <= x found) && any R' >= R
    private static int iterativeBinarySearch(int[] a, int x) {
        int l = 0, r = a.length;
        // I := 0 <= l' < n && a[l'] <= x && (a[l' - 1] > x || l' == 0) && a[r] > x
        while (l < r) {
            // 2 * m == l + r || 2 * m + 1 == l + r
            int m = (l + r) / 2;
            // 0 <= m < n && l <= m < r
            if (a[m] > x) {
                // a[m + 1] > x && 0 <= m < n && l <= m < r && a[l..m-1] > a[m] > x
                l = m + 1;
                // a[l] > x && l == m + 1 && I
            } else { // a[m] <= x
                // 0 <= m < n && a[m] <= x && l <= m < r && a[m] <= x && a[m - 1] > x
                r = m;
                // a[m] <= x && m == r
            }
        }
        // l >= r (l == r)
        return l;
    }
}
