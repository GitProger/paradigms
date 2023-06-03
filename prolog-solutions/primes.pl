prime(N) :- \+ composite(N).

init(LIMIT) :- 
  exclude_composite_for(2, 4, LIMIT),
  Root is floor(sqrt(LIMIT)),
  eratosthenes_for(3, Root, LIMIT), !.


eratosthenes_for(Cur, Begin, _) :- Cur > Begin, !.
eratosthenes_for(Cur, Begin, LIMIT) :- 
  composite(Cur), !,
  Next is Cur + 2, eratosthenes_for(Next, Begin, LIMIT).
eratosthenes_for(Cur, Begin, LIMIT) :-
  NextSq is Cur * Cur, 
  exclude_composite_for(Cur, NextSq, LIMIT),
  Next is Cur + 2,
  eratosthenes_for(Next, Begin, LIMIT).

exclude_composite_for(_, Cur, End) :- Cur > End, !.
exclude_composite_for(Step, Cur, End) :- 
  assert(composite(Cur)),
  Next is Cur + Step, exclude_composite_for(Step, Next, End).

is_decomposition(R, [H1 | [H2 | T]]) :- 
  H2 >= H1, 
  prime(H1),
  prime_divisors(R1, [H2 | T]),
  R is R1 * H1.

prime_divisors(1, []) :- !.
prime_divisors(N, [N]) :- prime(N), !. % if N is not spec, decompose does not work
prime_divisors(N, L) :- integer(N), decompose(N, L, 2), !. % N spec, L unspec
prime_divisors(N, L) :- is_decomposition(N, L).            % N unspec, L spec


div_for(N, End, End) :- 0 is N mod End, !.
div_for(N, Cur, End) :- Next is Cur + 1, div_for(N, Next, End).

decompose(N, [N], _) :- prime(N), !.
decompose(N, [H | T], Cur) :-
  div_for(N, Cur, H),
  N1 is floor(N / H),
  decompose(N1, T, H).


split([A], [A], []) :- !.
split([H1, H2 | L], [H1], [H2 | L]) :- H1 =\= H2, !.
split([H, H | L], [H | R1], R2) :- split([H | L], R1, R2), !. 
%% split([H1, H2 | L], [H1 | R1], R2) :- H1 is H2, split([H2 | L], R1, R2), !.

is_image([H | T], (H, B)) :- length([H | T], B), split([H | T], _, []), !.
%% is_image([H | T], (A, B)) :- A is H, length([H | T], B), split([H | T], _, []), !.


fold_div(L, [R]) :- 
  is_image(L, R), !.
fold_div(L, [HR | TR]) :- 
  split(L, L1, L2), 
  is_image(L1, HR), fold_div(L2, TR).

compact_prime_divisors(N, R) :- prime_divisors(N, L), fold_div(L, R).

comb_((A, B), PS, R, V) :-
  B > 0, B1 is B - 1,
  comb([(A, B1) | PS], R, V).

comb([(A, B) | PS], [A | R], 1) :- comb_((A, B), PS, R, 1).
comb([H | PS], R, _) :- comb_(H, PS, R, 0).
comb([(_, 0) | PS], R, _) :- comb(PS, R, 1).
comb([], [], _) :- !.

%% comb([(A, B) | PS], [A | R], 1) :- 
%%   B > 0, 
%%   B1 is B - 1,
%%   comb([(A, B1) | PS], R, 1).
%% comb([(A, B) | PS], R, _) :- 
%%   B > 0, 
%%   B1 is B - 1, 
%%   comb([(A, B1) | PS], R, 0).
%% comb([(_, 0) | PS], R, _) :- comb(PS, R, 1).
%% comb([], [], _) :- !.

divisors_divisors(1, [[]]) :- !.
divisors_divisors(N, R) :-
  compact_prime_divisors(N, PS),
  findall(SubR, comb(PS, SubR, 1), R).
