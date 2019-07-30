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

public class HiddenNotifier implements Runnable {
    
    private CVVanish vanishPlugin;
    private ScheduledTask hiddenNotificationScheduledTask;
    private TaskScheduler proxyTaskScheduler;
    
    public HiddenNotifier(CVVanish vanishPlugin) {
        
        this.vanishPlugin = vanishPlugin;
        proxyTaskScheduler = this.vanishPlugin.getProxy().getScheduler();
    }
    
    public void start() {
        hiddenNotificationScheduledTask = proxyTaskScheduler.schedule(vanishPlugin, this, 0, 2, TimeUnit.SECONDS);
    }
    
    public void stop() {
        proxyTaskScheduler.cancel(hiddenNotificationScheduledTask);
    }
    
    @Override
    public void run() {
        
        HashSet<UUID> hiddenPlayerIds = vanishPlugin.getHiddenPlayerIds();
        ProxyServer bungeeProxy = vanishPlugin.getProxy();
        
        for(UUID hiddenPlayerId : hiddenPlayerIds) {
            
            ProxiedPlayer hiddenPlayer = bungeeProxy.getPlayer(hiddenPlayerId);
            if(hiddenPlayer == null) {
                //TODO: Log error.
                continue;
            }
            
            TextComponent youAreInvisible = new TextComponent();
            youAreInvisible.setText("You are invisible to other players.");
            youAreInvisible.setColor(ChatColor.GREEN);
            hiddenPlayer.sendMessage(ChatMessageType.ACTION_BAR, youAreInvisible);
        }
    }
}
