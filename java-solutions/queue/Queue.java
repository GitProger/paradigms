package queue;

interface Queue {
 /**
  *  Model: a[0] .. a[n - 1]
  *         n >= 0
  *  Invariant: for i = 0 to n - 1: a[i] != null

     Let Statement(n): for i = 0 to n - 1: a'[i] == a[i]
 */
 //    Pred: e != null
 //    Post: n' == n + 1 & a'[n] == e && Statement(n)
    void enqueue(final Object e);

 //    Pred: n > 0
 //    Post: n' == n && R == a[0] && Statement(n)
    Object element();

 //    Pred: n > 0 
 //    Post: n' = n - 1 && R = a[0] && for i = 0 to n - 1: a'[i] == a[i + 1]
    Object dequeue();

 //    Pred: True
 //    Post: R == n && n' == n && Statement(n)
    int size();

 //    Pred: True
 //    Post: R == (n == 0) && n' == n && Statement(n)
    boolean isEmpty();

 //    Pred: True
 //    Post: n' = 0
    void clear();


    // Pred: e != null
    // Post: n' == n + 1 & a'[0] == e && for i = 1 to n: a'[i] == a[i - 1]
    void push(Object e);

    // Pred: n > 0
    // Post: n' == n && R == a[n - 1] && Statement(n)
    Object peek();

    // Pred: n > 0
    // Post: n' == n - 1 && R == a[n - 1] && Statement(n)
    Object remove();

    // Pred: 0 <= i < n
    // Post: n' == n && R == a[i] && Statement(n)
    Object get(int i);

    // Pred: 0 <= i < n && e != null
    // Post: n' == n && e == a[i] && for j in {0 .. n - 1}\{i}: a'[j] == a[j]
    void set(int i, Object e);



    //    Pred: n_arg > 0
    //    Post: n' == n && Statement(n) && R = a[1, 1+n_arg, 1+2n_arg ...]
    Queue getNth(int n);


    //    Pred: n_arg > 0
    //    Post: n' == n - n // n_arg && R = getNth(n_arg) && 
    //        a' = b.replace(null) && b.len = a.len &&
    //        for j in {0 .. n - 1}:
    //                  j % n_arg == narg - 1 -> b[j] = a[j]
    //                  j % n_arg != narg - 1 -> b[j] = null 
    Queue removeNth(int n);


    //    Pred: n_arg > 0
    //    Post: n' == n - n // n_arg &&
    //        a' = b.replace(null) && b.len = a.len &&
    //        for j in {0 .. n - 1}:
    //                  j % n_arg == narg - 1 -> b[j] = a[j]
    //                  j % n_arg != narg - 1 -> b[j] = null 
    void dropNth(int n); 
}
