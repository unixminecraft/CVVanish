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
        
        TextComponent playerNameValue = new TextComponent();
        TextComponent leftTheGame = new TextComponent();
        TextComponent isStillHere = new TextComponent();
        
        playerNameValue.setText(commandSenderPlayer.getName());
        leftTheGame.setText(" left the game.");
        isStillHere.setText(" is still here, they did not acutally leave.");
        
        playerNameValue.setColor(ChatColor.YELLOW);
        leftTheGame.setColor(ChatColor.YELLOW);
        isStillHere.setColor(ChatColor.DARK_AQUA);
        
        vanishPlugin.sendMessageAll(playerNameValue, leftTheGame);
        
        playerNameValue.setColor(ChatColor.DARK_AQUA);
        
        vanishPlugin.sendMessageWithPermission(NOTIFY_PERMISSION, playerNameValue, isStillHere);
    }
}
