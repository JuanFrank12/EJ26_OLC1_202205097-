package analisis;

// importaciones
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java_cup.runtime.Symbol;
import com.olc1.reports.TokenReport;
import com.olc1.reports.GoliteError;

%%

// Configuración de JFLEX
%cup
%class Lexer
%public
%line
%unicode
%column
%state COMMENT

%{
    public final List<GoliteError> errors = new ArrayList<>();
    public final List<TokenReport> tokens = new ArrayList<>();

    private boolean canEndStatement = false;
    private boolean eofScolEmitted = false;
    private boolean waitingIfBlock = false;
    private boolean waitingSwitchBlock = false;
    private boolean waitingForBlock = false;

    private final Stack<Boolean> braceLiteralStack = new Stack<>();

    private Symbol token(int type, String tokenType) {
        tokens.add(new TokenReport(yytext(), tokenType, yyline + 1, yycolumn + 1));
        canEndStatement = endsStatement(type);
        return new Symbol(type, yyline + 1, yycolumn + 1, yytext());
    }

   private boolean endsStatement(int type) {
    return type == sym.id
        || type == sym.integer
        || type == sym.decimal
        || type == sym.string
        || type == sym.rune
        || type == sym.type
        || type == sym.kwTrue
        || type == sym.kwFalse
        || type == sym.kwNil
        || type == sym.kwReturn
        || type == sym.rparen
        || type == sym.rbracket
        || type == sym.kwBreak
        || type == sym.kwContinue;
}
%}

%eofval{
    if (canEndStatement && !eofScolEmitted) {
        eofScolEmitted = true;
        canEndStatement = false;
        return new Symbol(sym.scol, yyline + 1, yycolumn + 1, ";");
    }

    return new Symbol(sym.EOF, yyline + 1, yycolumn + 1, yytext());
%eofval}

// Definición de patrones léxicos
digit = [0-9]
letter = [a-zA-Z]
whitespace = [\ \t\f]+
linebreak = \r\n|\r|\n

