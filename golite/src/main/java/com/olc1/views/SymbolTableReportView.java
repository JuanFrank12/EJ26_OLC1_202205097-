package com.olc1.views;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.olc1.reports.SymbolReport;

public class SymbolTableReportView extends JFrame {

    public SymbolTableReportView(List<SymbolReport> symbols) {
        setTitle("Reporte de Tabla de Símbolos");
        setSize(800, 500);
        setLocationRelativeTo(null);

        String[] columns = {
            "ID",
            "Tipo símbolo",
            "Tipo dato",
            "Ámbito",
            "Línea",
            "Columna"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0);

        if (symbols == null || symbols.isEmpty()) {
            model.addRow(new Object[]{
                "No hay símbolos registrados",
                "-",
                "-",
                "-",
                "-",
                "-"
            });
        } else {
            for (SymbolReport symbol : symbols) {
                model.addRow(new Object[]{
                    symbol.getId(),
                    symbol.getSymbolType(),
                    symbol.getDataType(),
                    symbol.getScope(),
                    symbol.getLine(),
                    symbol.getColumn()
                });
            }
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane);
    }
}