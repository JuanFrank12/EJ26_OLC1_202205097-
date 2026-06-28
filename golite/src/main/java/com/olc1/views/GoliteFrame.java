package com.olc1.views;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.File;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.util.ArrayList;
import java.util.List;

import analisis.Lexer;
import analisis.parser;

import com.olc1.ast.ASTNODE;
import com.olc1.ast.stm.Statments;
import com.olc1.reports.GoliteError;
import com.olc1.reports.TokenReport;
import com.olc1.reports.SymbolReport;
import com.olc1.visitor.interpreter.InterpreterVisitor;

public class GoliteFrame extends JFrame {
    private final EditorPanel editorPanel;
    private final JTextArea consoleTextArea;

    private Lexer lexer;
    private parser parser;
    InterpreterVisitor interpreter;

    private File currentFile = null;

    // Lista actual para el reporte de tabla de símbolos
    private List<SymbolReport> currentSymbols = new ArrayList<>();

    public GoliteFrame() {
        setTitle("Golite");
        setMinimumSize(new Dimension(600, 400));
        setSize(new Dimension(1200, 675));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        editorPanel = new EditorPanel();
        consoleTextArea = new JTextArea();
        cleanConsole();

        GoliteMenuBar menuBar = new GoliteMenuBar();
        setJMenuBar(menuBar);
        add(new MainPanel(editorPanel, consoleTextArea));

        wireActions(menuBar);

        setVisible(true);
        editorPanel.getTextArea().requestFocus();
    }

    private void wireActions(GoliteMenuBar menuBar) {
        menuBar.onRun(e -> run());
        menuBar.onClean(e -> cleanConsole());

        menuBar.onNew(e -> {
            editorPanel.setText("");
            currentFile = null;
            currentSymbols = new ArrayList<>();
            lexer = null;
            parser = null;
            interpreter = null;
            setTitle("Golite");
            cleanConsole();
        });

        menuBar.onLoad(e -> loadGLTFile());
        menuBar.onSave(e -> saveGLTFile());
        menuBar.onSaveAs(e -> saveAsGLTFile());
        menuBar.onExit(e -> System.exit(0));

        menuBar.onTokens(e -> tokens());
        menuBar.onErrors(e -> errors());

        // NUEVO: reporte de tabla de símbolos
        menuBar.onSymbols(e -> symbols());

        menuBar.onAbout(e -> JOptionPane.showMessageDialog(
                this,
                "GoLite\nVersión 1.0.0\nLaboratorio OLC1",
                "Acerca de",
                JOptionPane.INFORMATION_MESSAGE));
    }

    private void run() {
        try {
            currentSymbols = new ArrayList<>();

            lexer = new Lexer(new BufferedReader(new StringReader(editorPanel.getText())));
            parser = new parser(lexer);

            Object result = parser.parse().value;

            ASTNODE ast;

            if (result instanceof ASTNODE node) {
                ast = node;

            } else if (result instanceof java.util.List<?> list) {
                Statments statements = new Statments(null);

                for (Object item : list) {
                    if (item instanceof ASTNODE node) {
                        statements.add(node);
                    }
                }

                ast = statements;

            } else {
                throw new RuntimeException(
                        "El parser devolvio " + result.getClass().getName() + " y no se puede interpretar"
                );
            }

            interpreter = new InterpreterVisitor();
            interpreter.Visit(ast);

            // NUEVO: guardamos los símbolos generados por el intérprete
            currentSymbols = new ArrayList<>(interpreter.symbols);

            cleanConsole();
            consoleTextArea.append(interpreter.output);

        } catch (Exception e) {
            cleanConsole();
            consoleTextArea.append("Error: " + e.getMessage() + "\n");
        }

        consoleTextArea.setCaretPosition(consoleTextArea.getDocument().getLength());
        editorPanel.getTextArea().requestFocus();
    }

    private void loadGLTFile() {
        JFileChooser fileChooser = new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Archivos GoLite (*.glt, *.GLT)",
                "glt",
                "GLT"
        );

        fileChooser.setFileFilter(filter);
        fileChooser.setDialogTitle("Cargar archivo .GLT");

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try {
                String content = Files.readString(
                        selectedFile.toPath(),
                        StandardCharsets.UTF_8
                );

                editorPanel.setText(content);

                currentFile = selectedFile;
                setTitle("Golite - " + selectedFile.getName());

                cleanConsole();

                consoleTextArea.append("Archivo cargado correctamente:\n");
                consoleTextArea.append(selectedFile.getAbsolutePath() + "\n");

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo cargar el archivo:\n" + ex.getMessage(),
                        "Error al cargar archivo",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }

