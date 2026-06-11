package analisis;

//importaciones

import java.util.ArrayList;
import java.util.List;
import java_cup.runtime.Symbol;
import com.olc1.reports.TokenReport;
import com.olc1.reports.GoliteError;


%%

// Configuración de JFLEX
%cup //Indicamos que vamos a usar CUP
// Nombre de la clase del lexer
%class Lexer 
%public // Paquete del lexer
%line // conteo de lienas
%unicode
%column // conteo de columnas
//%8bit  // recibir caracteres en formato UTF-8
// %debug // Habilitar modo debug para ver el proceso de tokenización
//%ignorecase // ignorar mayusculas y minusculas
%state COMMENT


%{
    public final List<GoliteError> errors = new ArrayList<>();
    public final List<TokenReport> tokens = new ArrayList<>();

    private boolean canEndStatement = false;

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
            || type == sym.kwTrue
            || type == sym.kwFalse
            || type == sym.kwNil
            || type == sym.rparen
            || type == sym.kwBreak
            || type == sym.kwContinue;
    }
%}



%eofval{
    return new Symbol(sym.EOF, yyline, yycolumn, yytext());
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
<COMMENT>\r|\n|\r\n {
    /* ignorar saltos de línea */
}

// Comentario multilínea sin cerrar
<COMMENT><<EOF>> {
    errors.add(new GoliteError(
        "Lexico",
        "Comentario multilínea sin cerrar",
        yyline,
        yycolumn
    ));
    return new Symbol(sym.EOF, yyline, yycolumn, yytext());
}

// Numbers
{digit}+"."{digit}+ { return token(sym.decimal, "decimal"); }
{digit}+            { return token(sym.integer, "integer"); }

// Symbols
"("     { return token(sym.lparen, "lparen"); }
")"     { return token(sym.rparen, "rparen"); }
"{"     { return token(sym.lbrace, "lbrace"); }
"}"     {if (canEndStatement) {canEndStatement = false;yypushback(1);return new Symbol(sym.scol, yyline + 1, yycolumn + 1, ";");}

    return token(sym.rbrace, "rbrace");
}
","     { return token(sym.comma, "comma"); }
";"     { return token(sym.scol, "scol"); }

"++"    { return token(sym.increment, "increment"); }
"--"    { return token(sym.decrement, "decrement"); }

"+="    { return token(sym.plusAssign, "plusAssign"); }
"-="    { return token(sym.minusAssign, "minusAssign"); }

":="    { return token(sym.assign, "assign"); }
"="     { return token(sym.alllocate, "alllocate"); }

"=="    { return token(sym.equal, "equal"); }
"!="    { return token(sym.notEqual, "notEqual"); }
">="    { return token(sym.greaterEqual, "greaterEqual"); }
"<="    { return token(sym.lowerEqual, "lowerEqual"); }
">"     { return token(sym.greatertag, "greatertag"); }
"<"     { return token(sym.lowertag, "lowertag"); }

"&&"    { return token(sym.and, "and"); }
"||"    { return token(sym.or, "or"); }
"!"     { return token(sym.not, "not"); }

"+"     { return token(sym.plus, "plus"); }
"-"     { return token(sym.minus, "minus"); }
"*"     { return token(sym.times, "times"); }
"/"     { return token(sym.slash, "slash"); }
"%"     { return token(sym.mod, "mod"); }



// Key Words
"fmt.Println"         { return token(sym.fmtPrintln, "fmtPrintln"); }
"strconv.Atoi"       { return token(sym.fnAtoi, "fnAtoi"); }
"strconv.ParseFloat" { return token(sym.fnParseFloat, "fnParseFloat"); }
"reflect.TypeOf"     { return token(sym.fnTypeOf, "fnTypeOf"); }
".string"            { return token(sym.fnTypeString, "fnTypeString"); }
".String"            { return token(sym.fnTypeString, "fnTypeString"); }

"print"     { return token(sym.imprimir, "imprimir"); }
"true"      { return token(sym.kwTrue, "kwTrue"); }
"false"     { return token(sym.kwFalse, "kwFalse"); }
"if"        { return token(sym.kwIf, "kwIf"); }
"else"      { return token(sym.kwElse, "kwElse"); }
"for"       { return token(sym.kwFor, "kwFor"); }
"break"     { return token(sym.kwBreak, "kwBreak"); }
"continue"  { return token(sym.kwContinue, "kwContinue"); }
"nil"       { return token(sym.kwNil, "kwNil"); }
"var"       { return token(sym.kwVar, "kwVar"); }

"int"       { return token(sym.type, "type"); }
"float64"   { return token(sym.type, "type"); }
"string"    { return token(sym.type, "type"); }
"bool"      { return token(sym.type, "type"); }
"rune"      { return token(sym.type, "type"); }

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
{identifier} {
    return token(sym.id, "id");
}

// Saltos de línea: funcionan como fin de sentencia
{linebreak} {
    canEndStatement = false;
    return new Symbol(sym.scol, yyline + 1, yycolumn + 1, yytext());
}

// Ignorar espacios
{whitespace} {
    /* pass */
}

// Error
. {
    errors.add(new GoliteError(
    "Lexico",
    "Caracter no reconocido: " + yytext(),
    yyline + 1,
    yycolumn + 1
));
}