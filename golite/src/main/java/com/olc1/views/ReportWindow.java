package com.olc1.views;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ReportWindow extends JFrame {

    public ReportWindow(String title, String[] columns, Object[][] data) {
        setTitle(title);
        setSize(800, 500);
        setLocationRelativeTo(null);

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane);
    }
}