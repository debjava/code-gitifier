package com.ddlab.rnd.ui;

import com.ddlab.rnd.constants.MessageBundle;
import com.ddlab.rnd.git.model.UserAccount;
import com.ddlab.rnd.services.TestButtonServiceImpl;
import com.ddlab.rnd.ui.util.UIUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Getter
@Setter
public class CodePublishPanelComponent {

    private JPanel mainPanel;
    private JTextArea textArea;
    private JButton testBtn;
    private JComboBox hostedGitTypeCombo;
    private JComboBox slGitUserNameCombo;
    private JComboBox avlRepoCombo;

    public CodePublishPanelComponent() {
        createMainPanel();

        createHostedGitTypeLable();

        createdHostedGitTypeCombo();

        createSelectedGitUserNameLabel();

        createSelectedGitUserNameCombo();

        createAvlRepoLable();

        createAvlRepoCombo();

        createTestButton();

        createScrollPaneTextArea();

        initializeData();

    }

    // ~~~~~~ All private Methods

    private void createMainPanel() {
        mainPanel = new JPanel();
        mainPanel.setBounds(100, 100, 700, 400);
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        gbl_contentPanel.rowHeights = new int[]{0, 0, 0};
        gbl_contentPanel.columnWeights = new double[]{0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        mainPanel.setLayout(gbl_contentPanel);
    }

    private void createHostedGitTypeLable() {
        JLabel slGitActTypeLbl = new JLabel("Select Hosted Git Type: ");
        GridBagConstraints gbc_slGitActTypeLbl = new GridBagConstraints();
        gbc_slGitActTypeLbl.anchor = GridBagConstraints.EAST;
        gbc_slGitActTypeLbl.insets = new Insets(0, 0, 5, 5);
        gbc_slGitActTypeLbl.gridx = 0;
        gbc_slGitActTypeLbl.gridy = 0;
        mainPanel.add(slGitActTypeLbl, gbc_slGitActTypeLbl);
    }

    private void createdHostedGitTypeCombo() {
        hostedGitTypeCombo = new JComboBox();
        GridBagConstraints gbc_hostedGitTypeCombo = new GridBagConstraints();
        gbc_hostedGitTypeCombo.gridwidth = 9;
        gbc_hostedGitTypeCombo.insets = new Insets(0, 0, 5, 5);
        gbc_hostedGitTypeCombo.fill = GridBagConstraints.HORIZONTAL;
        gbc_hostedGitTypeCombo.gridx = 1;
        gbc_hostedGitTypeCombo.gridy = 0;
        mainPanel.add(hostedGitTypeCombo, gbc_hostedGitTypeCombo);
    }

    private void createSelectedGitUserNameLabel() {
        JLabel slGitUserNameLbl = new JLabel("Select Git User Name:");
        GridBagConstraints gbc_slGitUserNameLbl = new GridBagConstraints();
        gbc_slGitUserNameLbl.insets = new Insets(0, 0, 5, 5);
        gbc_slGitUserNameLbl.gridx = 0;
        gbc_slGitUserNameLbl.gridy = 1;
        mainPanel.add(slGitUserNameLbl, gbc_slGitUserNameLbl);
    }

    private void createSelectedGitUserNameCombo() {
        slGitUserNameCombo = new JComboBox();
        GridBagConstraints gbc_slGitUserNameCombo = new GridBagConstraints();
        gbc_slGitUserNameCombo.gridwidth = 9;
        gbc_slGitUserNameCombo.insets = new Insets(0, 0, 5, 5);
        gbc_slGitUserNameCombo.fill = GridBagConstraints.HORIZONTAL;
        gbc_slGitUserNameCombo.gridx = 1;
        gbc_slGitUserNameCombo.gridy = 1;
        mainPanel.add(slGitUserNameCombo, gbc_slGitUserNameCombo);
    }

    private void createAvlRepoLable() {
        JLabel avlRepoLbl = new JLabel("Available Repositories:");
        GridBagConstraints gbc_avlRepoLbl = new GridBagConstraints();
        gbc_avlRepoLbl.insets = new Insets(0, 0, 5, 5);
        gbc_avlRepoLbl.gridx = 0;
        gbc_avlRepoLbl.gridy = 2;
        mainPanel.add(avlRepoLbl, gbc_avlRepoLbl);
    }

    private void createAvlRepoCombo() {
        avlRepoCombo = new JComboBox();
        avlRepoCombo.setPreferredSize(new Dimension(200, avlRepoCombo.getPreferredSize().height));

        GridBagConstraints gbc_avlRepoCombo = new GridBagConstraints();
        gbc_avlRepoCombo.gridwidth = 9;
        gbc_avlRepoCombo.insets = new Insets(0, 0, 5, 5);
        gbc_avlRepoCombo.fill = GridBagConstraints.HORIZONTAL;
        gbc_avlRepoCombo.gridx = 1;
        gbc_avlRepoCombo.gridy = 2;
        mainPanel.add(avlRepoCombo, gbc_avlRepoCombo);
    }

    private void createTestButton() {
        testBtn = new JButton("Test");
        GridBagConstraints gbc_testBtn = new GridBagConstraints();
        gbc_testBtn.insets = new Insets(0, 0, 5, 5);
        gbc_testBtn.gridx = 11;
        gbc_testBtn.gridy = 2;
        mainPanel.add(testBtn, gbc_testBtn);

        testBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedGitType = hostedGitTypeCombo.getSelectedItem().toString();
                UserAccount userAccount = UIUtil.getSelectedUserAccount(hostedGitTypeCombo, slGitUserNameCombo);
                fillupAvailableRepoCombo(selectedGitType, userAccount);
                UIUtil.saveLastSessionSetting(hostedGitTypeCombo,slGitUserNameCombo);
            }
        });
    }

    private void fillupAvailableRepoCombo(String selectedGitType, UserAccount userAccount) {
        CompletableFuture<String[]> future = TestButtonServiceImpl.getRepos(selectedGitType, userAccount);
        future.thenAccept(result -> {
            ApplicationManager.getApplication().invokeLater(() -> {
                for(String value : result) {
                    avlRepoCombo.addItem(value);
                }
            });
        }).exceptionally(ex -> {
            log.error("Exception while getting the list of repos: {}", ex);
            ApplicationManager.getApplication().invokeLater(() ->
                    Messages.showErrorDialog("Exception while getting the list of repos: "+ex,"Publisher"));
            return null;
        });
    }

    private void createScrollPaneTextArea() {
        JScrollPane txtScrollPane = new JScrollPane();
        txtScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        txtScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        String shortDesc = MessageBundle.message("short.desc");
        txtScrollPane.setBorder(BorderFactory.createTitledBorder(shortDesc));
        GridBagConstraints gbc_txtScrollPane = new GridBagConstraints();
        gbc_txtScrollPane.gridwidth = 3;
        gbc_txtScrollPane.insets = new Insets(0, 0, 5, 5);
        gbc_txtScrollPane.fill = GridBagConstraints.BOTH;
        gbc_txtScrollPane.gridx = 0;
        gbc_txtScrollPane.gridy = 3;
        mainPanel.add(txtScrollPane, gbc_txtScrollPane);
        createTextArea();
        txtScrollPane.setViewportView(textArea);
    }

    private void createTextArea() {
        textArea = new JTextArea(5, 20); // rows, columns → gives preferred size
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
    }

    private void initializeData() {
        // Populate all combo boxes
        hostedGitTypeCombo.removeAllItems();
        Map<String, List<String>> gitAndUserNamesMap = TestButtonServiceImpl.getGitAndUserNamesMap();

        fillInitialGitTypeAndUserCombo(gitAndUserNamesMap);
        fillDynamicGitTypeAndUserCombo(gitAndUserNamesMap);
        clearOnSelectionOfGitUserName();
    }

    private void fillInitialGitTypeAndUserCombo(Map<String, List<String>> gitAndUserNamesMap) {
        gitAndUserNamesMap.forEach( (key,value) -> {
            hostedGitTypeCombo.addItem(key);
            // For the first time, populate
            slGitUserNameCombo.removeAllItems();
            List<String> gitUserNamesList = gitAndUserNamesMap.get(hostedGitTypeCombo.getSelectedItem().toString());
            for(String gitUserName : gitUserNamesList) {
                slGitUserNameCombo.addItem(gitUserName);
            }
        });
        //Initialize to last saved session
        UIUtil.resetToLastSavedSession(hostedGitTypeCombo,slGitUserNameCombo);
    }

    private void fillDynamicGitTypeAndUserCombo(Map<String, List<String>> gitAndUserNamesMap) {
        hostedGitTypeCombo.addActionListener(e -> {
            avlRepoCombo.removeAllItems();
            slGitUserNameCombo.removeAllItems();
            log.debug("Selected Git Combo Type: " + hostedGitTypeCombo.getSelectedItem().toString());
            List<String> gitUserNamesList = gitAndUserNamesMap.get(hostedGitTypeCombo.getSelectedItem().toString());
            for(String gitUserName : gitUserNamesList) {
                slGitUserNameCombo.addItem(gitUserName);
            }
        });
    }

    private void clearOnSelectionOfGitUserName() {
        slGitUserNameCombo.addActionListener(e -> avlRepoCombo.removeAllItems());
    }

    private void autoResizePanel() {
        SwingUtilities.invokeLater(() -> {
            mainPanel.revalidate();
            mainPanel.repaint();
            Window window = SwingUtilities.getWindowAncestor(mainPanel);
            if (window instanceof JFrame) {
                ((JFrame) window).pack(); // auto-resizes frame to fit new preferred sizes
            }
        });
    }

}