        editorPanel.getTextArea().requestFocus();
    }

    private void saveGLTFile() {
        if (currentFile == null) {
            saveAsGLTFile();
            return;
        }

        saveContentToFile(currentFile);
    }

    private void saveAsGLTFile() {
        JFileChooser fileChooser = new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Archivos GoLite (*.glt, *.GLT)",
                "glt",
                "GLT"
        );

        fileChooser.setFileFilter(filter);
        fileChooser.setDialogTitle("Guardar archivo .GLT");

        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            if (!selectedFile.getName().toLowerCase().endsWith(".glt")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".glt");
            }

            currentFile = selectedFile;
            saveContentToFile(currentFile);
            setTitle("Golite - " + currentFile.getName());
        }

        editorPanel.getTextArea().requestFocus();
    }

    private void saveContentToFile(File file) {
        try {
            Files.writeString(
                    file.toPath(),
                    editorPanel.getText(),
                    StandardCharsets.UTF_8
            );

            cleanConsole();
            consoleTextArea.append("Archivo guardado correctamente:\n");
            consoleTextArea.append(file.getAbsolutePath() + "\n");

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo guardar el archivo:\n" + ex.getMessage(),
                    "Error al guardar archivo",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        editorPanel.getTextArea().requestFocus();
    }

    private void errors() {
        if (lexer == null || parser == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Aún no se ha ejecutado nada.",
                    "Reporte de Errores",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String[] columnas = {
                "No.",
                "Descripción",
                "Línea",
                "Columna",
                "Tipo"
        };

        List<Object[]> filas = new ArrayList<>();
        int contador = 1;

        for (GoliteError error : lexer.errors) {
            filas.add(new Object[]{
                    contador++,
                    error.getDescription(),
                    error.getLine(),
                    error.getColumn(),
                    error.getType()
            });
        }

        for (GoliteError error : parser.errors) {
            filas.add(new Object[]{
                    contador++,
                    error.getDescription(),
                    error.getLine(),
                    error.getColumn(),
                    error.getType()
            });
        }

        if (interpreter != null) {
            for (GoliteError error : interpreter.errors) {
                filas.add(new Object[]{
                        contador++,
                        error.getDescription(),
                        error.getLine(),
                        error.getColumn(),
                        error.getType()
                });
            }
        }

        if (filas.isEmpty()) {
            filas.add(new Object[]{
                    1,
                    "No hay errores registrados.",
                    "-",
                    "-",
                    "Sin errores"
            });
        }

        Object[][] datos = filas.toArray(new Object[0][]);

        new ReportWindow(
                "Reporte de Errores",
                columnas,
                datos
        ).setVisible(true);
    }

    private void tokens() {
        if (lexer == null || parser == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Aún no se ha ejecutado nada.",
                    "Tabla de Tokens",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String[] columnas = {
                "No.",
                "Lexema",
                "Token",
                "Línea",
                "Columna"
        };

        List<Object[]> filas = new ArrayList<>();
        int contador = 1;

        for (TokenReport token : lexer.tokens) {
            filas.add(new Object[]{
                    contador++,
                    token.getLexeme(),
                    token.getTokenType(),
                    token.getLine(),
                    token.getColumn()
            });
        }

        if (filas.isEmpty()) {
            filas.add(new Object[]{
                    1,
                    "No hay tokens registrados.",
                    "-",
                    "-",
                    "-"
            });
        }

        Object[][] datos = filas.toArray(new Object[0][]);

        new ReportWindow(
                "Tabla de Tokens",
                columnas,
                datos
        ).setVisible(true);
    }

    // NUEVO: reporte de tabla de símbolos
    private void symbols() {
        if (lexer == null || parser == null || interpreter == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Aún no se ha ejecutado nada.",
                    "Reporte de Tabla de Símbolos",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String[] columnas = {
                "ID",
                "Tipo símbolo",
                "Tipo dato",
                "Ámbito",
                "Línea",
                "Columna"
        };

        List<Object[]> filas = new ArrayList<>();

        for (SymbolReport symbol : currentSymbols) {
            filas.add(new Object[]{
                    symbol.getId(),
                    symbol.getSymbolType(),
                    symbol.getDataType(),
                    symbol.getScope(),
                    symbol.getLine(),
                    symbol.getColumn()
            });
        }

        if (filas.isEmpty()) {
            filas.add(new Object[]{
                    "No hay símbolos registrados.",
                    "-",
                    "-",
                    "-",
                    "-",
                    "-"
            });
        }

        Object[][] datos = filas.toArray(new Object[0][]);

        new ReportWindow(
                "Reporte de Tabla de Símbolos",
                columnas,
                datos
        ).setVisible(true);
    }

    private void cleanConsole() {
        consoleTextArea.setText("CONSOLA  -  LABORATORIO DE ORGANIZACION DE LENGUAJES Y COMPILADORES 1\n\n");
    }

    public EditorPanel getEditorPanel() {
        return editorPanel;
    }

    public JTextArea getConsoleTextArea() {
        return consoleTextArea;
    }
}