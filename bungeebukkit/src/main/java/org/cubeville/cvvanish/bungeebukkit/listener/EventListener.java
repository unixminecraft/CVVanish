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

package org.cubeville.cvvanish.bungeebukkit.listener;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.cubeville.cvvanish.bungeebukkit.CVVanish;

@SuppressWarnings("deprecation")
public final class EventListener implements Listener {
    
    private static final class PlayerState {
        
        private final GameMode gameMode;
        private final boolean allowedFlight;
        private final boolean flying;
        
        private PlayerState(final GameMode gameMode, final boolean allowedFlight, final boolean flying) {
            
            this.gameMode = gameMode;
            this.allowedFlight = allowedFlight;
            this.flying = flying;
        }
        
        private static PlayerState getPlayerState(final Player player) {
            
            return new PlayerState(player.getGameMode(), player.getAllowFlight(), player.isFlying());
        }
        
        private void setPlayerState(final Player player) {
            
            player.setGameMode(gameMode);
            player.setAllowFlight(allowedFlight);
            player.setFlying(flying);
        }
    }
    
    private final CVVanish vanishPlugin;
    
    private final ConcurrentHashMap<UUID, PlayerState> playerStateInformation;
    
    private final HashSet<InventoryType> silentlyViewableInventoryTypes;
    private final HashSet<Material> interactDisallowedMaterials;
    private final HashSet<Material> silentlyViewableMaterials;
    
