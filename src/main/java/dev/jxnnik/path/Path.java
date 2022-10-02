package dev.jxnnik.path;

import dev.jxnnik.path.command.PathCommand;
import dev.jxnnik.path.listener.PlayerMoveListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Path extends JavaPlugin {

    private static Path instance;

    @Override
    public void onEnable() {
        instance = this;

        new PlayerMoveListener(this);

        getCommand("path").setExecutor(new PathCommand());
    }

    public static Path getInstance() {
        return instance;
    }
}
