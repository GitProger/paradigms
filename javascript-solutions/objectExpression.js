function Operation(oper, fun, difRule, ...exprs) {
    this.oper = () => oper;
    this.evaluate = (...args) => fun(...exprs.map(e => e.evaluate(...args)));
    this.diff = difRule;
    this.toString = () => exprs.map(op => op.toString()).join(" ") + " " + oper;
    this.prefix = () => "(" + oper + " " + exprs.map(op => op.prefix()).join(" ") + ")";
    this.postfix = () => "(" + exprs.map(op => op.postfix()).join(" ") + " " + oper + ")";
}

function Const(val) {
    let cVal = Number(val);
    Operation.call(this, "const",
        null,
        () => new Const(0))
    this.evaluate = () => cVal;
    this.toString = () => "" + cVal;
    this.prefix = () => "" + cVal;
    this.postfix = () => "" + cVal;
}
Const.arity = 0;

function Variable(name) {
    Operation.call(this, "var",
        null,
        v => new Const(Number(v === name)))
    this.evaluate = (x, y, z) => Number(({"x": x, "y": y, "z": z})[name]);
    this.toString = () => name;
    this.prefix = () => name;
    this.postfix = () => name;
}
Variable.arity = 0;

function Add(...exprs) {
    Operation.call(this, "+",
        (a, b) => a + b, 
        v => new Add(...exprs.map(e => e.diff(v))),
        ...exprs);
}
Add.arity = 2;

function Subtract(...exprs) {
    Operation.call(this, "-",
        (a, b) => a - b, 
        v => new Subtract(...exprs.map(e => e.diff(v))), 
        ...exprs);
}
Subtract.arity = 2;

function Multiply(...exprs) { // (vu)' = uv' + u'v
    Operation.call(this, "*",
        (a, b) => a * b, 
        v => new Add(new Multiply(exprs[0], exprs[1].diff(v)), new Multiply(exprs[0].diff(v), exprs[1])), 
        ...exprs);
}
Multiply.arity = 2;

function Divide(...exprs) { // (u/v)' = (u'v - uv') / v^2
    Operation.call(this, "/",
        (a, b) => a / b, 
        v => new Divide(
            new Subtract(
                new Multiply(exprs[0].diff(v), exprs[1]),
                new Multiply(exprs[0], exprs[1].diff(v))
            ),
            new Multiply(exprs[1], exprs[1])
        ),
        ...exprs);
}
Divide.arity = 2;

function Negate(...exprs) {
    Operation.call(this, "negate",
        a => -a, 
        v => new Negate(...exprs.map(e => e.diff(v))), 
        ...exprs);
}
Negate.arity = 1;

function Sqrt(...exprs) {
    Operation.call(this, "sqrt",
        a => Math.sqrt(a), 
        v => new Multiply(new Divide(new Const(1/2), this), exprs[0].diff(v)),
        ...exprs);
}
Sqrt.arity = 1;

function SumrecN(n, ...exprs) {
    Operation.call(this, "sumrec" + n,
        (...args) => args.reduce((acc, cur) => acc + 1 / cur, 0),
        v => exprs.map(e => new Divide(new Const(1), e)).reduce((acc, cur) => new Add(acc, cur)).diff(v),
        ...exprs);
}

function HMeanN(n, ...exprs) {
    Operation.call(this, "hmean" + n,
        (...args) => n / args.reduce((acc, cur) => acc + 1 / cur, 0),
        v => new Divide(new Const(n), new SumrecN(n, ...exprs)).diff(v),
        ...exprs);
}

const meansq = (...args) => args.reduce((acc, cur) => acc + cur * cur, 0) / args.length;

function Meansq(...exprs) {
    Operation.call(this, "meansq",
        meansq,
        v => new Divide(
                 exprs.map(e => new Multiply(e, e))
                      .reduce((acc, cur) => new Add(acc, cur)),
                 new Const(exprs.length)).diff(v),
        ...exprs);
}
Meansq.arity = -1;

function RMS(...exprs) {
    Operation.call(this, "rms",
        (...args) => Math.sqrt(meansq(...args)),
        v => new Sqrt(new Meansq(...exprs)).diff(v),
        ...exprs);
}
RMS.arity = -1;

const fun = {
    '+': Add,
    '-': Subtract,
    '*': Multiply,
    '/': Divide,
    'negate': Negate,
    'meansq': Meansq,
    'rms': RMS,
};

globalThis = (typeof globalThis === 'undefined' ? (() => this)() : globalThis)