escape_char = \\[\"\\nrt]
normal_char = [^\"\\\n\r]
str_lex = ({normal_char} | {escape_char})*

identifier_start = ({letter}|_)
identifier_char = ({letter}|{digit}|_)
identifier = {identifier_start}{identifier_char}*
invalid_id_number = {digit}+{identifier_start}{identifier_char}*

rune_char = [^\'\\\n\r]
rune_escape = \\[\'\\nrt]
rune_lex = \'({rune_char}|{rune_escape})\'

%%

// Comentario de una línea
"//"[^\r\n]* {
    /* ignorar comentario de una línea */
}

// Inicio comentario multilínea
"/*" {
    yybegin(COMMENT);
}

// Fin comentario multilínea
<COMMENT>"*/" {
    yybegin(YYINITIAL);
}

// Contenido comentario multilínea
<COMMENT>[^*\r\n]+ {
    /* ignorar contenido */
}

// Asteriscos dentro del comentario
<COMMENT>"*" {
    /* ignorar */
}

// Saltos de línea dentro del comentario
<COMMENT>\r\n|\r|\n {
    /* ignorar saltos de línea dentro del comentario */
}

// Comentario multilínea sin cerrar
<COMMENT><<EOF>> {
    errors.add(new GoliteError(
        "Lexico",
        "Comentario multilínea sin cerrar",
        yyline + 1,
        yycolumn + 1
    ));
    return new Symbol(sym.EOF, yyline + 1, yycolumn + 1, yytext());
}

// Numbers
{digit}+"."{digit}+ {
    return token(sym.decimal, "decimal");
}

{digit}+ {
    return token(sym.integer, "integer");
}

// Symbols
"(" { return token(sym.lparen, "lparen"); }
")" { return token(sym.rparen, "rparen"); }
"{" {
    if (waitingIfBlock) {
        waitingIfBlock = false;
        braceLiteralStack.push(false);
        return token(sym.ifLbrace, "ifLbrace");
    }

    if (waitingSwitchBlock) {
        waitingSwitchBlock = false;
        braceLiteralStack.push(false);
        return token(sym.switchLbrace, "switchLbrace");
    }

    if (waitingForBlock) {
        waitingForBlock = false;
        braceLiteralStack.push(false);
        return token(sym.forLbrace, "forLbrace");
    }

    boolean isLiteralBrace = canEndStatement;
    braceLiteralStack.push(isLiteralBrace);

    return token(sym.lbrace, "lbrace");
}
"}" {
    if (canEndStatement) {
        canEndStatement = false;
        yypushback(1);
        return new Symbol(sym.scol, yyline + 1, yycolumn + 1, ";");
    }

    boolean closesLiteral = false;

    if (!braceLiteralStack.empty()) {
        closesLiteral = braceLiteralStack.pop();
    }

    tokens.add(new TokenReport(yytext(), "rbrace", yyline + 1, yycolumn + 1));
    canEndStatement = closesLiteral;

    return new Symbol(sym.rbrace, yyline + 1, yycolumn + 1, yytext());
}
"[" { return token(sym.lbracket, "lbracket"); }
"]" { return token(sym.rbracket, "rbracket"); }
"," {return token(sym.comma, "comma");}
";" {return token(sym.scol, "scol");}

"++" {
    return token(sym.increment, "increment");
}

"--" {
    return token(sym.decrement, "decrement");
}

"+=" {
    return token(sym.plusAssign, "plusAssign");
}

"-=" {
    return token(sym.minusAssign, "minusAssign");
}

":=" {
    return token(sym.assign, "assign");
}

":" {
    return token(sym.colon, "colon");
}

"=" {
    return token(sym.alllocate, "alllocate");
}

"==" {
    return token(sym.equal, "equal");
}

"!=" {
    return token(sym.notEqual, "notEqual");
}

">=" {
    return token(sym.greaterEqual, "greaterEqual");
}

"<=" {
    return token(sym.lowerEqual, "lowerEqual");
}

">" {
    return token(sym.greatertag, "greatertag");
}

"<" {
    return token(sym.lowertag, "lowertag");
}

"&&" {
    return token(sym.and, "and");
}

"||" {
    return token(sym.or, "or");
}

"!" {
    return token(sym.not, "not");
}

"+" {
    return token(sym.plus, "plus");
}

"-" {
    return token(sym.minus, "minus");
}

"*" {
    return token(sym.times, "times");
}

"/" {
    return token(sym.slash, "slash");
}
"." {
    return token(sym.dot, "dot");
}


"%" {
    return token(sym.mod, "mod");
}

// Key Words / funciones embebidas
"fmt.Println" {
    return token(sym.fmtPrintln, "fmtPrintln");
}

"strconv.Atoi" {
    return token(sym.fnAtoi, "fnAtoi");
}

"strconv.ParseFloat" {
    return token(sym.fnParseFloat, "fnParseFloat");
}

"reflect.TypeOf" {
    return token(sym.fnTypeOf, "fnTypeOf");
}

"len" {
    return token(sym.fnLen, "fnLen");
}

"append" {
    return token(sym.fnAppend, "fnAppend");
}

"slices.Index" {
    return token(sym.fnSlicesIndex, "fnSlicesIndex");
}

"strings.Join" {
    return token(sym.fnStringsJoin, "fnStringsJoin");
}

".string" {
    return token(sym.fnTypeString, "fnTypeString");
}

".String" {
    return token(sym.fnTypeString, "fnTypeString");
}

// Palabras reservadas
"print" {
    return token(sym.imprimir, "imprimir");
}

"true" {
    return token(sym.kwTrue, "kwTrue");
}

"false" {
    return token(sym.kwFalse, "kwFalse");
}

"if" {
    waitingIfBlock = true;
    return token(sym.kwIf, "kwIf");
}

"else" {
    return token(sym.kwElse, "kwElse");
}

"for" {
    waitingForBlock = true;
    return token(sym.kwFor, "kwFor");
}
"range" {
    return token(sym.kwRange, "kwRange");
}

"break" {
    return token(sym.kwBreak, "kwBreak");
}

"continue" {
    return token(sym.kwContinue, "kwContinue");
}

"return" {
    return token(sym.kwReturn, "kwReturn");
}

"nil" {
    return token(sym.kwNil, "kwNil");
}

"func" {
    return token(sym.kwFunc, "kwFunc");
}

"var" {
    return token(sym.kwVar, "kwVar");
}
"type" {
    return token(sym.kwType, "kwType");
}

"struct" {
    return token(sym.kwStruct, "kwStruct");
}





// Tipos primitivos
"int" {
    return token(sym.type, "type");
}

"float64" {
    return token(sym.type, "type");
}

"string" {
    return token(sym.type, "type");
}

"bool" {
    return token(sym.type, "type");
}

"rune" {
    return token(sym.type, "type");
}

// String
\"{str_lex}\" {
    return token(sym.string, "string");
}

// Rune / char
{rune_lex} {
    return token(sym.rune, "rune");
}

// Identificador inválido: empieza con número
{invalid_id_number} {
    errors.add(new GoliteError(
        "Lexico",
        "Identificador no valido, no puede iniciar con numero: " + yytext(),
        yyline + 1,
        yycolumn + 1
    ));
}

// ID válido
"switch" {
    waitingSwitchBlock = true;
    return token(sym.kwSwitch, "kwSwitch");
}

"case" {
    return token(sym.kwCase, "kwCase");
}

"default" {
    return token(sym.kwDefault, "kwDefault");
}


{identifier} {
    return token(sym.id, "id");
}

// Saltos de línea: funcionan como fin de sentencia
{linebreak} {
    if (canEndStatement) {
        canEndStatement = false;
        return new Symbol(sym.scol, yyline + 1, yycolumn + 1, ";");
    }

    /* ignorar saltos que no terminan sentencia */
}

// Ignorar espacios
{whitespace} {
    /* pass */
}

// Error léxico
. {
    errors.add(new GoliteError(
        "Lexico",
        "Caracter no reconocido: " + yytext(),
        yyline + 1,
        yycolumn + 1
    ));
}