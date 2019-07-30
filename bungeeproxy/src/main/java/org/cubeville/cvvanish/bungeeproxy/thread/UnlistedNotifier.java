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

public class UnlistedNotifier implements Runnable {
    
    private CVVanish vanishPlugin;
    private ScheduledTask unlistedNotificationScheduledTask;
    private TaskScheduler proxyTaskScheduler;
    
    public UnlistedNotifier(CVVanish vanishPlugin) {
        
        this.vanishPlugin = vanishPlugin;
        this.proxyTaskScheduler = this.vanishPlugin.getProxy().getScheduler();
    }
    
    public void start() {
        unlistedNotificationScheduledTask = proxyTaskScheduler.schedule(vanishPlugin, this, 0, 2, TimeUnit.SECONDS);
    }
    
    public void stop() {
        proxyTaskScheduler.cancel(unlistedNotificationScheduledTask);
    }
    
    @Override
    public void run() {
        
        HashSet<UUID> unlistedPlayerIds = vanishPlugin.getUnlistedPlayerIds();
        ProxyServer bungeeProxy = vanishPlugin.getProxy();
        
        for(UUID unlistedPlayerId : unlistedPlayerIds) {
            
            ProxiedPlayer unlistedPlayer = bungeeProxy.getPlayer(unlistedPlayerId);
            if(unlistedPlayer == null) {
                //TODO: Log error.
                continue;
            }
            
            TextComponent youAreLocallyVisible = new TextComponent();
            youAreLocallyVisible.setText("You are able to be seen, but you are not listed in tab.");
            youAreLocallyVisible.setColor(ChatColor.GOLD);
            unlistedPlayer.sendMessage(ChatMessageType.ACTION_BAR, youAreLocallyVisible);
        }
    }
}
