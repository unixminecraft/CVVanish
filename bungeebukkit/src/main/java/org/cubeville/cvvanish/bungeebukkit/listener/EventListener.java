package org.cubeville.cvvanish.bungeebukkit.listener;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.cubeville.cvvanish.bungeebukkit.CVVanish;

@SuppressWarnings("deprecation")
public class EventListener implements Listener {

    private CVVanish vanishPlugin;
    
    public EventListener(CVVanish vanishPlugin) {
        this.vanishPlugin = vanishPlugin;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageEvent(EntityDamageEvent entityDamageEvent) {
        
        Entity damagedEntity = entityDamageEvent.getEntity();
        if(!(damagedEntity instanceof Player)) {
            return;
        }
        
        UUID damagedPlayerId = ((Player) damagedEntity).getUniqueId();
        if(!vanishPlugin.isPlayerHidden(damagedPlayerId)) {
            return;
        }
        
        entityDamageEvent.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityPickupItemEvent(EntityPickupItemEvent entityPickupItemEvent) {
        
        LivingEntity pickingUpLivingEntity = entityPickupItemEvent.getEntity();
        if(!(pickingUpLivingEntity instanceof Player)) {
            return;
        }
        
        UUID pickingUpPlayerId = ((Player) pickingUpLivingEntity).getUniqueId();
        if(!vanishPlugin.isPlayerHidden(pickingUpPlayerId)) {
            return;
        }
        
        if(vanishPlugin.isPlayerPickupEnabled(pickingUpPlayerId)) {
            return;
        }
        
        entityPickupItemEvent.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityTargetEvent(EntityTargetEvent entityTargetEvent) {
        
        Entity targetedEntity = entityTargetEvent.getTarget();
        if(!(targetedEntity instanceof Player)) {
            return;
        }
        
        UUID targetedPlayerId = ((Player) targetedEntity).getUniqueId();
        if(!vanishPlugin.isPlayerHidden(targetedPlayerId)) {
            return;
        }
        
        entityTargetEvent.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFoodLevelChangeEvent(FoodLevelChangeEvent foodLevelChangeEvent) {
        
        HumanEntity foodChangedHumanEntity = foodLevelChangeEvent.getEntity();
        if(!(foodChangedHumanEntity instanceof Player)) {
            return;
        }
        
        UUID foodChangedPlayerId = ((Player) foodChangedHumanEntity).getUniqueId();
        if(!vanishPlugin.isPlayerHidden(foodChangedPlayerId)) {
            return;
        }
        
        foodLevelChangeEvent.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileHitEvent(ProjectileHitEvent projectileHitEvent) {
        
        Entity hitEntity = projectileHitEvent.getHitEntity();
        if(hitEntity == null) {
            return;
        }
        
        if(!(hitEntity instanceof Player)) {
            return;
        }
        
        UUID hitPlayerId = ((Player) hitEntity).getUniqueId();
        if(!vanishPlugin.isPlayerHidden(hitPlayerId)) {
            return;
        }
        
        //TODO: No .setCancelled() method exists.
        //projectileHitEvent.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEvent(PlayerInteractEvent playerInteractEvent) {
        
        UUID interactingPlayerId = playerInteractEvent.getPlayer().getUniqueId();
        if(!vanishPlugin.isPlayerHidden(interactingPlayerId)) {
            return;
        }
        
        if(playerInteractEvent.getAction() == Action.PHYSICAL) {
            playerInteractEvent.setCancelled(true);
            return;
        }
    }
    
    @SuppressWarnings("unchecked")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLoginEvent(PlayerLoginEvent playerLoginEvent) {
        
        Player loginPlayer = playerLoginEvent.getPlayer();
        UUID loginPlayerId = loginPlayer.getUniqueId();
        Server minecraftServer = vanishPlugin.getServer();
        Collection<Player> allOnlinePlayers = (Collection<Player>) minecraftServer.getOnlinePlayers();
        
        if(vanishPlugin.isPlayerHidden(loginPlayerId)) {
            for(Player otherOnlinePlayer : allOnlinePlayers) {
                
                if(otherOnlinePlayer.getUniqueId().equals(loginPlayerId)) {
                    continue;
                }
                if(otherOnlinePlayer.hasPermission("cvvanish.see")) {
                    continue;
                }
                otherOnlinePlayer.hidePlayer(vanishPlugin, loginPlayer);
            }
        }
        
        if(loginPlayer.hasPermission("cvvanish.see")) {
            return;
        }
        
        for(Player otherOnlinePlayer : allOnlinePlayers) {
            if(vanishPlugin.isPlayerHidden(otherOnlinePlayer.getUniqueId())) {
                loginPlayer.hidePlayer(vanishPlugin, otherOnlinePlayer);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent playerPickupItemEvent) {
        
        UUID pickingUpPlayerId = playerPickupItemEvent.getPlayer().getUniqueId();
        if(!vanishPlugin.isPlayerHidden(pickingUpPlayerId)) {
            return;
        }
        
        if(vanishPlugin.isPlayerPickupEnabled(pickingUpPlayerId)) {
            return;
        }
        
        playerPickupItemEvent.setCancelled(true);
    }
}
