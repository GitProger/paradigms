%% AVL-Tree
%% node(Left, Right, Diff=(H(L) - H(R)), Key, Value)

map_build([], null) :- !.
map_build([(K, V) | T], TreeMap) :- map_build(T, R), map_put(R, K, V, TreeMap).

map_get(node(_, _, _, Key, Value), Key, Value) :- !.
map_get(node(L, _, _, K, _), Key, Value) :- Key < K, map_get(L, Key, Value), !.
map_get(node(_, R, _, K, _), Key, Value) :- Key > K, map_get(R, Key, Value), !.

map_get_min(node(null, _, _, K, V), K, V).
map_get_min(node(L, _, _, _, _), K, V) :- map_get_min(L, K, V), !.

map_get_max(node(_, null, _, K, V), K, V).
map_get_max(node(_, R, _, _, _), K, V) :- map_get_max(R, K, V), !.

map_del_min(node(null, R, _, _, _), R).
map_del_min(node(L, R, D, K, V), node(L1, R, D1, K, V)) :- D1 is D - 1, map_del_min(L, L1), !.

map_del_max(node(L, null, _, _, _), L).
map_del_max(node(L, R, D, K, V), node(L, R1, D1, K, V)) :- D1 is D + 1, map_del_max(R, R1), !.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

rotate_left_small_(Da1, Da2, Db1, Db2, 
  node(P, node(Q, R, Db1, Kb, Vb), Da1, Ka, Va), 
  node(node(P, Q, Da2, Ka, Va), R, Db2, Kb, Vb)).
rotate_left_small(A, B) :- rotate_left_small_(-2, 0 , -1, 0, A, B), !.
rotate_left_small(A, B) :- rotate_left_small_(-2, -1,  0, 1, A, B), !.
rotate_left_small(A, A) :- !.

rotate_right_small_(Da1, Da2, Db1, Db2, 
  node(node(P, Q, Da1, Ka, Va), R, Db1, Kb, Vb),
  node(P, node(Q, R, Db2, Kb, Vb), Da2, Ka, Va)).
rotate_right_small(A, B) :- rotate_right_small_(2, 0, 1,  0, A, B), !.
rotate_right_small(A, B) :- rotate_right_small_(2, 1, 0, -1, A, B), !.
rotate_right_small(A, A) :- !.

rotate_left_big(node(L, R, D, K, V), B) :- rotate_right_small(R, R1), rotate_left_small(node(L, R1, D, K, V), B), !.
rotate_left_big(A, A) :- !.

rotate_right_big(node(L, R, D, K, V), B) :- rotate_left_small(L, L1), rotate_right_small(node(L1, R, D, K, V), B), !.
rotate_right_big(A, A) :- !.

balance(A, A). % 15.1
%balance(A, R) :- rotate_left_small(A, B), rotate_right_small(B, R). % 18.7
%balance(A, R) :- rotate_left_small(A, B), rotate_right_small(B, C), rotate_left_big(C, D), rotate_right_big(D, R). % 25.8

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

map_put(null, Key, Value, node(null, null, 0, Key, Value), 1) :- !.
map_put(node(L, R, Diff, Key, _), Key, Value, node(L, R, Diff, Key, Value), 0) :- !.
map_put(node(L, R, D, K, V), Key, Value, node(L1, R, D1, K, V), Added) :-
  Key < K,
  map_put(L, Key, Value, LX, Added),
  D1 is D + Added,
  balance(LX, L1), !.
map_put(node(L, R, D, K, V), Key, Value, node(L, R1, D1, K, V), Added) :- 
  Key > K,
  map_put(R, Key, Value, RX, Added),
  D1 is D - Added,
  balance(RX, R1), !.

map_put(Node, K, V, R) :- map_put(Node, K, V, R, _).

map_remove(null, _, null, 0) :- !.
map_remove(node(null, null, _, Key, _), Key, null, 1) :- !.
map_remove(node(L, R, D, K, V), Key, node(L1, R, D1, K, V), Removed) :- 
  Key < K, 
  map_remove(L, Key, LX, Removed), 
  D1 is D - Removed,
  balance(LX, L1), !.
map_remove(node(L, R, D, K, V), Key, node(L, R1, D1, K, V), Removed) :- 
  Key > K, 
  map_remove(R, Key, RX, Removed), 
  D1 is D + Removed, 
  balance(RX, R1), !.
map_remove(node(L, R, D, K, V), K, node(L, R1, D1, K1, V1), 1) :-
  D < 0, D1 is D + 1,
  map_get_min(R, K1, V1),
  map_del_min(R, R1), !.
map_remove(node(L, R, D, K, V), K, node(L1, R, D1, K1, V1), 1) :- 
  D >= 0, D1 is D - 1,
  map_get_max(L, K1, V1),
  map_del_max(L, L1), !.

map_remove(Node, K, R) :- map_remove(Node, K, R, _).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

map_getCeiling(node(_, _, _, K, V), K, V) :- !.
map_getCeiling(node(_, R, _, K, _), Key, Value) :- Key > K, map_getCeiling(R, Key, Value), !.
map_getCeiling(node(L, _, _, K, _), Key, Value) :- Key < K, map_getCeiling(L, Key, Value), !.
map_getCeiling(node(_, _, _, K, V), Key, Value) :- Key < K, Value = V.

map_putCeiling(node(L, R, D, K, _), K, V, node(L, R, D, K, V), true) :- !.
map_putCeiling(node(L, R, D, K, V), Key, Value, node(L1, R, D, K, V), true) :- Key < K, map_putCeiling(L, Key, Value, L1, true), !.
map_putCeiling(node(L, R, D, K, _), Key, Value, node(L, R, D, K, Value), true) :- Key < K.
map_putCeiling(node(L, R, D, K, V), Key, Value, node(L, R1, D, K, V), Put) :- Key > K, map_putCeiling(R, Key, Value, R1, Put).
map_putCeiling(node(L, null, D, K, V), Key, _, node(L, null, D, K, V), false) :- Key > K.

map_putCeiling(null, _, _, null).
map_putCeiling(T, K, V, R) :- map_putCeiling(T, K, V, R, _).
