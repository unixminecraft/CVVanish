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

public class ShowCommand extends PlayerCommand {
    
    private static final String NOTIFY_PERMISSION = "cvvanish.show.notify";
    private static final String USE_PERMISSION = "cvvanish.show.use";
    
    private CVVanish vanishPlugin;
    
    public ShowCommand(CVVanish vanishPlugin) {
        
        super("show");
        this.vanishPlugin = vanishPlugin;
        setMinimumPermissionToUse(USE_PERMISSION);
        setNoPermissionMessage("Unknown command. Type \"/help\" for help.", ChatColor.WHITE);
        setSyntax("Syntax: /show [fj]");
        addFlag("fj");
    }
    
    @Override
    public void execute(ProxiedPlayer commandSenderPlayer, Set<String> flags, List<String> arguments) {
        
        boolean fakeJoin = flags.contains("fj");
        
        UUID commandSenderPlayerId = commandSenderPlayer.getUniqueId();
        if(vanishPlugin.isPlayerFullyVisible(commandSenderPlayerId)) {
            
            TextComponent youCantBecomeMoreVisible = new TextComponent();
            youCantBecomeMoreVisible.setText("No. We won't make you more visible than you already are. It's impossible.");
            youCantBecomeMoreVisible.setColor(ChatColor.RED);
            commandSenderPlayer.sendMessage(youCantBecomeMoreVisible);
            return;
        }
        
        if(!vanishPlugin.showPlayer(commandSenderPlayerId)) {
            
            //TODO: Log error.
            TextComponent internalError = new TextComponent();
            internalError.setText("Internal error, please try again. If the issue persists, please contact a server administrator.");
            internalError.setColor(ChatColor.RED);
            commandSenderPlayer.sendMessage(internalError);
            return;
        }
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("[HH:mm:ss]");
        String formattedTimeNow = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        
        TextComponent youAreNoLongerHidden = new TextComponent();
        TextComponent timeNowValue = new TextComponent();
        TextComponent playerNameValue = new TextComponent();
        TextComponent isNowFullyVisible = new TextComponent();
        
        youAreNoLongerHidden.setText("You are no longer hidden. ");
        timeNowValue.setText(formattedTimeNow);
        playerNameValue.setText(commandSenderPlayer.getName());
        isNowFullyVisible.setText(" is now fully visible.");
        
        youAreNoLongerHidden.setColor(ChatColor.GREEN);
        timeNowValue.setColor(ChatColor.GREEN);
        playerNameValue.setColor(ChatColor.DARK_AQUA);
        isNowFullyVisible.setColor(ChatColor.DARK_AQUA);
        
        commandSenderPlayer.sendMessage(youAreNoLongerHidden, timeNowValue);
        
        vanishPlugin.sendMessageWithPermission(NOTIFY_PERMISSION, playerNameValue, isNowFullyVisible);
        
        if(!fakeJoin) {
            return;
        }
        
        vanishPlugin.getProxy().getPluginManager().dispatchCommand(commandSenderPlayer, "fj");
    }
}
