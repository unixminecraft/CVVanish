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

public class HideCommand extends PlayerCommand {
    
    private static final String NOTIFY_PERMISSION = "cvvanish.hide.notify";
    private static final String USE_PERMISSION = "cvvanish.hide.use";
    
    private CVVanish vanishPlugin;
    
    public HideCommand(CVVanish vanishPlugin) {
        
        super("hide");
        this.vanishPlugin = vanishPlugin;
        setMinimumPermissionToUse(USE_PERMISSION);
        setNoPermissionMessage("Unknown command. Type \"/help\" for help.", ChatColor.WHITE);
        setSyntax("Syntax: /hide [fq]");
        addFlag("fq");
    }
    
    @Override
    public void execute(ProxiedPlayer commandSenderPlayer, Set<String> flags, List<String> arguments) {
        
        boolean fakeQuit = flags.contains("fq");
        
        UUID commandSenderPlayerId = commandSenderPlayer.getUniqueId();
        if(vanishPlugin.isPlayerHidden(commandSenderPlayerId)) {
            
            TextComponent youCantHideMore = new TextComponent();
            
            youCantHideMore.setText("You can't be any more hidden than you already are.");
            youCantHideMore.setColor(ChatColor.RED);
            
            commandSenderPlayer.sendMessage(youCantHideMore);
            return;
        }
        
        if(!vanishPlugin.hidePlayer(commandSenderPlayerId)) {
            
            //TODO: Log error.
            TextComponent internalError = new TextComponent();
            
            internalError.setText("Internal error, please try again. If the issue persists, please contact a server administrator.");
            internalError.setColor(ChatColor.RED);
            
            commandSenderPlayer.sendMessage(internalError);
            return;
        }
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("[HH:mm:ss]");
        String formattedTimeNow = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        
        TextComponent youAreHidden = new TextComponent();
        TextComponent timeNowValue = new TextComponent();
        TextComponent playerNameValue = new TextComponent();
        TextComponent isNowHidden = new TextComponent();
        
        youAreHidden.setText("You are hidden. ");
        timeNowValue.setText(formattedTimeNow);
        playerNameValue.setText(commandSenderPlayer.getName());
        isNowHidden.setText(" is now fully hidden.");
        
        youAreHidden.setColor(ChatColor.GREEN);
        timeNowValue.setColor(ChatColor.GREEN);
        playerNameValue.setColor(ChatColor.DARK_AQUA);
        isNowHidden.setColor(ChatColor.DARK_AQUA);
        
        commandSenderPlayer.sendMessage(youAreHidden, timeNowValue);
        
        vanishPlugin.sendMessageWithPermission(NOTIFY_PERMISSION, playerNameValue, isNowHidden);
        
        if(!fakeQuit) {
            return;
        }
        
        vanishPlugin.getProxy().getPluginManager().dispatchCommand(commandSenderPlayer, "fq");
    }
}
