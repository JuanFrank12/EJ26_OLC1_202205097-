BNF - Lenguaje GoLite
1. Programa principal
<programa> ::= <lista_declaraciones>

<lista_declaraciones> ::= <declaracion>
                         | <lista_declaraciones> <declaracion>

<declaracion> ::= <declaracion_struct>
                | <declaracion_funcion>
                | <declaracion_metodo>
                | <funcion_main>
                | <instruccion>
2. Función principal
<funcion_main> ::= "func" "main" "(" ")" <bloque>

La función main es el punto de entrada del programa. No debe recibir parámetros ni declarar tipo de retorno.

3. Bloques e instrucciones
<bloque> ::= "{" <lista_instrucciones> "}"

<lista_instrucciones> ::= <instruccion>
                         | <lista_instrucciones> <instruccion>
                         | ε

<instruccion> ::= <declaracion_variable>
                | <asignacion_variable>
                | <asignacion_compuesta>
                | <incremento_decremento>
                | <impresion>
                | <sentencia_if>
                | <sentencia_switch>
                | <sentencia_for>
                | <sentencia_for_range>
                | <sentencia_break>
                | <sentencia_continue>
                | <sentencia_return>
                | <llamada_funcion> ";"
                | <llamada_metodo> ";"
                | <asignacion_slice>
                | <asignacion_struct>
                | <bloque>
4. Tipos de dato
<tipo> ::= "int"
         | "float64"
         | "string"
         | "bool"
         | "rune"
         | <tipo_slice>
         | <identificador>

<tipo_slice> ::= "[" "]" <tipo>
5. Declaración y asignación de variables
<declaracion_variable> ::= <identificador> ":=" <expresion> ";"
                         | "var" <identificador> <tipo> ";"
                         | "var" <identificador> <tipo> "=" <expresion> ";"

<asignacion_variable> ::= <identificador> "=" <expresion> ";"

<asignacion_compuesta> ::= <identificador> "+=" <expresion> ";"
                         | <identificador> "-=" <expresion> ";"

<incremento_decremento> ::= <identificador> "++" ";"
                          | <identificador> "--" ";"
6. Impresión
<impresion> ::= "fmt.Println" "(" ")"
              | "fmt.Println" "(" <lista_expresiones> ")" ";"

<lista_expresiones> ::= <expresion>
                      | <lista_expresiones> "," <expresion>
7. Sentencia if / else if / else
<sentencia_if> ::= "if" <expresion> <bloque>
                 | "if" <expresion> <bloque> <lista_else_if>
                 | "if" <expresion> <bloque> <lista_else_if> <sentencia_else>
                 | "if" <expresion> <bloque> <sentencia_else>

<lista_else_if> ::= <sentencia_else_if>
                  | <lista_else_if> <sentencia_else_if>

<sentencia_else_if> ::= "else" "if" <expresion> <bloque>

<sentencia_else> ::= "else" <bloque>
8. Sentencia switch
<sentencia_switch> ::= "switch" <expresion> "{" <lista_cases> "}"
                     | "switch" <expresion> "{" <lista_cases> <default_case> "}"

<lista_cases> ::= <case>
                | <lista_cases> <case>

<case> ::= "case" <expresion> ":" <lista_instrucciones>

<default_case> ::= "default" ":" <lista_instrucciones>
9. Ciclos for
For tipo while
<sentencia_for> ::= "for" <expresion> <bloque>
For clásico
<sentencia_for> ::= "for" <for_init> ";" <expresion> ";" <for_update> <bloque>

<for_init> ::= <declaracion_variable_sin_punto_coma>
             | <asignacion_variable_sin_punto_coma>

<for_update> ::= <asignacion_variable_sin_punto_coma>
               | <asignacion_compuesta_sin_punto_coma>
               | <incremento_decremento_sin_punto_coma>
For range
<sentencia_for_range> ::= "for" <identificador> "," <identificador> ":=" "range" <expresion> <bloque>
10. Break, continue y return
<sentencia_break> ::= "break" ";"

<sentencia_continue> ::= "continue" ";"

<sentencia_return> ::= "return" ";"
                     | "return" <expresion> ";"
11. Funciones
<declaracion_funcion> ::= "func" <identificador> "(" ")" <bloque>
                        | "func" <identificador> "(" <lista_parametros> ")" <bloque>
                        | "func" <identificador> "(" ")" <tipo> <bloque>
                        | "func" <identificador> "(" <lista_parametros> ")" <tipo> <bloque>

<lista_parametros> ::= <parametro>
                     | <lista_parametros> "," <parametro>

<parametro> ::= <identificador> <tipo>

<llamada_funcion> ::= <identificador> "(" ")"
                    | <identificador> "(" <lista_expresiones> ")"
