package com.ddlab.rnd.ui.table;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

public class DeleteCellEditor extends AbstractCellEditor implements TableCellEditor {

    private JButton button;
    private JTable table;
    private int rowToDelete = -1;

    public DeleteCellEditor(JTable table) {
        this.table = table;

        button = new JButton("Remove");
        button.setForeground(java.awt.Color.RED);

        button.addActionListener(e -> {
            rowToDelete = table.getEditingRow();
            fireEditingStopped();   // Stop editing first

            // ✅ Delete AFTER editor lifecycle completes
            SwingUtilities.invokeLater(() -> {
                if (rowToDelete >= 0 &&
                        rowToDelete < table.getModel().getRowCount()) {
                    ((DefaultTableModel) table.getModel()).removeRow(rowToDelete);
                }
                rowToDelete = -1;
            });
        });
    }

    @Override
    public java.awt.Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected,
            int row, int column) {
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return "Remove";
    }

}