package ru.life.io;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class OpenChooser extends JFileChooser {

    private final JFrame owner;

    public OpenChooser(JFrame owner) {
        super();

        this.owner = owner;

        setDialogTitle("Open");
        setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
    }

    public int showDialog() {
        return showOpenDialog(owner);
    }

    public String getPath() {
        return getSelectedFile().getAbsolutePath();
    }
}