12. Structs
<declaracion_struct> ::= "type" <identificador> "struct" "{" <lista_atributos> "}"

<lista_atributos> ::= <atributo>
                    | <lista_atributos> <atributo>

<atributo> ::= <identificador> <tipo>

<literal_struct> ::= <identificador> "{" "}"
                   | <identificador> "{" <lista_inicializacion_atributos> "}"

<lista_inicializacion_atributos> ::= <inicializacion_atributo>
                                   | <lista_inicializacion_atributos> "," <inicializacion_atributo>

<inicializacion_atributo> ::= <identificador> ":" <expresion>

<acceso_struct> ::= <expresion> "." <identificador>

<asignacion_struct> ::= <expresion> "." <identificador> "=" <expresion> ";"
                      | <expresion> "." <identificador> "+=" <expresion> ";"
                      | <expresion> "." <identificador> "-=" <expresion> ";"
13. Métodos de structs
<declaracion_metodo> ::= "func" "(" <identificador> <identificador> ")" <identificador> "(" ")" <bloque>
                       | "func" "(" <identificador> <identificador> ")" <identificador> "(" <lista_parametros> ")" <bloque>
                       | "func" "(" <identificador> <identificador> ")" <identificador> "(" ")" <tipo> <bloque>
                       | "func" "(" <identificador> <identificador> ")" <identificador> "(" <lista_parametros> ")" <tipo> <bloque>

<llamada_metodo> ::= <expresion> "." <identificador> "(" ")"
                   | <expresion> "." <identificador> "(" <lista_expresiones> ")"
14. Slices
<literal_slice> ::= "[" "]" <tipo> "{" "}"
                  | "[" "]" <tipo> "{" <lista_expresiones> "}"
                  | "[" "]" <tipo> "{" <lista_expresiones> "," "}"

<acceso_slice> ::= <expresion> "[" <expresion> "]"

<asignacion_slice> ::= <expresion> "[" <expresion> "]" "=" <expresion> ";"

<funcion_append> ::= "append" "(" <expresion> "," <expresion> ")"

<funcion_slices_index> ::= "slices.Index" "(" <expresion> "," <expresion> ")"

<funcion_strings_join> ::= "strings.Join" "(" <expresion> "," <expresion> ")"
15. Funciones embebidas
<funcion_embebida> ::= "strconv.Atoi" "(" <expresion> ")"
                     | "strconv.ParseFloat" "(" <expresion> ")"
                     | "reflect.TypeOf" "(" <expresion> ")"
                     | "reflect.TypeOf" "(" <expresion> ")" "." "string" "(" ")"
                     | "len" "(" <expresion> ")"
                     | <funcion_append>
                     | <funcion_slices_index>
                     | <funcion_strings_join>
16. Expresiones
<expresion> ::= <literal>
              | <identificador>
              | <expresion> "+" <expresion>
              | <expresion> "-" <expresion>
              | <expresion> "*" <expresion>
              | <expresion> "/" <expresion>
              | <expresion> "%" <expresion>
              | "-" <expresion>
              | "!" <expresion>
              | <expresion> "==" <expresion>
              | <expresion> "!=" <expresion>
              | <expresion> ">" <expresion>
              | <expresion> ">=" <expresion>
              | <expresion> "<" <expresion>
              | <expresion> "<=" <expresion>
              | <expresion> "&&" <expresion>
              | <expresion> "||" <expresion>
              | "(" <expresion> ")"
              | <llamada_funcion>
              | <llamada_metodo>
              | <literal_slice>
              | <acceso_slice>
              | <literal_struct>
              | <acceso_struct>
              | <funcion_embebida>
17. Literales
<literal> ::= <entero>
            | <decimal>
            | <cadena>
            | <booleano>
            | <rune>
            | "nil"

<booleano> ::= "true"
             | "false"

<entero> ::= <digito>
           | <entero> <digito>

<decimal> ::= <entero> "." <entero>

<cadena> ::= "\"" <contenido_cadena> "\""

<rune> ::= "'" <caracter> "'"
18. Identificadores
<identificador> ::= <letra>
                  | <identificador> <letra>
                  | <identificador> <digito>
                  | <identificador> "_"

<letra> ::= "a" | "b" | "c" | ... | "z"
          | "A" | "B" | "C" | ... | "Z"
          | "_"

<digito> ::= "0" | "1" | "2" | "3" | "4"
           | "5" | "6" | "7" | "8" | "9"
19. Comentarios
<comentario_linea> ::= "//" <texto_hasta_fin_linea>

<comentario_multilinea> ::= "/*" <texto> "*/"

Los comentarios son ignorados durante el análisis léxico y no generan instrucciones dentro del AST.