package org.cubeville.cvvanish.bungeeproxy.command;

import java.util.List;
import java.util.Set;

import org.cubeville.common.bungeecord.command.PlayerCommand;
import org.cubeville.cvvanish.bungeeproxy.CVVanish;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class FakeJoinCommand extends PlayerCommand {
    
    private static final String NOTIFY_PERMISSION = "cvvanish.fakejoin.notify";
    private static final String USE_PERMISSION = "cvvanish.fakejoin.use";
    
    private CVVanish vanishPlugin;
    
    public FakeJoinCommand(CVVanish vanishPlugin) {
        
        super("fj");
        this.vanishPlugin = vanishPlugin;
        setMinimumPermissionToUse(USE_PERMISSION);
        setNoPermissionMessage("Unknown command. Type \"/help\" for help.", ChatColor.WHITE);
        setSyntax("Syntax: /fj");
    }

    @Override
    public void execute(ProxiedPlayer commandSenderPlayer, Set<String> flags, List<String> arguments) {
        
        TextComponent playerNameValueAll = new TextComponent();
        TextComponent joinedTheGame = new TextComponent();
        TextComponent playerNameValueNotify = new TextComponent();
        TextComponent wasAlreadyHere = new TextComponent();
        
        playerNameValueAll.setText(commandSenderPlayer.getName());
        joinedTheGame.setText(" joined the game.");
        playerNameValueNotify.setText(commandSenderPlayer.getName());
        wasAlreadyHere.setText(" was already here, they acutally joined earlier.");
        
        playerNameValueAll.setColor(ChatColor.YELLOW);
        joinedTheGame.setColor(ChatColor.YELLOW);
        playerNameValueNotify.setColor(ChatColor.DARK_AQUA);
        wasAlreadyHere.setColor(ChatColor.DARK_AQUA);
        
        for(ProxiedPlayer onlinePlayer : vanishPlugin.getProxy().getPlayers()) {
            
            onlinePlayer.sendMessage(playerNameValueAll, joinedTheGame);
            if(onlinePlayer.hasPermission(NOTIFY_PERMISSION)) {
                onlinePlayer.sendMessage(playerNameValueNotify, wasAlreadyHere);
            }
        }
    }
}
