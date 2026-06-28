package com.olc1.views;

import java.io.File;

public class EditorTab {
    private final EditorPanel editorPanel;
    private File file;
    private String title;

    public EditorTab(String title) {
        this.title = title;
        this.editorPanel = new EditorPanel();
        this.file = null;
    }

    public EditorPanel getEditorPanel() {
        return editorPanel;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;

        if (file != null) {
            this.title = file.getName();
        }
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return editorPanel.getText();
    }

    public void setText(String text) {
        editorPanel.setText(text);
    }
}