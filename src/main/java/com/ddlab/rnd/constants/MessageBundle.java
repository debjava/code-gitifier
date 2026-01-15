package com.ddlab.rnd.constants;

import com.intellij.DynamicBundle;

public class MessageBundle extends DynamicBundle  {

    private static final MessageBundle INSTANCE = new MessageBundle();

    protected MessageBundle() {
        super("messages.MyMessageBundle");
    }

    public static String message(String key, Object... params) {
        return INSTANCE.getMessage(key, params);
    }

    public static String[] getGitChoices(String key,Object... params) {
        String allGitChoices = INSTANCE.getMessage(key, params);
        String[] gitChoices = allGitChoices.split(",");
        return gitChoices;
    }

    public static String[] getGitTableColumns(String key,Object... params) {
        String allTableColumns = INSTANCE.getMessage(key, params);
        String[] gitTableColumns = allTableColumns.split(",");
        return gitTableColumns;
    }
}
