package analisis;

//importaciones
import java_cup.runtime.Symbol;

%%

// Codigo de usuario 

%{
    String cadena = "";
%}

%init{
    yyline =1;
    yycolumn =1;
%init}

//Configuracion de flex
%cup // se indica que vamos a usar cup 
%class Scanner
%public
%line  //LLeva el conteo de lineas 
%column //conteo de columnas 
%8bit //caracteres tipo UTF-8
//%debug // 
%ignorecase // ignora las mayusculas y minusculas 

%state CADENA

//SIMBOLOS DEL SISTEMA
PAR1= "("
PAR2 = ")"
FINCADENA = ";"
MAS = "+"
MENOS = "-"
MULTI ="*"
DIVI = "/"
BLANCOS = [\ \r\t\f\n]+
ENTERO = [0-9]+
DECIMAL =[0-9]+"."[0-9]+

//PALABRAS RESERVADAS

IMPRIMIR = "imprimir"


%%
// primero se ponen las reservadas <NOMBRE_TOKEN, LEXEMA>
<YYINITIAL> {IMPRIMIR}  {return new Symbol(sym.IMPRIMIR, yyline, yycolumn, yytext());}

<YYINITIAL> {DECIMAL}  {return new Symbol(sym.DECIMAL, yyline, yycolumn, yytext());}
<YYINITIAL> {ENTERO}  {return new Symbol(sym.ENTERO, yyline, yycolumn, yytext());}

//SIMBOLOS
<YYINITIAL> {FINCADENA}  {return new Symbol(sym.FINCADENA, yyline, yycolumn, yytext());}
<YYINITIAL> {MAS}  {return new Symbol(sym.MAS, yyline, yycolumn, yytext());}
<YYINITIAL> {MENOS}  {return new Symbol(sym.MENOS, yyline, yycolumn, yytext());}
<YYINITIAL> {MULTI}  {return new Symbol(sym.MULTI, yyline, yycolumn, yytext());}
<YYINITIAL> {DIVI}  {return new Symbol(sym.DIVI, yyline, yycolumn, yytext());}
<YYINITIAL> {PAR1}  {return new Symbol(sym.PAR1, yyline, yycolumn, yytext());}
<YYINITIAL> {PAR2}  {return new Symbol(sym.PAR2, yyline, yycolumn, yytext());}

<YYINITIAL> {BLANCOS}  {/*Ignorar*/}

// Moverme a estado cadena
<YYINITIAL> [\"]    {yybegin(CADENA); cadena=""; }

<CADENA> {
    [\"]    {String temporal = cadena; cadena = "";
            yybegin(YYINITIAL);
            return new Symbol(sym.CADENA, yyline, yycolumn, temporal);}
    [^\"]   {cadena+=yytext();}
}