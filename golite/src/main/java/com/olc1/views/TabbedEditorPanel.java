package com.olc1.views;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeListener;

public class TabbedEditorPanel extends JPanel {
    private final JTabbedPane tabbedPane;
    private final List<EditorTab> tabs;
    private int untitledCounter = 1;

    public TabbedEditorPanel() {
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();
        tabs = new ArrayList<>();

        add(tabbedPane, BorderLayout.CENTER);

        newTab();
    }

    public EditorTab newTab() {
        EditorTab tab = new EditorTab("Nuevo " + untitledCounter++);
        tabs.add(tab);

        tabbedPane.addTab(tab.getTitle(), tab.getEditorPanel());
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);

        return tab;
    }

    public EditorTab openFile(File file, String content) {
        EditorTab tab = new EditorTab(file.getName());
        tab.setFile(file);
        tab.setText(content);

        tabs.add(tab);

        tabbedPane.addTab(tab.getTitle(), tab.getEditorPanel());
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);

        return tab;
    }

    public void closeCurrentTab() {
        int index = tabbedPane.getSelectedIndex();

        if (index < 0) {
            return;
        }

        tabs.remove(index);
        tabbedPane.removeTabAt(index);

        if (tabs.isEmpty()) {
            newTab();
        }
    }

    public EditorTab getCurrentTab() {
        int index = tabbedPane.getSelectedIndex();

        if (index < 0 || index >= tabs.size()) {
            return null;
        }

        return tabs.get(index);
    }

    public EditorPanel getCurrentEditorPanel() {
        EditorTab tab = getCurrentTab();

        if (tab == null) {
            return null;
        }

        return tab.getEditorPanel();
    }

    public String getCurrentText() {
        EditorTab tab = getCurrentTab();

        if (tab == null) {
            return "";
        }

        return tab.getText();
    }

    public void setCurrentText(String text) {
        EditorTab tab = getCurrentTab();

        if (tab != null) {
            tab.setText(text);
        }
    }

    public File getCurrentFile() {
        EditorTab tab = getCurrentTab();

        if (tab == null) {
            return null;
        }

        return tab.getFile();
    }

    public void setCurrentFile(File file) {
        EditorTab tab = getCurrentTab();

        if (tab == null) {
            return;
        }

        tab.setFile(file);

        int index = tabbedPane.getSelectedIndex();
        if (index >= 0) {
            tabbedPane.setTitleAt(index, tab.getTitle());
        }
    }

    public void addChangeListener(ChangeListener listener) {
        tabbedPane.addChangeListener(listener);
    }
}