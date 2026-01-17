package com.ddlab.rnd.services;

import com.ddlab.rnd.git.model.UserAccount;
import com.ddlab.rnd.handler.HostedGitType;
//import com.ddlab.rnd.handler.IGitHandler;
import com.ddlab.rnd.handler.IGitHandler;
import com.ddlab.rnd.setting.PublisherSetting;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.Messages;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class TestButtonServiceImpl {

    public static CompletableFuture<String[]> getRepos(String selectedGitType, UserAccount userAccount) {
        CompletableFuture<String[]> future = new CompletableFuture<>();
        ProgressManager.getInstance().run(new Task.Modal(null, "Compute Value", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                   IGitHandler gitHandler = HostedGitType.fromString(selectedGitType).getGitHandler(userAccount);
                   gitHandler.getAllRepositories();
                    String[] repos = gitHandler.getAllRepositories();
                    future.complete(repos);
                } catch(Exception re) {
                    future.completeExceptionally(re);
                    log.error("Exception while getting the list of repos: {}", re);
                    ApplicationManager.getApplication().invokeLater(() ->
                            Messages.showErrorDialog("Exception while getting the list of repos: "+re,"Publisher"));
                }
            }
        });
        return future;
    }

    public static Map<String, List<String>> getGitAndUserNamesMap() {
        PublisherSetting setting = PublisherSetting.getInstance();
        Map<String,String> tableInfoMap = setting.getGitInfoTableMap();
        Map<String, List<String>> gitAndUserNamesMap = new LinkedHashMap<>();
        tableInfoMap.forEach((key,value) -> {
            String[] keys = key.split("~");
            String hostedGitType = keys[0];
            String gitUserName = keys[1];

            if(gitAndUserNamesMap.containsKey(hostedGitType)) {
                gitAndUserNamesMap.get(hostedGitType).add(gitUserName);
            } else {
                List<String> gitUserNames = new ArrayList<>();
                gitUserNames.add(gitUserName);
                gitAndUserNamesMap.put(hostedGitType, gitUserNames);
            }
        });
        return gitAndUserNamesMap;
    }
}
