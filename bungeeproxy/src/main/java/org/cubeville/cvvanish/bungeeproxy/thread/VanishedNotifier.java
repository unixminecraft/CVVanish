package org.cubeville.cvvanish.bungeeproxy.thread;

import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.cubeville.cvvanish.bungeeproxy.CVVanish;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.scheduler.TaskScheduler;

public class VanishedNotifier implements Runnable {
    
    private CVVanish vanishPlugin;
    private ScheduledTask vanishedNotificationScheduledTask;
    private TaskScheduler proxyTaskScheduler;
    
    public VanishedNotifier(CVVanish vanishPlugin) {
        
        this.vanishPlugin = vanishPlugin;
        proxyTaskScheduler = this.vanishPlugin.getProxy().getScheduler();
    }
    
    public void start() {
        vanishedNotificationScheduledTask = proxyTaskScheduler.schedule(vanishPlugin, this, 0, 2, TimeUnit.SECONDS);
    }
    
    public void stop() {
        proxyTaskScheduler.cancel(vanishedNotificationScheduledTask);
    }
    
    @Override
    public void run() {
        
        HashSet<UUID> vanishedPlayerIds = vanishPlugin.getVanishedPlayerIds();
        ProxyServer bungeeProxy = vanishPlugin.getProxy();
        
        for(UUID vanishedPlayerId : vanishedPlayerIds) {
            
            ProxiedPlayer vanishedPlayer = bungeeProxy.getPlayer(vanishedPlayerId);
            if(vanishedPlayer == null) {
                //TODO: Log error.
                continue;
            }
            
            TextComponent youAreListed = new TextComponent();
            youAreListed.setText("You are unable to be seen, but you are listed in tab.");
            youAreListed.setColor(ChatColor.GOLD);
            vanishedPlayer.sendMessage(ChatMessageType.ACTION_BAR, youAreListed);
        }
    }
}
