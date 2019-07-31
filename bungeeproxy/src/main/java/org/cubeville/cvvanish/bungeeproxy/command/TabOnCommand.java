package org.cubeville.cvvanish.bungeeproxy.command;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.cubeville.common.bungeecord.command.PlayerCommand;
import org.cubeville.cvvanish.bungeeproxy.CVVanish;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TabOnCommand extends PlayerCommand {
    
    private static final String NOTIFY_PERMISSION = "cvvanish.tabon.notify";
    private static final String USE_PERMISSION = "cvvanish.tabon.use";
    
    private CVVanish vanishPlugin;
    
    public TabOnCommand(CVVanish vanishPlugin) {
        
        super("ton");
        this.vanishPlugin = vanishPlugin;
        setMinimumPermissionToUse(USE_PERMISSION);
        setNoPermissionMessage("Unknown command. Type \"/help\" for help.", ChatColor.WHITE);
        setSyntax("Syntax: /ton");
    }
    
    @Override
    public void execute(ProxiedPlayer commandSenderPlayer, Set<String> flags, List<String> arguments) {
        
        UUID commandSenderPlayerId = commandSenderPlayer.getUniqueId();
        if(vanishPlugin.isPlayerFullyVisible(commandSenderPlayerId) || vanishPlugin.isPlayerVanished(commandSenderPlayerId)) {
            
            TextComponent youAreInTabAlready = new TextComponent();
            youAreInTabAlready.setText("You are already listed in tab.");
            youAreInTabAlready.setColor(ChatColor.RED);
            commandSenderPlayer.sendMessage(youAreInTabAlready);
            return;
        }
        
        if(!vanishPlugin.relistPlayer(commandSenderPlayerId)) {
            
            //TODO: Log error.
            TextComponent internalError = new TextComponent();
            internalError.setText("Internal error, please try again. If the issue persists, please contact a server administrator.");
            internalError.setColor(ChatColor.RED);
            commandSenderPlayer.sendMessage(internalError);
            return;
        }
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("[HH:mm:ss]");
        String formattedTimeNow = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        
        TextComponent youAreNow = new TextComponent();
        TextComponent hiddenStatusValue = new TextComponent();
        TextComponent period = new TextComponent();
        TextComponent timeNowValue = new TextComponent();
        TextComponent playerNameValue = new TextComponent();
        TextComponent isNow = new TextComponent();
        
        youAreNow.setText("You are now ");
        period.setText(". ");
        timeNowValue.setText(formattedTimeNow);
        playerNameValue.setText(commandSenderPlayer.getName());
        isNow.setText(" is now ");
        
        youAreNow.setColor(ChatColor.GREEN);
        period.setColor(ChatColor.GREEN);
        timeNowValue.setColor(ChatColor.GREEN);
        playerNameValue.setColor(ChatColor.DARK_AQUA);
        isNow.setColor(ChatColor.DARK_AQUA);
        
        boolean fullyVisible = vanishPlugin.isPlayerFullyVisible(commandSenderPlayerId);
        boolean vanished = vanishPlugin.isPlayerVanished(commandSenderPlayerId);
        
        if(fullyVisible) {
            
            hiddenStatusValue.setText("fully visible");
            hiddenStatusValue.setColor(ChatColor.GREEN);
            
            commandSenderPlayer.sendMessage(youAreNow, hiddenStatusValue, period, timeNowValue);
            
            hiddenStatusValue.setColor(ChatColor.DARK_AQUA);
            period.setColor(ChatColor.DARK_AQUA);
            
            vanishPlugin.sendMessageWithPermission(NOTIFY_PERMISSION, playerNameValue, isNow, hiddenStatusValue, period);
        }
        else if(vanished) {
            
            hiddenStatusValue.setText("invisible, but listed in tab");
            hiddenStatusValue.setColor(ChatColor.GREEN);
            
            commandSenderPlayer.sendMessage(youAreNow, hiddenStatusValue, period, timeNowValue);
            
            hiddenStatusValue.setColor(ChatColor.DARK_AQUA);
            period.setColor(ChatColor.DARK_AQUA);
            
            vanishPlugin.sendMessageWithPermission(NOTIFY_PERMISSION, playerNameValue, isNow, hiddenStatusValue, period);
        }
        else {
            
            //TODO: Log error.
            
            TextComponent unlikelyErrorOccurred = new TextComponent();
            unlikelyErrorOccurred.setText("An unlikely error has occurred. Please report this to a server administrator, so they can look into it.");
            unlikelyErrorOccurred.setColor(ChatColor.RED);
            commandSenderPlayer.sendMessage(unlikelyErrorOccurred);
        }
    }
}