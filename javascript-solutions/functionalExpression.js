const cnst = value => () => value; // :NOTE: Number

// :NOTE: performance
const variable = vName => (x, y, z) => ({'x': x, 'y': y, 'z': z}[vName]);

const highOrderFnMaker = oper => (...exprs) => (...vars) => oper(...exprs.map(e => e(...vars)));

const add = highOrderFnMaker((a, b) => a + b);
const subtract = highOrderFnMaker((a, b) => a - b);
const multiply = highOrderFnMaker((a, b) => a * b);
const divide = highOrderFnMaker((a, b) => a / b);
const negate = highOrderFnMaker(x => -x);

const indexOfBy = f => (...arr) => arr.indexOf(f(...arr));
const argMin3 = highOrderFnMaker(indexOfBy(Math.min));
const argMax3 = highOrderFnMaker(indexOfBy(Math.max));
const argMin5 = argMin3;
const argMax5 = argMax3;

const one = cnst(1);
const two = cnst(2);
///////////////////////////////////////////////////////////////////////////////

const fun = {
    // :NOTE: .arity
    '+': {'nm': add, 'sz': 2}, 
    '-': {'nm': subtract, 'sz': 2}, 
    '*': {'nm': multiply, 'sz': 2}, 
    '/': {'nm': divide, 'sz': 2},
    'argMin3': {'nm': argMin3, 'sz': 3},
    'argMin5': {'nm': argMin5, 'sz': 5},
    'argMax3': {'nm': argMax3, 'sz': 3},
    'argMax5': {'nm': argMax5, 'sz': 5},
    'negate': {'nm': negate, 'sz': 1},
};

const consts = {'one': one, 'two': two};

const words = expr => expr.trim().split(/\s+/);
const isVar = c => c.match(/^[xyz]$/) !== null;
// :NOTE: 1.1
const isNum = c => c.match(/^-?\d+$/) !== null;
const isConst = c => c in consts;

const compose = (...f) => arg => f.reduceRight((acc, gCur) => gCur(acc), arg);

const parse = compose(
    a => a[0],
    tokens => tokens.reduce((x, y) => {
        if (isConst(y)) {
            x.push(consts[y]);
        } else if (isVar(y)) {
            x.push(variable(y));
        } else if (isNum(y)) {
            x.push(cnst(parseFloat(y)));
        } else {
            let args = x.splice(-fun[y].sz);
            x.push(fun[y].nm(...args));
        }
        return x;
    }, []),
    words
);


const test = (function () {
    const parabola = add(
        subtract(
            multiply(variable("x"), variable("x")),
            multiply(cnst(2), variable("x"))
        ),
        cnst(1)
    );
    return function () {
        for (let x = 0; x <= 10; x++) {
            println("for x = " + x + ": x^2-2x+1 equals " + parabola(x, 0, 0));
        }
    }
})();


test();
println(parse("x x 2 - * x * 1 +")(5, 0, 0));
