package com.ddlab.rnd.ui.table;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class TextCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        JLabel cell = (JLabel) super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

//        cell.setBackground(Color.WHITE);
//        cell.setForeground(Color.BLACK);


        // Customize text appearance to mimic JTextField
//        cell.setHorizontalAlignment(SwingConstants.LEFT);
//        cell.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
//        cell.setForeground(java.awt.Color.WHITE);   // text color white
//        cell.setBackground(java.awt.Color.BLACK);   // dark background for contrast

        // Ensure background is visible (important for JTable renderers)
//        if (isSelected) {
//            cell.setBackground(table.getSelectionBackground());
//            cell.setForeground(table.getSelectionForeground());
//        } else {
////            cell.setBackground(java.awt.Color.BLACK);
//            cell.setForeground(java.awt.Color.WHITE);
//        }

        return cell;
    }
}