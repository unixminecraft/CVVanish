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
        
        TextComponent playerNameValue = new TextComponent();
        TextComponent joinedTheGame = new TextComponent();
        TextComponent wasAlreadyHere = new TextComponent();
        
        playerNameValue.setText(commandSenderPlayer.getName());
        joinedTheGame.setText(" joined the game.");
        wasAlreadyHere.setText(" was already here, they acutally joined earlier.");
        
        playerNameValue.setColor(ChatColor.YELLOW);
        joinedTheGame.setColor(ChatColor.YELLOW);
        wasAlreadyHere.setColor(ChatColor.DARK_AQUA);
        
        vanishPlugin.sendMessageAll(playerNameValue, joinedTheGame);
        
        playerNameValue.setColor(ChatColor.DARK_AQUA);
        
        vanishPlugin.sendMessageWithPermission(NOTIFY_PERMISSION, playerNameValue, wasAlreadyHere);
    }
}
