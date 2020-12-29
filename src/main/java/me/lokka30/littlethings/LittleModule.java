package me.lokka30.littlethings;

public interface LittleModule {

    boolean isEnabled();

    String getName();

    int getInstalledConfigVersion();

    int getLatestConfigVersion();

    void loadConfig();

    void loadModule();

    void reloadModule();
}
