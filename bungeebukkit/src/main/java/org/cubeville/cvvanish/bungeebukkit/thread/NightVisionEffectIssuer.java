/*
 * CVVanish Copyright (C) 2019 Cubeville
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package org.cubeville.cvvanish.bungeebukkit.thread;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.cubeville.cvvanish.bungeebukkit.CVVanish;

public final class NightVisionEffectIssuer implements Runnable {

    private final CVVanish vanishPlugin;
    
    private final Server bukkitServer;
    private final BukkitScheduler bukkitScheduler;
    
    private int nightVisionScheduledTaskIdNumber;
    
    public NightVisionEffectIssuer(final CVVanish vanishPlugin) {
    	
        this.vanishPlugin = vanishPlugin;
        
        this.bukkitServer = vanishPlugin.getServer();
        this.bukkitScheduler = this.bukkitServer.getScheduler();
        
    }
    
    public void start() {
    	
        nightVisionScheduledTaskIdNumber = bukkitScheduler.scheduleSyncRepeatingTask(vanishPlugin, this, 2400, 2400);
    }
    
    public void stop() {
        
        if(nightVisionScheduledTaskIdNumber != -1) {
        	bukkitScheduler.cancelTask(nightVisionScheduledTaskIdNumber);
        }
    }
    
    @Override
    public void run() {
        
    	final HashSet<UUID> hiddenPlayerIds = vanishPlugin.getVanishedPlayerIds();
        
        for(final UUID hiddenPlayerId : hiddenPlayerIds) {
            
        	final Player hiddenPlayer = bukkitServer.getPlayer(hiddenPlayerId);
            if(hiddenPlayer == null) {
                continue;
            }
            
            final PotionEffect nightVisionPotionEffect = new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1);
            hiddenPlayer.removePotionEffect(PotionEffectType.NIGHT_VISION);
            hiddenPlayer.addPotionEffect(nightVisionPotionEffect);
        }
    }
}
