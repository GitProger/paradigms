package search;

public class BinarySearchSpan {
    // Pred: args != null
    // Post: true
    public static void main(String[] args) {
        // n := args.length
        // args != null && n > 0 && args[0] - correct integer
        int x = Integer.parseInt(args[0]);
        int[] a = new int[args.length - 1];
        int curI = 1;
        while (curI < args.length) {
            // args != null && 0 <= cur - 1 < n - 1 && 1 <= curI < n
            a[curI - 1] = Integer.parseInt(args[curI]);
            // a[curI - 1] = parseInt(...) && curI + 1 <= n  (curI < n)
            curI++;
            // curI <= n
        }
        // curI == n
        // a != null 
        int l1 = recursiveBinarySearch(a, x, true);
        // a != null && l == recursiveBinarySearch(a, x, T) 
        int r1 = recursiveBinarySearch(a, x, false);
        // r == recursiveBinarySearch(a, x, false)

        int l2 = iterativeBinarySearch(a, x, true);
        int r2 = iterativeBinarySearch(a, x, false);

        if (l1 != l2 || r1 != r2) {
            throw new AssertionError("Different results.");
        }
        System.out.println(l1 + " " + (r1 - l1));
        // r1 - l1 == n
    }

    // Pred: a_i >= a_i+1 for i from 0..|a|-1 && a != null
    // Post [R = result]: 
    //      isLeft = true ->
    //          (a[R] >= x && (a[R - 1] < x || R == 0)) || (R == |a| if no a[i] >= x found) && any R' >= R
    //      isLeft = false ->
    //          (a[R] <= x && (a[R + 1] > x || R == |a|)) || (R == |a| if no a[i] <= x found) && any R' <= R
    private static int recursiveBinarySearch(int[] a, int x, boolean isLeft) {
        // Pred
        return recursiveBinarySearchStaff(x, a, 0, a.length, isLeft);
    }

    // Pred: a_i >= a_i+1 for i from 0..|a|-1 && 0 <= l <= r <= |a| && a != null
    // Post [R = result]: 
    //      isLeft = true ->
    //          (a[R] >= x && (a[R - 1] < x || R == 0)) || (R == |a| if no a[i] >= x found) && any R' >= R
    //      isLeft = false ->
    //          (a[R] <= x && (a[R + 1] > x || R == |a|)) || (R == |a| if no a[i] <= x found) && any R' <= R
    private static int recursiveBinarySearchStaff(int x, int[] a, int l, int r, boolean isLeft) {
        if (l >= r) {
            // Pred && (l >= r (l == r)) && x in [l, r)
            return isLeft ? l : r;
        }
        // l < r && (2 * m' == l + r || 2 * m' + 1 == l + r)
        int m = (l + r) / 2;
        // m = m'
        if ((isLeft && a[m] < x) || (!isLeft && a[m] <= x)) {
            // isLeft: a[m + 1] > x && 0 <= m < n && l <= m < r && a[l..m-1] > a[m] > x
            // !isLeft: a[m + 1] >= x && 0 <= m < n && l <= m < r && a[l..m-1] > a[m] >= x
            return recursiveBinarySearchStaff(x, a, m + 1, r, isLeft);
        } else {
            // isLeft: 0 <= m < n && a[m] <= x && l <= m < r && a[m] <= x && a[m - 1] > x
            // !isLeft: 0 <= m < n && a[m] < x && l <= m < r && a[m] < x && a[m - 1] > x
            return recursiveBinarySearchStaff(x, a, l, m, isLeft);
        }
    }

    // Pred: a_i >= a_i+1 for i from 0..|a|-1 && a != null
    // Post [R = result]: 
    //      isLeft = true ->
    //          (a[R] >= x && (a[R - 1] < x || R == 0)) || (R == |a| if no a[i] >= x found) && any R' >= R
    //      isLeft = false ->
    //          (a[R] <= x && (a[R + 1] > x || R == |a|)) || (R == |a| if no a[i] <= x found) && any R' <= R
    private static int iterativeBinarySearch(int[] a, int x, boolean isLeft) {
        // Pred
        int l = 0, r = a.length;
        // isLeft: I := 0 <= l' < n && a[l'] <= x && (a[l' - 1] > x || l' == 0) && a[r] > x
        // !isLeft: I := 0 <= l' < n && a[l'] <= x && a[r - 1] <= x && a[r] > x
        while (l < r) {
            int m = (l + r) / 2;
            // 2 * m == l + r || 2 * m + 1 == l + r
            if ((isLeft && a[m] < x) || (!isLeft && a[m] <= x)) {
                // isLeft: a[m + 1] > x && 0 <= m < n && l <= m < r && a[l..m-1] > a[m] > x
                // !isLeft: a[m + 1] >= x && 0 <= m < n && l <= m < r && a[l..m-1] > a[m] >= x
                l = m + 1;
                // a[l] > x && l == m + 1 && I
            } else {
                // isLeft: 0 <= m < n && a[m] <= x && l <= m < r && a[m] <= x && a[m - 1] > x
                // !isLeft: 0 <= m < n && a[m] < x && l <= m < r && a[m] < x && a[m - 1] > x
                r = m;
                // a[m] <= x && m == r
            }
        }
        // l == r
        return isLeft ? l : r;
    }
}
