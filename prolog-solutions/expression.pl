:- load_library('alice.tuprolog.lib.DCGLibrary').

assoc(K, [(K, V) | _], V) :- !.
assoc(K, [_ | T], V) :- assoc(K, T, V).

variable(Name, variable(Name)).
const(Value, const(Value)).
operation(op_add     , A, B, R) :- R is A + B.
operation(op_subtract, A, B, R) :- R is A - B.
operation(op_multiply, A, B, R) :- R is A * B.
operation(op_divide  , A, B, R) :- R is A / B.
operation(op_negate  , A,    R) :- R is -A.

%% MODIFICATION
to_bool(N, B) :- (N > 0, B is 1; B is 0).

operation(op_not, A, R) :- to_bool(A, AB), R is 1 - AB, !.
operation(op_and, A, B, R) :- to_bool(A, AB), to_bool(B, BB), R is AB * BB, !.
operation(op_or, A, B, R) :- to_bool(A, AB), to_bool(B, BB), RB is AB + BB, to_bool(RB, R), !.
operation(op_xor, A, B, R) :- to_bool(A, AB), to_bool(B, BB), RB is AB + BB, R is RB mod 2, !.
operation(op_impl, A, B, R) :- to_bool(A, AB), to_bool(B, BB), (AB =:= 1, BB is 0, R is 0; R is 1), !.
operation(op_iff, A, B, R) :- to_bool(A, AB), to_bool(B, BB), (AB =:= BB, R is 1; R is 0), !.
%% /MODIFICATION

evaluate(const(Value), _, Value).
evaluate(variable(Name), Variables, R) :- atom_chars(Name, [HeadChar | _]), assoc(HeadChar, Variables, R).
evaluate(operation(Oper, A), Variables, R) :- evaluate(A, Variables, AR), operation(Oper, AR, R).
evaluate(operation(Oper, A, B), Variables, R) :- evaluate(A, Variables, AR), evaluate(B, Variables, BR), operation(Oper, AR, BR, R).

infix_str(E, A) :- ground(E), phrase(expr_p(E), C), atom_chars(A, C).
infix_str(E, A) :-   atom(A), atom_chars(A, C), del_spaces(C, CClear), phrase(expr_p(E), CClear).


op_p(op_add) --> ['+'].
op_p(op_subtract) --> ['-'].
op_p(op_multiply) --> ['*'].
op_p(op_divide) --> ['/'].
op_p(op_negate, 'negate').

%% MODIFICATION
op_p(op_not, '!').
op_p(op_and, '&&').
op_p(op_or, '||').
op_p(op_xor, '^^').
op_p(op_impl, '->').
op_p(op_iff, '<->').
%% /MODIFICATION

op_p(Oper) --> { op_p(Oper, S), atom_chars(S, C) }, C.

op_p_one(Oper) --> { op_p(Oper, S), atom_chars(S, ['n', 'e', 'g', 'a', 't', 'e']) }, ['n', 'e', 'g', 'a', 't', 'e'].
op_p_one(Oper) --> { op_p(Oper, S), atom_chars(S, ['!']) }, ['!'].


nonvar(V, _) :- var(V).
nonvar(V, T) :- nonvar(V), call(T).

letter_p(C) --> { member(C, [x, y, z, 'X', 'Y', 'Z']) }, [C].
xyz_p([H]) --> letter_p(H).
xyz_p([H | T]) --> letter_p(H), xyz_p(T).

expr_p(variable(Name)) --> { nonvar(Name, atom_chars(Name, CS)) }, xyz_p(CS), { atom_chars(Name, CS) }.
expr_p(const(Value)) -->
  { nonvar(Value, number_chars(Value, Chars)) },
  numeric_p(Chars),
  { Chars = [_ | _], number_chars(Value, Chars) }.


digit_p(D) --> { member(D, ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.']) }, [D].
digits_p([H]) --> digit_p(H).
digits_p([H | T]) --> digit_p(H), digits_p(T).


numeric_p(Chars) --> digits_p(Chars).
numeric_p(['-' | Abs]) --> ['-'], digits_p(Abs).

expr_p(operation(Op, A, B)) -->
  { var(Op) -> Gap = []; Gap = [' '] },
  ['('], expr_p(A), Gap, op_p(Op), Gap, expr_p(B), [')'].
expr_p(operation(Op, A)) --> 
  { var(Op) -> Ws = []; Ws = [' '] }, 
  op_p_one(Op), Ws, expr_p(A).

del_spaces([], []).
del_spaces([' ' | T], RT) :- !, del_spaces(T, RT).
del_spaces([H | T], [H | RT]) :- del_spaces(T, RT).
