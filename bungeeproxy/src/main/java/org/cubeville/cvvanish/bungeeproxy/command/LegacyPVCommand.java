package org.cubeville.cvvanish.bungeeproxy.command;

import java.util.List;
import java.util.Set;

import org.cubeville.common.bungeecord.command.PlayerCommand;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class LegacyPVCommand extends PlayerCommand {
    
    private static final String USE_PERMISSION = "cvvanish.legacypv.use";
    
    public LegacyPVCommand() {
        
        super("pv");
        setMinimumPermissionToUse(USE_PERMISSION);
        setNoPermissionMessage("Unknown command. Type \"/help\" for help.", ChatColor.WHITE);
        setSyntax("Syntax: /pv");
        setNumberOfOptionalArguments(2);
    }
    
    @Override
    public void execute(ProxiedPlayer commandSenderPlayer, Set<String> flags, List<String> arguments) {
        
        TextComponent thisIsTheLegacyVanishCommand = new TextComponent();
        TextComponent pleaseCheckTheStaffForums = new TextComponent();
        
        thisIsTheLegacyVanishCommand.setText("This is the legacy vanish command.");
        pleaseCheckTheStaffForums.setText("Please check the staff forums for an updated list of commands.");
        
        thisIsTheLegacyVanishCommand.setColor(ChatColor.RED);
        pleaseCheckTheStaffForums.setColor(ChatColor.AQUA);
        
        commandSenderPlayer.sendMessage(thisIsTheLegacyVanishCommand);
        commandSenderPlayer.sendMessage(pleaseCheckTheStaffForums);
    }
}
