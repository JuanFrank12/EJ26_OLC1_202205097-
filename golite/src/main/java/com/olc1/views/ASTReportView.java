package com.olc1.views;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.olc1.ast.ASTNODE;
import com.olc1.reports.ASTDotGenerator;

public class ASTReportView extends JFrame {

    private String getDotCommand() {
    File graphvizDot = new File("C:\\Program Files\\Graphviz\\bin\\dot.exe");

    if (graphvizDot.exists()) {
        return graphvizDot.getAbsolutePath();
    }

    return "dot";
}

    public ASTReportView(ASTNODE ast) {
        setTitle("Reporte AST");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        ASTDotGenerator generator = new ASTDotGenerator();
        String dot = generator.generate(ast);

        try {
            File dotFile = File.createTempFile("ast_report_", ".dot");
            File pngFile = File.createTempFile("ast_report_", ".png");

            Files.writeString(dotFile.toPath(), dot, StandardCharsets.UTF_8);

            ProcessBuilder processBuilder = new ProcessBuilder(
                getDotCommand(),
                "-Tpng",
                dotFile.getAbsolutePath(),
                "-o",
                pngFile.getAbsolutePath()
        );

            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0 && pngFile.exists()) {
                ImageIcon icon = new ImageIcon(pngFile.getAbsolutePath());

                Image scaled = icon.getImage().getScaledInstance(
                        icon.getIconWidth(),
                        icon.getIconHeight(),
                        Image.SCALE_SMOOTH
                );

                JLabel imageLabel = new JLabel(new ImageIcon(scaled));
                JScrollPane scrollPane = new JScrollPane(imageLabel);

                add(scrollPane, BorderLayout.CENTER);
            } else {
                showDotText(dot);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo generar la imagen con Graphviz.\n"
                    + "Se mostrará el código DOT.\n\n"
                    + "Si querés verlo como árbol gráfico, instalá Graphviz y asegurate de tener 'dot' en el PATH.",
                    "Reporte AST",
                    JOptionPane.WARNING_MESSAGE
            );

            showDotText(dot);
        }
    }

    private void showDotText(String dot) {
        JTextArea textArea = new JTextArea(dot);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);
    }
}