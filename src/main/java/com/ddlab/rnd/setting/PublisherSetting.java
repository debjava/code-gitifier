package com.ddlab.rnd.setting;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;

@State(
        name = "ddlabsgitifiersettings",
        storages = @Storage("ddlabsgitifiersettings.xml")
)
@Getter
@Setter
public class PublisherSetting implements PersistentStateComponent<PublisherSetting> {

    private LinkedHashMap<String, String> gitInfoTableMap = new LinkedHashMap<String, String>();
    private String lastSavedHostedGitTypeSelection = null;
    private String lastSavedGitUserNameSelection = null;


    public static PublisherSetting getInstance() {
        return com.intellij.openapi.application.ApplicationManager.getApplication().getService(PublisherSetting.class);
    }

    @Override
    public @Nullable PublisherSetting getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull PublisherSetting state) {
        this.gitInfoTableMap = state.gitInfoTableMap;
        this.lastSavedHostedGitTypeSelection = state.lastSavedHostedGitTypeSelection;
        this.lastSavedGitUserNameSelection = state.lastSavedGitUserNameSelection;
    }
}
