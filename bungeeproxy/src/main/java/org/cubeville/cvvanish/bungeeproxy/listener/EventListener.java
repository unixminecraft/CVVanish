package org.cubeville.cvvanish.bungeeproxy.listener;

import org.cubeville.cvplayerdata.bungeecord.CVPlayerData;
import org.cubeville.cvplayerdata.bungeecord.exception.PlayerDataNotFoundException;
import org.cubeville.cvvanish.bungeeproxy.CVVanish;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class EventListener implements Listener {
    
    private static final String SILENT_JOIN_PERMISSION = "cvvanish.silent.join";
    private static final String SILENT_LEAVE_PERMISSION = "cvvanish.silent.leave";
    private static final String SILENT_VIEW_PERMISSION = "cvvanish.silent.notify";
    
    private CVPlayerData playerDataPlugin;
    private CVVanish vanishPlugin;
    private ProxyServer proxyServer;
    
    public EventListener(CVVanish vanishPlugin, CVPlayerData playerDataPlugin) {
        
        this.vanishPlugin = vanishPlugin;
        this.proxyServer = this.vanishPlugin.getProxy();
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDisconnectEvent(PlayerDisconnectEvent playerDisconnectEvent) {
        
        ProxiedPlayer disconnectingPlayer = playerDisconnectEvent.getPlayer();
        
        TextComponent playerNameValue = new TextComponent();
        TextComponent leftTheGame = new TextComponent();
        
        playerNameValue.setText(disconnectingPlayer.getName());
        
        if(disconnectingPlayer.hasPermission(SILENT_LEAVE_PERMISSION)) {
            
            leftTheGame.setText(" left the game silently.");
            
            playerNameValue.setColor(ChatColor.DARK_AQUA);
            leftTheGame.setColor(ChatColor.DARK_AQUA);
            
            for(ProxiedPlayer onlinePlayer : proxyServer.getPlayers()) {
                if(onlinePlayer.hasPermission(SILENT_VIEW_PERMISSION)) {
                    onlinePlayer.sendMessage(playerNameValue, leftTheGame);
                }
            }
        }
        else {
            
            leftTheGame.setText(" left the game.");
            
            playerNameValue.setColor(ChatColor.YELLOW);
            leftTheGame.setColor(ChatColor.YELLOW);
            
            for(ProxiedPlayer onlinePlayer : proxyServer.getPlayers()) {
                onlinePlayer.sendMessage(playerNameValue, leftTheGame);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPostLoginEvent(PostLoginEvent postLoginEvent) {
        
        ProxiedPlayer loggedInPlayer = postLoginEvent.getPlayer();
        String playerName = loggedInPlayer.getName();
        
        boolean isPlayerNew = playerDataPlugin.isPlayerNew(loggedInPlayer);
        boolean canJoinSilently = loggedInPlayer.hasPermission(SILENT_JOIN_PERMISSION);
        boolean hasNameChanged = false;
        
        if(!isPlayerNew) {
            try {
                hasNameChanged = playerDataPlugin.hasNameChanged(loggedInPlayer);
            }
            catch(PlayerDataNotFoundException e) {
                
                //TODO: Log error.
                hasNameChanged = false;
            }
        }
        
        if(isPlayerNew) {
            
            playerDataPlugin.onPlayerFirstJoin(loggedInPlayer);
            
            if(canJoinSilently) {
                
                TextComponent playerNameValue = new TextComponent();
                TextComponent isANewPlayer = new TextComponent();
                TextComponent joinedTheGame = new TextComponent();
                
                playerNameValue.setText(playerName);
                isANewPlayer.setText(" is a new player ");
                joinedTheGame.setText("and has joined the game silently.");
                
                playerNameValue.setColor(ChatColor.DARK_AQUA);
                isANewPlayer.setColor(ChatColor.AQUA);
                joinedTheGame.setColor(ChatColor.DARK_AQUA);
                
                for(ProxiedPlayer onlinePlayer : proxyServer.getPlayers()) {
                    if(onlinePlayer.hasPermission(SILENT_VIEW_PERMISSION)) {
                        onlinePlayer.sendMessage(playerNameValue, isANewPlayer, joinedTheGame);
                    }
                }
            }
            else {
                
                TextComponent everyoneWelcome = new TextComponent();
                TextComponent playerNameValue = new TextComponent();
                TextComponent exclamationMark = new TextComponent();
                
                everyoneWelcome.setText("Everyone welcome Cubeville's newest player, ");
                playerNameValue.setText(playerName);
                exclamationMark.setText("!");
                
                everyoneWelcome.setColor(ChatColor.YELLOW);
                playerNameValue.setColor(ChatColor.GOLD);
                exclamationMark.setColor(ChatColor.YELLOW);
                
                for(ProxiedPlayer onlinePlayer : proxyServer.getPlayers()) {
                    onlinePlayer.sendMessage(everyoneWelcome, playerNameValue, exclamationMark);
                }
            }
        }
        else if(hasNameChanged) {
            
            String oldPlayerName = "";
            try {
                oldPlayerName = playerDataPlugin.changePlayerName(loggedInPlayer);
            }
            catch(PlayerDataNotFoundException e) {
                
                //TODO: Log error.
                if(canJoinSilently) {
                    
                    TextComponent playerNameValue = new TextComponent();
                    TextComponent joinedTheGame = new TextComponent();
                    
                    playerNameValue.setText(playerName);
                    joinedTheGame.setText(" joined the game silently.");
                    
                    playerNameValue.setColor(ChatColor.DARK_AQUA);
                    joinedTheGame.setColor(ChatColor.DARK_AQUA);
                    
                    for(ProxiedPlayer onlinePlayer : proxyServer.getPlayers()) {
                        if(onlinePlayer.hasPermission(SILENT_VIEW_PERMISSION)) {
                            onlinePlayer.sendMessage(playerNameValue, joinedTheGame);
                        }
                    }
                }
                else {
                    
                    TextComponent playerNameValue = new TextComponent();
                    TextComponent joinedTheGame = new TextComponent();
                    
                    playerNameValue.setText(playerName);
                    joinedTheGame.setText(" joined the game.");
                    
                    playerNameValue.setColor(ChatColor.YELLOW);
                    joinedTheGame.setColor(ChatColor.YELLOW);
                    
                    for(ProxiedPlayer onlinePlayer : proxyServer.getPlayers()) {
                        onlinePlayer.sendMessage(playerNameValue, joinedTheGame);
                    }
                }
                
                return;
                
            }
            
            if(canJoinSilently) {
                
                TextComponent newPlayerNameValue = new TextComponent();
                TextComponent formerlyKnownAs = new TextComponent();
                TextComponent oldPlayerNameValue = new TextComponent();
                TextComponent closeParenthesis = new TextComponent();
                TextComponent joinedTheGame = new TextComponent();
                
                newPlayerNameValue.setText(playerName);
                formerlyKnownAs.setText(" (formerly known as ");
                oldPlayerNameValue.setText(oldPlayerName);
                closeParenthesis.setText(")");
                joinedTheGame.setText(" joined the game silently.");
                
                newPlayerNameValue.setColor(ChatColor.DARK_AQUA);
                formerlyKnownAs.setColor(ChatColor.AQUA);
                oldPlayerNameValue.setColor(ChatColor.DARK_AQUA);
                closeParenthesis.setColor(ChatColor.AQUA);
                joinedTheGame.setColor(ChatColor.DARK_AQUA);
                
                for(ProxiedPlayer onlinePlayer : proxyServer.getPlayers()) {
                    if(onlinePlayer.hasPermission(SILENT_VIEW_PERMISSION)) {
                        onlinePlayer.sendMessage(newPlayerNameValue, formerlyKnownAs, oldPlayerNameValue, closeParenthesis, joinedTheGame);
                    }
                }
            }
            else {
                
                TextComponent newPlayerNameValue = new TextComponent();
                TextComponent formerlyKnownAs = new TextComponent();
                TextComponent oldPlayerNameValue = new TextComponent();
                TextComponent joinedTheGame = new TextComponent();
                
                newPlayerNameValue.setText(playerName);
                formerlyKnownAs.setText(" (formerly known as ");
                oldPlayerNameValue.setText(oldPlayerName);
                joinedTheGame.setText(") joined the game silently.");
                
                newPlayerNameValue.setColor(ChatColor.YELLOW);
                formerlyKnownAs.setColor(ChatColor.YELLOW);
                oldPlayerNameValue.setColor(ChatColor.YELLOW);
                joinedTheGame.setColor(ChatColor.YELLOW);
                
                for(ProxiedPlayer onlinePlayer : proxyServer.getPlayers()) {
                    onlinePlayer.sendMessage(newPlayerNameValue, formerlyKnownAs, oldPlayerNameValue, joinedTheGame);
                }
            }
        }
        else {
            if(canJoinSilently) {
                
                TextComponent playerNameValue = new TextComponent();
                TextComponent joinedTheGame = new TextComponent();
                
                playerNameValue.setText(playerName);
                joinedTheGame.setText(" joined the game silently.");
                
                playerNameValue.setColor(ChatColor.DARK_AQUA);
                joinedTheGame.setColor(ChatColor.DARK_AQUA);
                
                for(ProxiedPlayer onlinePlayer : proxyServer.getPlayers()) {
                    if(onlinePlayer.hasPermission(SILENT_VIEW_PERMISSION)) {
                        onlinePlayer.sendMessage(playerNameValue, joinedTheGame);
                    }
                }
            }
            else {
                
                TextComponent playerNameValue = new TextComponent();
                TextComponent joinedTheGame = new TextComponent();
                
                playerNameValue.setText(playerName);
                joinedTheGame.setText(" joined the game.");
                
                playerNameValue.setColor(ChatColor.YELLOW);
                joinedTheGame.setColor(ChatColor.YELLOW);
                
                for(ProxiedPlayer onlinePlayer : proxyServer.getPlayers()) {
                    onlinePlayer.sendMessage(playerNameValue, joinedTheGame);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProxyPingEvent(ProxyPingEvent proxyPingEvent) {
        
        ServerPing serverPing = proxyPingEvent.getResponse();
        int playerCount = 0;
        
        for(ProxiedPlayer onlinePlayer : vanishPlugin.getProxy().getPlayers()) {
            if(vanishPlugin.isPlayerFullyVisible(onlinePlayer.getUniqueId())) {
                playerCount++;
            }
        }
        
        serverPing.getPlayers().setOnline(playerCount);
        serverPing.getPlayers().setSample(new ServerPing.PlayerInfo[0]);
    }
}
