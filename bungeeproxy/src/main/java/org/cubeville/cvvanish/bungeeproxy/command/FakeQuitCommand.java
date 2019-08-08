package org.cubeville.cvvanish.bungeeproxy.command;

import java.util.List;
import java.util.Set;

import org.cubeville.common.bungeecord.command.PlayerCommand;
import org.cubeville.cvvanish.bungeeproxy.CVVanish;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class FakeQuitCommand extends PlayerCommand {
    
    private static final String NOTIFY_PERMISSION = "cvvanish.fakequit.notify";
    private static final String USE_PERMISSION = "cvvanish.fakequit.use";
    
    private CVVanish vanishPlugin;
    
    public FakeQuitCommand(CVVanish vanishPlugin) {
        
        super("fq");
        this.vanishPlugin = vanishPlugin;
        setMinimumPermissionToUse(USE_PERMISSION);
        setNoPermissionMessage("Unknown command. Type \"/help\" for help.", ChatColor.WHITE);
        setSyntax("Syntax: /fq");
    }
    
    @Override
    public void execute(ProxiedPlayer commandSenderPlayer, Set<String> flags, List<String> arguments) {
        
        TextComponent playerNameValueAll = new TextComponent();
        TextComponent leftTheGame = new TextComponent();
        TextComponent playerNameValueNotify = new TextComponent();
        TextComponent isStillHere = new TextComponent();
        
        playerNameValueAll.setText(commandSenderPlayer.getName());
        leftTheGame.setText(" left the game.");
        playerNameValueNotify.setText(commandSenderPlayer.getName());
        isStillHere.setText(" is still here, they did not acutally leave.");
        
        playerNameValueAll.setColor(ChatColor.YELLOW);
        leftTheGame.setColor(ChatColor.YELLOW);
        playerNameValueNotify.setColor(ChatColor.DARK_AQUA);
        isStillHere.setColor(ChatColor.DARK_AQUA);
        
        for(ProxiedPlayer onlinePlayer : vanishPlugin.getProxy().getPlayers()) {
            
            onlinePlayer.sendMessage(playerNameValueAll, leftTheGame);
            if(onlinePlayer.hasPermission(NOTIFY_PERMISSION)) {
                onlinePlayer.sendMessage(playerNameValueNotify, isStillHere);
            }
        }
    }
}