for (let meta = 2; meta <= 5; meta++) {
    fun[`sumrec${meta}`] = globalThis[`Sumrec${meta}`] = function (...exprs) { SumrecN.call(this, meta, ...exprs) };
    fun[`hmean${meta}`]  = globalThis[`HMean${meta}`] = function (...exprs) { HMeanN.call(this, meta, ...exprs) };
    globalThis[`Sumrec${meta}`].arity = meta;
    globalThis[`HMean${meta}`].arity = meta;
}


const words = expr => expr.trim().split(/\s+/);
const isVar = c => c.match(/^[xyz]$/) !== null;
const isNum = c => c.match(/^-?\d+$/) !== null;
const compose = (...f) => arg => f.reduceRight((acc, gCur) => gCur(acc), arg);
const parse = compose(tokens => {
    let st = [];
    for (let tok of tokens) {
        if (isVar(tok)) {
            st.push(new Variable(tok));
        } else if (isNum(tok)) {
            st.push(new Const(tok));
        } else {
            let args = st.splice(-fun[tok].arity);
            st.push(new fun[tok](...args));
        }
    }
    return st.pop();
}, words);

// ============================================================================
// ================================== PARSER ==================================
// ============================================================================

const generateException = name => {
    eval(`globalThis[name] = function ${name}(message) {
        Error.call(this, message);
        this.message = message;
    }`);
    globalThis[name].prototype = Object.create(Error.prototype);
    globalThis[name].prototype.name = name;
    globalThis[name].prototype.constructor = globalThis[name];
}

for (let e of ['BraceError', 'UnknownTokenError', 'InvalidOperationError', 'WrongArgumentCountError']) {
    generateException(e)
}

const tokenizeSExpr = s => s.split(/(\(|\s+|\))/).filter(e => e.trim().length > 0);

function closeOpeningBrace(tokens, s, e) {
    let b = 0;
    for (let i = s; i < e; i++) {
        if (tokens[i] === '(') { b++ }
        else if (tokens[i] === ')') { b-- }
        if (b === 0) {
            return i;
        }
        if (b < 0) {
            throw new BraceError('Too many ")"');
        }
    }
    throw new BraceError('")" expected');
}

function parseNExprs(cnt, tokens, s, e, ParticularSExpParser) {
    let exprs = [];
    let vararg = (cnt < 0);
    while (cnt-- > 0 || (vararg && s !== e)) {
        if (s >= e) {
            throw new WrongArgumentCountError("Too few arguments");
        }
        let x = closeOpeningBrace(tokens, s, e) + 1;
        exprs.push(ParticularSExpParser(tokens, s, x));
        s = x;
    }
    if (!vararg && s !== e) {
        throw new WrongArgumentCountError("Too many arguments");
    }
    return exprs;
}


const genParser = bodyFunction => function (tokens, s = 0, e = tokens.length) {
    if (tokens[s] === '(') {
        if (tokens[e - 1] !== ')') {
            throw new BraceError('")" expected');
        }

        return bodyFunction(tokens, s, e);
    } else if (s + 1 === e) {
        let t = tokens[s];
        if (isVar(t)) {
            return new Variable(t);
        } else if (isNum(t)) {
            return new Const(t);
        } else {
            throw new UnknownTokenError(`Wrong constant or variable name '${tokens[s]}' (acceptable: x,y,z)`);
        }
    } else if (s >= e) {
        throw new UnknownTokenError(`Not a token: '${tokens.slice(s, e).join(' ')}'`);
    } else {
        throw new InvalidOperationError('"(" expected');
    }
}



const parseTokensPrefix = genParser((tokens, s, e) => {
    let func = fun[tokens[s + 1]];
    if (func === undefined) {
        throw new InvalidOperationError(`Unknown operation '${tokens[s + 1]}'`);
    }
    return new func(...parseNExprs(func.arity, tokens, s + 2, e - 1, parseTokensPrefix));
});

const parseTokensPostfix = genParser((tokens, s, e) => {
    let func = fun[tokens[e - 2]];
    if (func === undefined) {
        throw new InvalidOperationError(`Unknown operation '${tokens[e - 2]}'`);
    }
    return new func(...parseNExprs(func.arity, tokens, s + 1, e - 2, parseTokensPostfix));
});

const parsePrefix = compose(parseTokensPrefix, tokenizeSExpr);
const parsePostfix = compose(parseTokensPostfix, tokenizeSExpr);
