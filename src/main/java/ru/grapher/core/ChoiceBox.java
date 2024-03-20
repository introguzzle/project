package ru.grapher.core;

import javax.swing.*;
import javax.swing.plaf.ComboBoxUI;
import java.util.Collection;
import java.util.Map;

public abstract class ChoiceBox extends JComboBox<String> {

    public ChoiceBox() {
        super();

        this.setEnabled(false);
        this.getModel().addListDataListener(new ChoiceBoxListener(this));
    }

    public ChoiceBox(ComboBoxUI ui) {
        super();

        this.setUI(ui);
        this.setEnabled(false);
        this.getModel().addListDataListener(new ChoiceBoxListener(this));
    }

    public void setLinkedComponent(JComponent component) {
        this.getModel().addListDataListener(new ChoiceBoxListener(this) {
            @Override
            public void onChange() {
                component.setEnabled(ChoiceBox.this.getModel().getSize() != 0);
            }
        });
    }

    public void clear() {
        this.removeAllItems();
    }

    private boolean contains(String item) {
        for (int i = 0; i < getItemCount(); i++) {
            if (item.equals(getItemAt(i)))
                return true;
        }

        return false;
    }

    public void setItems(Collection<String> collection) {
        if (!collection.isEmpty())
            for (String item: collection)
                if (!contains(item))
                    this.addItem(item);
    }

    public void setItems(Map<String, ?> map) {
        this.setItems(map.keySet());
    }

    public void setNewItems(Collection<String> collection) {
        this.clear();

        this.setItems(collection);
    }

    public void setNewItems(Map<String, ?> map) {
        this.clear();

        this.setItems(map);
    }

    public void next() {
        int size = this.getModel().getSize();

        if (size > 0)
            this.setSelectedIndex((this.getSelectedIndex() + 1) % size);
    }

    @Override
    public void setModel(ComboBoxModel<String> model) {
        if (this.getModel() == null)
            super.setModel(model);
    }

    @Override
    public String getSelectedItem() {
        return (String) super.getSelectedItem();
    }
}
