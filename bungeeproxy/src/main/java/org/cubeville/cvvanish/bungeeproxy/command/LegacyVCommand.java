package org.cubeville.cvvanish.bungeeproxy.command;

import java.util.List;
import java.util.Set;

import org.cubeville.common.bungeecord.command.PlayerCommand;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class LegacyVCommand extends PlayerCommand {
    
    private static final String USE_PERMISSION = "cvvanish.legacyv.use";
    
    public LegacyVCommand() {
        
        super("v");
        setMinimumPermissionToUse(USE_PERMISSION);
        setNoPermissionMessage("Unknown command. Type \"/help\" for help.", ChatColor.WHITE);
    }

    @Override
    public void execute(ProxiedPlayer commandSenderPlayer, Set<String> flags, List<String> arguments) {
        
        TextComponent thisIsTheLegacyVanishCommand = new TextComponent();
        TextComponent theNewCommandsAre = new TextComponent();
        TextComponent hideCommandValue = new TextComponent();
        TextComponent and = new TextComponent();
        TextComponent showCommandValue = new TextComponent();
        TextComponent period = new TextComponent();
        
        thisIsTheLegacyVanishCommand.setText("This is the legacy vanish command.");
        theNewCommandsAre.setText("The new commands are ");
        hideCommandValue.setText("/hide");
        and.setText(" and ");
        showCommandValue.setText("/show");
        period.setText(".");
        
        thisIsTheLegacyVanishCommand.setColor(ChatColor.RED);
        theNewCommandsAre.setColor(ChatColor.AQUA);
        hideCommandValue.setColor(ChatColor.GOLD);
        and.setColor(ChatColor.AQUA);
        showCommandValue.setColor(ChatColor.GOLD);
        period.setColor(ChatColor.AQUA);
        
        commandSenderPlayer.sendMessage(thisIsTheLegacyVanishCommand);
        commandSenderPlayer.sendMessage(theNewCommandsAre, hideCommandValue, and, showCommandValue, period);
    }
}
