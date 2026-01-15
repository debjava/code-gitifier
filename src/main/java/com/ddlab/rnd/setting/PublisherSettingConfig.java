package com.ddlab.rnd.setting;

import com.ddlab.rnd.ui.GitPanelComponent;
import com.ddlab.rnd.ui.util.UIUtil;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PublisherSettingConfig implements Configurable {
    private JPanel panel;
    private GitPanelComponent gitPanelComponent;

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "";
    }

    @Override
    public @Nullable JComponent createComponent() {
        panel = createUIAndGetPanel();
        return panel;
    }

    @Override
    public boolean isModified() {
//        return false;
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        UIUtil.saveSetting(gitPanelComponent);
    }

    @Override
    public void reset() {
        UIUtil.resetReloadSetting(gitPanelComponent);
    }

    // ~~~~~~~~ private methods ~~~~~~~~
    private JPanel createUIAndGetPanel() {
        gitPanelComponent = new GitPanelComponent();
        return gitPanelComponent.getMainPanel();
    }
}
