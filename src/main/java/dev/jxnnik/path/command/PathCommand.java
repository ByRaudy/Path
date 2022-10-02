package dev.jxnnik.path.command;

import com.destroystokyo.paper.Title;
import dev.jxnnik.path.Path;
import dev.jxnnik.path.data.PathData;
import dev.jxnnik.path.data.PrefixData;
import jdk.dynalink.linker.support.CompositeGuardingDynamicLinker;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PathCommand implements CommandExecutor {

    private final HashMap<String, List<Location>> pathMap;

    public PathCommand() {
        pathMap = new HashMap<>();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;

        if (player.isOp() && args.length == 0) {
            player.sendMessage(PrefixData.PREFIX_MAIN + "Hilfe zu /path:");
            player.sendMessage(PrefixData.PREFIX_MAIN + "/path create");
            player.sendMessage(PrefixData.PREFIX_MAIN + "/path list");
            player.sendMessage(PrefixData.PREFIX_MAIN + "/path save <name>");
            player.sendMessage(PrefixData.PREFIX_MAIN + "/path play <name> <ticks(optional)>");
            player.sendMessage(PrefixData.PREFIX_MAIN + "/path ride <name> <ticks(optional)>");
            return false;
        }

        if (player.isOp() && args[0].equals("create")) {
            player.sendTitle(new Title("", "Please run a path and type: /path save <name>", 10, 1000, 10));
            PathData.task = true;
            return false;
        }

        if (player.isOp() && args.length == 2 && args[0].equals("save") && PathData.task) {
            List<Location> finalLocations = new ArrayList<>(PathData.locations);
            pathMap.put(args[1], finalLocations);
            PathData.task = false;
            PathData.locations.clear();
            player.sendMessage(PrefixData.PREFIX_MAIN + "Path saved.");
            player.resetTitle();
        }

        if (player.isOp() && args[0].equals("list")) {
            pathMap.keySet().forEach(player::sendMessage);
        }

        if (player.isOp() && args[0].equals("play")) {
            if (pathMap.get(args[1]) != null) {
                List<Location> pathLocations = pathMap.get(args[1]);
                ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(pathLocations.get(0), EntityType.ARMOR_STAND);
                AtomicInteger count = new AtomicInteger(0);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            count.getAndIncrement();
                            armorStand.teleport(pathLocations.get(count.get()));
                        } catch (IndexOutOfBoundsException exception) {
                            player.sendMessage(PrefixData.PREFIX_MAIN + "Path is over.");
                            armorStand.remove();
                            this.cancel();
                        }
                    }
                }.runTaskTimer(Path.getInstance(), 0, args.length == 3 ? Integer.parseInt(args[2]) : 1);
            } else {
                player.sendMessage(PrefixData.PREFIX_MAIN + "The plugin can't find a path with name '" + args[1] + "'.");
            }
        }

        if (player.isOp() && args[0].equals("ride")) {
            if (pathMap.get(args[1]) != null) {
                List<Location> pathLocations = pathMap.get(args[1]);
                ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(pathLocations.get(0), EntityType.ARMOR_STAND);
                AtomicInteger count = new AtomicInteger(0);

                PathData.cachedLocation = player.getLocation();
                player.setGameMode(GameMode.SPECTATOR);
                player.setSpectatorTarget(armorStand);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            count.getAndIncrement();
                            armorStand.teleport(pathLocations.get(count.get()));
                        } catch (IndexOutOfBoundsException exception) {
                            player.sendMessage(PrefixData.PREFIX_MAIN + "Path is over.");
                            player.teleport(PathData.cachedLocation);
                            player.setGameMode(GameMode.SURVIVAL);
                            PathData.cachedLocation = null;
                            armorStand.remove();
                            this.cancel();
                        }
                    }
                }.runTaskTimer(Path.getInstance(), 0, args.length == 3 ? Integer.parseInt(args[2]) : 1);
            } else {
                player.sendMessage(PrefixData.PREFIX_MAIN + "The plugin can't find a path with name '" + args[1] + "'.");
            }
        }

        return false;
    }
}
