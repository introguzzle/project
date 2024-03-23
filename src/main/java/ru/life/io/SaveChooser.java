package ru.life.io;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SaveChooser extends JFileChooser {

    private final JFrame owner;

    public SaveChooser(JFrame owner) {
        super();

        this.owner = owner;

        setDialogTitle("Save");
        setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
    }

    public int showDialog() {
        return showSaveDialog(owner);
    }

    public String getPath() {
        String absolutePath = getSelectedFile().getAbsolutePath();

        return absolutePath.endsWith(".txt") ? absolutePath : absolutePath + ".txt";
    }
}