    public EventListener(final CVVanish vanishPlugin) {
        
        this.vanishPlugin = vanishPlugin;
        
        playerStateInformation = new ConcurrentHashMap<UUID, PlayerState>();
        
        silentlyViewableInventoryTypes = new HashSet<InventoryType>();
        interactDisallowedMaterials = new HashSet<Material>();
        silentlyViewableMaterials = new HashSet<Material>();
        
        
        
        silentlyViewableInventoryTypes.add(InventoryType.CHEST);
        silentlyViewableInventoryTypes.add(InventoryType.SHULKER_BOX);
        silentlyViewableInventoryTypes.add(InventoryType.BARREL);
        
        
        
        interactDisallowedMaterials.add(Material.ACACIA_PRESSURE_PLATE);
        interactDisallowedMaterials.add(Material.BIRCH_PRESSURE_PLATE);
        interactDisallowedMaterials.add(Material.DARK_OAK_PRESSURE_PLATE);
        interactDisallowedMaterials.add(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
        interactDisallowedMaterials.add(Material.JUNGLE_PRESSURE_PLATE);
        interactDisallowedMaterials.add(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
        interactDisallowedMaterials.add(Material.OAK_PRESSURE_PLATE);
        interactDisallowedMaterials.add(Material.SPRUCE_PRESSURE_PLATE);
        interactDisallowedMaterials.add(Material.STONE_PRESSURE_PLATE);
        interactDisallowedMaterials.add(Material.TRIPWIRE);
        
        
        
        silentlyViewableMaterials.add(Material.CHEST);
        silentlyViewableMaterials.add(Material.TRAPPED_CHEST);
        
        silentlyViewableMaterials.add(Material.BLACK_SHULKER_BOX);
        silentlyViewableMaterials.add(Material.BLUE_SHULKER_BOX);
        silentlyViewableMaterials.add(Material.BROWN_SHULKER_BOX);
        silentlyViewableMaterials.add(Material.CYAN_SHULKER_BOX);
        silentlyViewableMaterials.add(Material.GRAY_SHULKER_BOX);
        silentlyViewableMaterials.add(Material.GREEN_SHULKER_BOX);
        silentlyViewableMaterials.add(Material.LIGHT_BLUE_SHULKER_BOX);
        silentlyViewableMaterials.add(Material.LIGHT_GRAY_SHULKER_BOX);
        silentlyViewableMaterials.add(Material.LIME_SHULKER_BOX);
        silentlyViewableMaterials.add(Material.MAGENTA_SHULKER_BOX);
        silentlyViewableMaterials.add(Material.ORANGE_SHULKER_BOX);
        silentlyViewableMaterials.add(Material.PINK_SHULKER_BOX);
        silentlyViewableMaterials.add(Material.PURPLE_SHULKER_BOX);
        silentlyViewableMaterials.add(Material.RED_SHULKER_BOX);
        silentlyViewableMaterials.add(Material.WHITE_SHULKER_BOX);
        silentlyViewableMaterials.add(Material.YELLOW_SHULKER_BOX);
        
        silentlyViewableMaterials.add(Material.BARREL);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(final EntityDamageEvent event) {
        
    	final Entity entity = event.getEntity();
        if(!(entity instanceof Player)) {
            return;
        }
        
        final UUID playerId = ((Player) entity).getUniqueId();
        if(!vanishPlugin.isVanishEnabled(playerId)) {
            return;
        }
        
        event.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityPickupItem(final EntityPickupItemEvent event) {
        
    	final LivingEntity livingEntity = event.getEntity();
        if(!(livingEntity instanceof Player)) {
            return;
        }
        
        final UUID playerId = ((Player) livingEntity).getUniqueId();
        if(!vanishPlugin.isVanishEnabled(playerId)) {
            return;
        }
        
        if(vanishPlugin.isPickupEnabled(playerId)) {
            return;
        }
        
        event.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityTarget(final EntityTargetEvent event) {
        
    	final Entity entity = event.getTarget();
        if(!(entity instanceof Player)) {
            return;
        }
        
        final UUID playerId = ((Player) entity).getUniqueId();
        if(!vanishPlugin.isVanishEnabled(playerId)) {
            return;
        }
        
        event.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFoodLevelChange(final FoodLevelChangeEvent event) {
        
    	final HumanEntity humanEntity = event.getEntity();
        if(!(humanEntity instanceof Player)) {
            return;
        }
        
        final UUID playerId = ((Player) humanEntity).getUniqueId();
        if(!vanishPlugin.isVanishEnabled(playerId)) {
            return;
        }
        
        event.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(final InventoryClickEvent event) {
        
    	final HumanEntity humanEntity = event.getWhoClicked();
    	
        if(!(humanEntity instanceof Player)) {
            return;
        }
        
        final Player player = (Player) humanEntity;
        final UUID playerId = player.getUniqueId();
        
        if(!vanishPlugin.isVanishEnabled(playerId)) {
            return;
        }
        
        if(!playerStateInformation.containsKey(playerId)) {
            return;
        }
        
        if(player.getGameMode() == GameMode.SPECTATOR) {
            event.setCancelled(false);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClose(final InventoryCloseEvent event) {
        
    	final HumanEntity humanEntity = event.getPlayer();
    	
        if(!(humanEntity instanceof Player)) {
            return;
        }
        
        final Player player = (Player) humanEntity;
        final UUID playerId = player.getUniqueId();
        
        if(!playerStateInformation.containsKey(playerId)) {
            return;
        }
        
        if(!silentlyViewableInventoryTypes.contains(player.getInventory().getType())) {
            return;
        }
        
        final BukkitRunnable bukkitRunnable = new BukkitRunnable() {
            
            @Override
            public void run() {
                
            	final PlayerState playerState = playerStateInformation.get(playerId);
                if(playerState == null) {
                    return;
                }
                
                playerState.setPlayerState(player);
                playerStateInformation.remove(playerId);
            }
        };
        
        bukkitRunnable.runTaskLater(vanishPlugin, 1);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangedWorld(final PlayerChangedWorldEvent event) {
        
    	final Player player = event.getPlayer();
        if(vanishPlugin.isVanishEnabled(player.getUniqueId())) {
            
        	final PotionEffect nightVisionPotionEffect = new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1);
            player.addPotionEffect(nightVisionPotionEffect);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerGameModeChange(final PlayerGameModeChangeEvent event) {
        
    	final UUID playerId = event.getPlayer().getUniqueId();
    	final GameMode newGameMode = event.getNewGameMode();
        
        if(playerStateInformation.containsKey(playerId) && newGameMode == GameMode.SPECTATOR) {
            if(event.isCancelled()) {
                event.setCancelled(false);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(final PlayerInteractEvent event) {
    	
    	final Player player = event.getPlayer();
    	final UUID playerId = player.getUniqueId();
    	
        if(!vanishPlugin.isVanishEnabled(playerId)) {
            return;
        }
        
        final Action action = event.getAction();
        final Block block = event.getClickedBlock();
        
        if(action == Action.PHYSICAL) {
        	
            if(block != null) {
                
            	final Material material = block.getType();
                if(interactDisallowedMaterials.contains(material)) {
                	
                    event.setCancelled(true);
                }
            }
        }
        else if(action == Action.RIGHT_CLICK_BLOCK) {
            
            if(player.getGameMode() == GameMode.SPECTATOR) {
                return;
            }
            
            if(player.isSneaking()) {
                
            	final PlayerInventory playerInventory = player.getInventory();
            	final ItemStack mainHandItemStack = playerInventory.getItemInMainHand();
            	final ItemStack offHandItemStack = playerInventory.getItemInOffHand();
            	
                if(mainHandItemStack != null) {
                    
                	final Material mainHandMaterial = mainHandItemStack.getType();
                    if(mainHandMaterial.isBlock() && mainHandMaterial != Material.AIR) {
                        return;
                    }
                }
                else if(offHandItemStack != null) {
                    
                	final Material offHandMaterial = offHandItemStack.getType();
                    if(offHandMaterial.isBlock() && offHandMaterial != Material.AIR) {
                        return;
                    }
                }
            }
            
            if(block == null) {
                return;
            }
            
            final Material blockMaterial = block.getType();
            
            if(blockMaterial == Material.ENDER_CHEST) {
                event.setCancelled(true);
                player.openInventory(player.getEnderChest());
                return;
            }
            
            if(!silentlyViewableMaterials.contains(blockMaterial)) {
                return;
            }
            
            final Location location = player.getLocation();
            
            final PlayerState playerState = PlayerState.getPlayerState(player);
            playerStateInformation.put(playerId, playerState);
            player.setGameMode(GameMode.SPECTATOR);
            
            player.teleport(location.add(0.0D, 0.25D, 0.0D));
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        
        event.setJoinMessage(null);
        
        final Player player = event.getPlayer();
        final UUID playerId = player.getUniqueId();
        
        if(vanishPlugin.isVanishEnabled(playerId)) {
        	
            vanishPlugin.enableVanish(playerId);
            vanishPlugin.disappear(playerId);
            
            final PotionEffect nightVisionPotionEffect = new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1);
            player.addPotionEffect(nightVisionPotionEffect);
        }
        
        if(player.hasPermission(CVVanish.INVISIBLE_VIEW_PERMISSION)) {
            return;
        }
        
        for(final Player onlinePlayer : (Collection<? extends Player>) vanishPlugin.getServer().getOnlinePlayers()) {
            
        	final UUID onlinePlayerId = onlinePlayer.getUniqueId();
            
            if(onlinePlayerId.equals(playerId)) {
                continue;
            }
            
            if(vanishPlugin.isVanishEnabled(onlinePlayerId)) {
                player.hidePlayer(vanishPlugin, onlinePlayer);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(final PlayerMoveEvent event) {
        
    	final Player player = event.getPlayer();
    	final UUID playerId = player.getUniqueId();
        
        if(playerStateInformation.containsKey(playerId)) {
        	
            if(!silentlyViewableInventoryTypes.contains(player.getOpenInventory().getType())) {
                
                playerStateInformation.get(playerId).setPlayerState(player);
                playerStateInformation.remove(playerId);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPickupItem(final PlayerPickupItemEvent event) {
        
    	final UUID playerId = event.getPlayer().getUniqueId();
    	
        if(!vanishPlugin.isVanishEnabled(playerId)) {
            return;
        }
        
        if(vanishPlugin.isPickupEnabled(playerId)) {
            return;
        }
        
        event.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        
        event.setQuitMessage(null);
        
        final Player player = event.getPlayer();
        final UUID playerId = player.getUniqueId();
        
        if(playerStateInformation.containsKey(playerId)) {
            playerStateInformation.get(playerId).setPlayerState(player);
            playerStateInformation.remove(playerId);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        
    	final UUID playerId = event.getPlayer().getUniqueId();
        
        if(playerStateInformation.containsKey(playerId) && event.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE) {
            event.setCancelled(true);
        }
    }
}
