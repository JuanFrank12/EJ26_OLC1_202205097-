# EJ26_OLC1_202205097-
Proyecto del laboratorio de OLC1
GoLite es un intérprete desarrollado en Java para el curso Organización de Lenguajes y Compiladores 1
El intérprete permite trabajar con variables, tipos estáticos, expresiones aritméticas, operadores lógicos, estructuras de control, funciones embebidas y reportes generales del análisis.

Funcionalidades implementadas

El proyecto incluye soporte para:

Identificadores
Sensibilidad entre mayúsculas y minúsculas
Comentarios de una línea y multilínea
Tipos estáticos
Valor nulo nil
Bloques de sentencias
Signos de agrupación
Declaración y asignación de variables
Operadores aritméticos: suma, resta, multiplicación, división y módulo
Operadores de asignación: += y -=
Negación unaria
Operadores de comparación
Operadores lógicos
Precedencia y asociatividad de operadores
Sentencias if, else if y else
Sentencia for
Sentencias de transferencia break y continue
Funciones embebidas:
fmt.Println
strconv.Atoi
strconv.ParseFloat
reflect.TypeOf(...).string()
Reporte de errores
Tabla de tokens
Tecnologías utilizadas
Java
Java Swing
Maven
JFlex
CUP
Requisitos para ejecutar el proyecto

Para compilar y ejecutar el proyecto se necesita tener instalado:

Java JDK 21 o superior
Apache Maven
Visual Studio Code, IntelliJ IDEA, NetBeans o cualquier IDE compatible con Java
Compilación del proyecto

Primero se debe abrir una terminal dentro de la carpeta raíz del proyecto.

Luego ejecutar el siguiente comando:

mvn clean compile

Este comando genera nuevamente los archivos del analizador léxico y sintáctico, y compila todo el código fuente del proyecto.