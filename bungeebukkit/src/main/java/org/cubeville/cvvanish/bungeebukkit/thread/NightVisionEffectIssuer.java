package org.cubeville.cvvanish.bungeebukkit.thread;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cubeville.cvvanish.bungeebukkit.CVVanish;

public class NightVisionEffectIssuer implements Runnable {

    private CVVanish vanishPlugin;
    private int nightVisionScheduledTaskIdNumber;
    
    public NightVisionEffectIssuer(CVVanish vanishPlugin) {
        this.vanishPlugin = vanishPlugin;
    }
    
    public void start() {
        nightVisionScheduledTaskIdNumber = vanishPlugin.getServer().getScheduler().scheduleSyncRepeatingTask(vanishPlugin, this, 2400, 2400);
    }
    
    public void stop() {
        
        if(nightVisionScheduledTaskIdNumber != -1) {
            vanishPlugin.getServer().getScheduler().cancelTask(nightVisionScheduledTaskIdNumber);
        }
    }
    
    @Override
    public void run() {
        
        Server bukkitServer = vanishPlugin.getServer();
        HashSet<UUID> hiddenPlayerIds = vanishPlugin.getVanishedPlayerIds();
        
        for(UUID hiddenPlayerId : hiddenPlayerIds) {
            
            Player hiddenPlayer = bukkitServer.getPlayer(hiddenPlayerId);
            if(hiddenPlayer == null) {
                continue;
            }
            
            PotionEffect nightVisionPotionEffect = new PotionEffect(PotionEffectType.NIGHT_VISION, 2400, 1);
            hiddenPlayer.removePotionEffect(PotionEffectType.NIGHT_VISION);
            hiddenPlayer.addPotionEffect(nightVisionPotionEffect);
        }
    }
}
