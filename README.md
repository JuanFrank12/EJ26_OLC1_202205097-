# EJ26_OLC1_202205097-
GoLite - Proyecto OLC1
Información general

Curso: Organización de Lenguajes y Compiladores 1
Proyecto: GoLite
Fase: Proyecto Fase 2
Lenguaje de implementación: Java
Herramientas utilizadas: JFlex, CUP, Maven y Swing

GoLite es un intérprete desarrollado en Java para un lenguaje inspirado en Go. El proyecto incluye análisis léxico, análisis sintáctico, construcción de AST, interpretación de instrucciones, manejo de errores, tabla de símbolos, reporte de tokens y visualización del AST.

Autor

Nombre: Juan Francisco Ramirez de Paz 
Carné: 202205097

Características implementadas

El intérprete GoLite cuenta con las siguientes funcionalidades:

Interfaz gráfica en Java Swing.
Editor de código integrado.
Consola de salida.
Carga de archivos .glt.
Guardado de archivos .glt.
Reporte de errores léxicos, sintácticos y semánticos.
Reporte de tokens.
Reporte de tabla de símbolos.
Reporte gráfico del AST usando Graphviz.
Ejecución desde la función main.
Manejo de ámbitos.
Tipado estático.
Soporte para variables, funciones, structs, métodos y slices.
Tipos de datos soportados

GoLite soporta los siguientes tipos de datos:

int
float64
string
bool
rune
nil
slices
structs
Instrucciones soportadas

El lenguaje soporta las siguientes instrucciones:

Declaración de variables
Asignación de variables
Asignaciones compuestas += y -=
Incremento y decremento ++ y --
Bloques de instrucciones
if / else if / else
switch / case / default
for tipo while
for clásico
for range
break
continue
return
fmt.Println
Funciones soportadas
Funciones de usuario

El lenguaje permite declarar funciones con o sin parámetros y con o sin tipo de retorno.

Ejemplo:

func sumar(a int, b int) int {
    return a + b
}
Métodos de structs

También se permite declarar métodos asociados a structs.

Ejemplo:

type Persona struct {
    nombre string
    edad int
}

func (p Persona) saludar() {
    fmt.Println("Hola", p.nombre)
}
Funciones embebidas

Se implementaron las siguientes funciones embebidas:

fmt.Println
strconv.Atoi
strconv.ParseFloat
reflect.TypeOf
reflect.TypeOf(...).string()
len
append
slices.Index
strings.Join
Reportes implementados
Reporte de errores

Muestra los errores detectados durante la ejecución del archivo:

No.
Descripción
Línea
Columna
Tipo

Los errores pueden ser:

Léxicos
Sintácticos
Semánticos
Reporte de tokens

Muestra los tokens reconocidos por el analizador léxico:

No.
Lexema
Token
Línea
Columna
Tabla de símbolos

Muestra los símbolos encontrados durante la interpretación:

ID
Tipo símbolo
Tipo dato
Ámbito
Línea
Columna

Ejemplos de símbolos:

Variable
Función
Procedimiento
Parámetro
Struct
Atributo
Método
Receiver
Reporte AST

El AST se genera como un árbol gráfico utilizando Graphviz.
Este reporte permite visualizar la estructura sintáctica del programa ejecutado.

Requisitos del sistema

Para compilar y ejecutar el proyecto se necesita:

Java JDK 21
Maven
JFlex
CUP
Graphviz

Graphviz es necesario para visualizar el reporte AST como imagen.

Compilación del proyecto

Desde la raíz del repositorio se puede compilar el proyecto con Maven:

mvn clean compile -f "golite/pom.xml"

O usando la ruta absoluta del proyecto:

mvn clean compile -f "c:\Users\jfgho\OneDrive\Escritorio\Github\EJ26_OLC1_202205097-\golite\pom.xml"
Ejecución del proyecto

Para ejecutar el proyecto desde Maven:

mvn exec:java -f "golite/pom.xml"

También se puede abrir el proyecto desde un IDE como Visual Studio Code y ejecutar la clase principal del programa.

Ejemplo de código GoLite
type Persona struct {
    nombre string
    edad int
}

func mayorDeEdad(nombre string, edad int) bool {
    if edad >= 18 {
        fmt.Println("Mayor:", nombre)
        return true
    } else {
        fmt.Println("Menor:", nombre)
        return false
    }
}

func sumar(a int, b int) int {
    return a + b
}

func main() {
    carlos := "Carlos"
    ana := "Ana"

    edadCarlos := 20
    edadAna := 16

    fmt.Println(mayorDeEdad(carlos, edadCarlos))
    fmt.Println(mayorDeEdad(ana, edadAna))

    resultado := sumar(5, 5)
    fmt.Println(resultado)

    if resultado > 5 {
        fmt.Println("Resultado mayor que 5")
    }
}

Salida esperada:

Mayor: Carlos
true
Menor: Ana
false
10
Resultado mayor que 5
Estructura general del proyecto
golite/
├── pom.xml
├── src/
│   └── main/
│       ├── cup/
│       ├── jflex/
│       └── java/
│           └── com/
│               └── olc1/
│                   ├── ast/
│                   ├── reports/
│                   ├── views/
│                   └── visitor/
Reporte AST con Graphviz

Para que el reporte AST pueda generarse como imagen, Graphviz debe estar instalado y disponible en el sistema.

En Windows se puede instalar con:

winget install Graphviz.Graphviz

Para verificar la instalación:

dot -V

Si el comando no se reconoce, se puede agregar manualmente esta ruta al PATH:

C:\Program Files\Graphviz\bin
Consideraciones semánticas

El intérprete valida errores como:

Variables no declaradas
Variables duplicadas en el mismo ámbito
Tipos incompatibles
División entre cero
Módulo entre cero
Uso inválido de nil
break fuera de ciclos o switch
continue fuera de ciclos
Funciones no declaradas
Cantidad incorrecta de parámetros
Tipos incorrectos en parámetros
Retornos incorrectos
Structs vacíos
Atributos inexistentes
Métodos inexistentes
Índices fuera de rango en slices
Índices no enteros en slices
Estado del proyecto

El proyecto cuenta con un intérprete funcional para el lenguaje GoLite, incluyendo análisis léxico, sintáctico, semántico, ejecución de instrucciones, manejo de errores y generación de reportes requeridos.