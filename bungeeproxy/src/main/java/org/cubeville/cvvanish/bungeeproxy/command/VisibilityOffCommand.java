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

public class VisibilityOffCommand extends PlayerCommand {
    
    private static final String NOTIFY_PERMISSION = "cvvanish.visibilityoff.notify";
    private static final String USE_PERMISSION = "cvvanish.visibilityoff.use";
    
    private CVVanish vanishPlugin;
    
    public VisibilityOffCommand(CVVanish vanishPlugin) {
        
        super("voff");
        this.vanishPlugin = vanishPlugin;
        setMinimumPermissionToUse(USE_PERMISSION);
        setNoPermissionMessage("Unknown command. Type \"/help\" for help.", ChatColor.WHITE);
        setSyntax("Syntax: /voff");
    }
    
    @Override
    public void execute(ProxiedPlayer commandSenderPlayer, Set<String> flags, List<String> arguments) {
        
        UUID commandSenderPlayerId = commandSenderPlayer.getUniqueId();
        if(vanishPlugin.isPlayerHidden(commandSenderPlayerId) || vanishPlugin.isPlayerVanished(commandSenderPlayerId)) {
            
            TextComponent youAreAlreadyInvisible = new TextComponent();
            youAreAlreadyInvisible.setText("You are already unable to be seen.");
            youAreAlreadyInvisible.setColor(ChatColor.RED);
            commandSenderPlayer.sendMessage(youAreAlreadyInvisible);
            return;
        }
        
        if(!vanishPlugin.vanishPlayer(commandSenderPlayerId)) {
            
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
        
        boolean hidden = vanishPlugin.isPlayerHidden(commandSenderPlayerId);
        boolean vanished = vanishPlugin.isPlayerVanished(commandSenderPlayerId);
        
        if(hidden) {
            
            hiddenStatusValue.setText("fully hidden");
            hiddenStatusValue.setColor(ChatColor.GREEN);
            
            commandSenderPlayer.sendMessage(youAreNow, hiddenStatusValue, period, timeNowValue);
            
            hiddenStatusValue.setColor(ChatColor.DARK_AQUA);
            period.setColor(ChatColor.DARK_AQUA);
            
            for(ProxiedPlayer onlinePlayer : vanishPlugin.getProxy().getPlayers()) {
                if(onlinePlayer.hasPermission(NOTIFY_PERMISSION)) {
                    onlinePlayer.sendMessage(playerNameValue, isNow, hiddenStatusValue, period);
                }
            }
        }
        else if(vanished) {
            
            hiddenStatusValue.setText("not able to be seen, but listed in tab");
            hiddenStatusValue.setColor(ChatColor.GREEN);
            
            commandSenderPlayer.sendMessage(youAreNow, hiddenStatusValue, period, timeNowValue);
            
            hiddenStatusValue.setColor(ChatColor.DARK_AQUA);
            period.setColor(ChatColor.DARK_AQUA);
            
            for(ProxiedPlayer onlinePlayer : vanishPlugin.getProxy().getPlayers()) {
                if(onlinePlayer.hasPermission(NOTIFY_PERMISSION)) {
                    onlinePlayer.sendMessage(playerNameValue, isNow, hiddenStatusValue, period);
                }
            }
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
