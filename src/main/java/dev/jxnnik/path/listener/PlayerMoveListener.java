package dev.jxnnik.path.listener;

import dev.jxnnik.path.data.PathData;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

public class PlayerMoveListener implements Listener {

    public PlayerMoveListener(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void handlePlayerMove(PlayerMoveEvent event) {
        if(PathData.task) {
            PathData.locations.add(event.getPlayer().getLocation());
        }
    }
}