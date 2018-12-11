package org.cubeville.cvvanish;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


public final class CVVanish extends JavaPlugin implements Listener {
    private VanishCommand vanishCommand;

    public static Set<UUID> invisible;

    @Override
    public void onEnable() {
        vanishCommand = new VanishCommand(this);
        invisible = new HashSet<>();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player senderPlayer = null;
        UUID senderId = null;
        if(sender instanceof Player) {
            senderPlayer = (Player) sender;
            senderId = senderPlayer.getUniqueId();
        }

        if(command.getName().equals("v")) {
            if(senderPlayer == null) return true;
            vanishCommand.onVanishCommand(senderPlayer, args);
            return true;
        }
        return true;
    }

    //TODO EventHandler: onPlayerJoin
}
