package com.ddlab.rnd.ui;

//import com.ddlab.rnd.constants.CommonConstants;
import com.ddlab.rnd.constants.MessageBundle;
import com.ddlab.rnd.ui.table.ComboBoxRenderer;
import com.ddlab.rnd.ui.table.DeleteButtonRenderer;
import com.ddlab.rnd.ui.table.DeleteCellEditor;
import com.ddlab.rnd.ui.table.TextCellRenderer;
import com.ddlab.rnd.ui.util.UIUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
@Setter
public class GitPanelComponent {

    private JPanel mainPanel;

    private DefaultTableModel gitTableModel;
    private JTable gitInfoTable;

//    private Map<String, String> gitInfoTableMap = new HashMap<String, String>();

    public GitPanelComponent() {
        mainPanel = new JPanel();

        mainPanelLayout();

        createGitInfoLabel();

        gitTableModel = new DefaultTableModel(MessageBundle.getGitTableColumns("git.table.columns"), 0);

        gitInfoTable = createAndGetGitInfoTable(gitTableModel);

        updateTableColumns(gitInfoTable, gitTableModel);

        createScrollPaneForTable(gitInfoTable);

        addRowButton();

        createGetInfoButton();

        createTempDeleteLable();
    }

    // ~~~~~~~~~ ALl private methods ~~~~~~~~~~~~~~~~~~~

    private void mainPanelLayout() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
        gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        mainPanel.setLayout(gridBagLayout);
    }

    private void createGitInfoLabel() {
        JLabel infoLbl = new JLabel("Enter below Git username and token");
        GridBagConstraints gbc_infoLbl = new GridBagConstraints();
        gbc_infoLbl.insets = new Insets(0, 0, 5, 5);
        gbc_infoLbl.gridx = 1;
        gbc_infoLbl.gridy = 1;
        mainPanel.add(infoLbl, gbc_infoLbl);
    }

    private JTable createAndGetGitInfoTable(DefaultTableModel tableModel) {
        JTable table = new JTable(tableModel) {
            @Override
            public String getToolTipText(java.awt.event.MouseEvent e) {

                int row = rowAtPoint(e.getPoint());
                int col = columnAtPoint(e.getPoint());

                if (row < 0 || col < 0) {
                    return null;
                }

                row = convertRowIndexToModel(row);
                col = convertColumnIndexToModel(col);

                Object value = getModel().getValueAt(row, col);

                switch (col) {
                    case 0:
                        return "Select Git provider (GitHub, GitLab, Bitbucket)";
                    case 1:
                        return "Double Click to enter the user name";
                    case 2:
                        return "Double Click to enter the git token";
                    case 3:
                        return "Click to remove this row";
                    default:
                        return value != null ? value.toString() : null;
                }
            }
        };
        table.setRowHeight(28);

        return table;
    }

    private void updateTableColumns(JTable table, DefaultTableModel tableModel) {
        JComboBox<String> comboEditor = new JComboBox<>(MessageBundle.getGitChoices("hosted.git.choices"));
        TableColumn comboColumn = table.getColumnModel().getColumn(0);
        comboColumn.setCellEditor(new DefaultCellEditor(comboEditor));
        comboColumn.setCellRenderer(new ComboBoxRenderer(MessageBundle.getGitChoices("hosted.git.choices")));

        table.getColumnModel().getColumn(1).setCellRenderer(new TextCellRenderer());
        table.getColumnModel().getColumn(2).setCellRenderer(new TextCellRenderer());

        // Button column
        table.getColumnModel().getColumn(3)
                .setCellRenderer(new DeleteButtonRenderer());
        table.getColumnModel().getColumn(3)
                .setCellEditor(new DeleteCellEditor(gitInfoTable));

    }

    private void createScrollPaneForTable(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);

        GridBagConstraints gbc_gitInfoTable = new GridBagConstraints();
        gbc_gitInfoTable.gridwidth = 7;
        gbc_gitInfoTable.insets = new Insets(0, 0, 5, 5);
        gbc_gitInfoTable.fill = GridBagConstraints.BOTH;
        gbc_gitInfoTable.gridx = 1;
        gbc_gitInfoTable.gridy = 2;
//        mainPanel.add(gitInfoTable, gbc_gitInfoTable);
        mainPanel.add(scrollPane, gbc_gitInfoTable);
    }

    private void addRowButton() {
        JButton addRowBtn = new JButton("Add");
        GridBagConstraints gbc_addRowBtn = new GridBagConstraints();
        gbc_addRowBtn.insets = new Insets(0, 0, 5, 0);
        gbc_addRowBtn.gridx = 8;
        gbc_addRowBtn.gridy = 2;
        mainPanel.add(addRowBtn, gbc_addRowBtn);

        addRowBtn
                .addActionListener(e -> UIUtil.populateOneRow(gitTableModel));
    }

    // To be removed later
    @Deprecated
    private void createGetInfoButton() {
        JButton getInfoBtn = new JButton("Get Info");
        GridBagConstraints gbc_getInfoBtn = new GridBagConstraints();
        gbc_getInfoBtn.insets = new Insets(0, 0, 5, 0);
        gbc_getInfoBtn.gridx = 8;
        gbc_getInfoBtn.gridy = 4;
        mainPanel.add(getInfoBtn, gbc_getInfoBtn);

        getInfoBtn.addActionListener(e -> {
            showAllTableInfo();
        });
    }

    // To be deleted Later
    @Deprecated
    private void createTempDeleteLable() {
        JLabel deleteLabel2 = new JLabel("Delete label 2");
        GridBagConstraints gbc_deleteLabel2 = new GridBagConstraints();
        gbc_deleteLabel2.insets = new Insets(0, 0, 0, 5);
        gbc_deleteLabel2.gridx = 1;
        gbc_deleteLabel2.gridy = 7;
        mainPanel.add(deleteLabel2, gbc_deleteLabel2);
    }

    // To be deleted later
    @Deprecated
    private void showAllTableInfo() {
        if (gitInfoTable.isEditing()) {
            gitInfoTable.getCellEditor().stopCellEditing();
        }

        Map<String, String> gitInfoMap = new HashMap<String, String>();
        DefaultTableModel model = (DefaultTableModel) gitInfoTable.getModel();
        for (int row = 0; row < model.getRowCount(); row++) {

            String selectionGitItem = String.valueOf(model.getValueAt(row, 0));
            String userNameField = String.valueOf(model.getValueAt(row, 1));
            String tokenField = String.valueOf(model.getValueAt(row, 2));

            gitInfoMap.put(selectionGitItem + "~" + userNameField, tokenField);

        }
    }

}
