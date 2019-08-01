package org.cubeville.cvvanish.bungeebukkit.listener;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.GameMode;
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
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
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
public class EventListener implements Listener {
    
    private static final class PlayerState {
        
        private final GameMode gameMode;
        private final boolean allowedFlight;
        private final boolean flying;
        
        private PlayerState(GameMode gameMode, boolean allowedFlight, boolean flying) {
            
            this.gameMode = gameMode;
            this.allowedFlight = allowedFlight;
            this.flying = flying;
        }
        
        private static PlayerState getPlayerState(Player player) {
            
            return new PlayerState(player.getGameMode(), player.getAllowFlight(), player.isFlying());
        }
        
        private void setPlayerState(Player player) {
            
            player.setGameMode(gameMode);
            player.setAllowFlight(allowedFlight);
            player.setFlying(flying);
        }
    }
    
    private CVVanish vanishPlugin;
    private HashMap<UUID, PlayerState> playerStateInformation;
    private HashSet<InventoryType> silentlyViewableInventoryTypes;
    private HashSet<Material> interactDisallowedMaterials;
    private HashSet<Material> silentlyViewableMaterials;
    
    public EventListener(CVVanish vanishPlugin) {
        
        this.vanishPlugin = vanishPlugin;
        
        playerStateInformation = new HashMap<UUID, PlayerState>();
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
    public void onEntityDamageEvent(EntityDamageEvent entityDamageEvent) {
        
        Entity damagedEntity = entityDamageEvent.getEntity();
        if(!(damagedEntity instanceof Player)) {
            return;
        }
        
        UUID damagedPlayerId = ((Player) damagedEntity).getUniqueId();
        if(!vanishPlugin.isPlayerVanished(damagedPlayerId)) {
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
        if(!vanishPlugin.isPlayerVanished(pickingUpPlayerId)) {
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
        if(!vanishPlugin.isPlayerVanished(targetedPlayerId)) {
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
        if(!vanishPlugin.isPlayerVanished(foodChangedPlayerId)) {
            return;
        }
        
        foodLevelChangeEvent.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClickEvent(InventoryClickEvent inventoryClickEvent) {
        
        HumanEntity clickingHumanEntity = inventoryClickEvent.getWhoClicked();
        if(!(clickingHumanEntity instanceof Player)) {
            return;
        }
        
        Player clickingPlayer = (Player) clickingHumanEntity;
        UUID clickingPlayerId = clickingPlayer.getUniqueId();
        
        if(!vanishPlugin.isPlayerVanished(clickingPlayerId)) {
            return;
        }
        
        if(!playerStateInformation.containsKey(clickingPlayerId)) {
            return;
        }
        
        if(clickingPlayer.getGameMode() == GameMode.SPECTATOR) {
            inventoryClickEvent.setCancelled(false);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryCloseEvent(InventoryCloseEvent inventoryCloseEvent) {
        
        HumanEntity humanEntityClosingInventory = inventoryCloseEvent.getPlayer();
        if(!(humanEntityClosingInventory instanceof Player)) {
            return;
        }
        
        final Player playerClosingInventory = (Player) humanEntityClosingInventory;
        final UUID playerClosingInventoryId = playerClosingInventory.getUniqueId();
        
        if(!playerStateInformation.containsKey(playerClosingInventoryId)) {
            return;
        }
        
        if(!silentlyViewableInventoryTypes.contains(playerClosingInventory.getInventory().getType())) {
            return;
        }
        
        BukkitRunnable bukkitRunnable = new BukkitRunnable() {
            
            @Override
            public void run() {
                
                PlayerState playerState = playerStateInformation.get(playerClosingInventoryId);
                if(playerState == null) {
                    return;
                }
                
                playerState.setPlayerState(playerClosingInventory);
                playerStateInformation.remove(playerClosingInventoryId);
            }
        };
        
        bukkitRunnable.runTaskLater(vanishPlugin, 1);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent playerChangedWorldEvent) {
        
        Player changingWorldPlayer = playerChangedWorldEvent.getPlayer();
        if(vanishPlugin.isPlayerVanished(changingWorldPlayer.getUniqueId())) {
            
            PotionEffect nightVisionPotionEffect = new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1);
            changingWorldPlayer.addPotionEffect(nightVisionPotionEffect);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerGameModeChangeEvent(PlayerGameModeChangeEvent playerGameModeChangeEvent) {
        
        UUID playerChangingGameModeId = playerGameModeChangeEvent.getPlayer().getUniqueId();
        GameMode newGameMode = playerGameModeChangeEvent.getNewGameMode();
        
        if(playerStateInformation.containsKey(playerChangingGameModeId) && newGameMode == GameMode.SPECTATOR) {
            if(playerGameModeChangeEvent.isCancelled()) {
                playerGameModeChangeEvent.setCancelled(false);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEvent(PlayerInteractEvent playerInteractEvent) {
        
        Player interactingPlayer = playerInteractEvent.getPlayer();
        UUID interactingPlayerId = interactingPlayer.getUniqueId();
        if(!vanishPlugin.isPlayerVanished(interactingPlayerId)) {
            return;
        }
        
        Action playerInteractEventAction = playerInteractEvent.getAction();
        Block clickedBlock = playerInteractEvent.getClickedBlock();
        
        if(playerInteractEventAction == Action.PHYSICAL) {
            if(clickedBlock != null) {
                
                Material clickedBlockMaterial = clickedBlock.getType();
                if(interactDisallowedMaterials.contains(clickedBlockMaterial)) {
                    playerInteractEvent.setCancelled(true);
                }
            }
        }
        else if(playerInteractEventAction == Action.RIGHT_CLICK_BLOCK) {
            
            if(interactingPlayer.getGameMode() == GameMode.SPECTATOR) {
                return;
            }
            
            if(interactingPlayer.isSneaking()) {
                
                PlayerInventory interactingPlayerInventory = interactingPlayer.getInventory();
                ItemStack itemInMainHand = interactingPlayerInventory.getItemInMainHand();
                ItemStack itemInOffHand = interactingPlayerInventory.getItemInOffHand();
                if(itemInMainHand != null) {
                    
                    Material itemInMainHandMaterial = itemInMainHand.getType();
                    if(itemInMainHandMaterial.isBlock() && itemInMainHandMaterial != Material.AIR) {
                        return;
                    }
                }
                else if(itemInOffHand != null) {
                    
                    Material itemInOffHandMaterial = itemInOffHand.getType();
                    if(itemInOffHandMaterial.isBlock() && itemInOffHandMaterial != Material.AIR) {
                        return;
                    }
                }
            }
            
            if(clickedBlock == null) {
                return;
            }
            
            Material clickedBlockMaterial = clickedBlock.getType();
            if(clickedBlockMaterial == Material.ENDER_CHEST) {
                playerInteractEvent.setCancelled(true);
                interactingPlayer.openInventory(interactingPlayer.getEnderChest());
                return;
            }
            
            if(!silentlyViewableMaterials.contains(clickedBlockMaterial)) {
                return;
            }
            
            PlayerState interactingPlayerState = PlayerState.getPlayerState(interactingPlayer);
            playerStateInformation.put(interactingPlayerId, interactingPlayerState);
            interactingPlayer.setGameMode(GameMode.SPECTATOR);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoinEvent(PlayerJoinEvent playerJoinEvent) {
        
        playerJoinEvent.setJoinMessage(null);
        
        Player joiningPlayer = playerJoinEvent.getPlayer();
        UUID joiningPlayerId = joiningPlayer.getUniqueId();
        if(vanishPlugin.isPlayerVanished(joiningPlayerId)) {
            vanishPlugin.vanishPlayer(joiningPlayerId);
        }
        
        PotionEffect nightVisionPotionEffect = new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1);
        joiningPlayer.addPotionEffect(nightVisionPotionEffect);
        
        if(joiningPlayer.hasPermission("cvvanish.vanish.view")) {
            return;
        }
        
        Collection<? extends Player> allOnlinePlayers = (Collection<? extends Player>) vanishPlugin.getServer().getOnlinePlayers();
        for(Player onlinePlayer : allOnlinePlayers) {
            
            UUID onlinePlayerId = onlinePlayer.getUniqueId();
            
            if(onlinePlayerId.equals(joiningPlayerId)) {
                continue;
            }
            
            if(vanishPlugin.isPlayerVanished(onlinePlayerId)) {
                joiningPlayer.hidePlayer(vanishPlugin, onlinePlayer);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLoginEvent(PlayerLoginEvent playerLoginEvent) {
        
        //TODO: Does this have to go under onPlayerJoinEvent() ?
        
        Player loginPlayer = playerLoginEvent.getPlayer();
        UUID loginPlayerId = loginPlayer.getUniqueId();
        
        if(vanishPlugin.isPlayerVanished(loginPlayerId)) {
            vanishPlugin.vanishPlayer(loginPlayerId);
        }
        
        if(loginPlayer.hasPermission("cvvanish.vanish.view")) {
            return;
        }
        
        Collection<? extends Player> allOnlinePlayers = (Collection<? extends Player>) vanishPlugin.getServer().getOnlinePlayers();
        for(Player onlinePlayer : allOnlinePlayers) {
            
            UUID onlinePlayerId = onlinePlayer.getUniqueId();
            
            if(onlinePlayerId.equals(loginPlayerId)) {
                continue;
            }
            
            if(vanishPlugin.isPlayerVanished(onlinePlayerId)) {
                loginPlayer.hidePlayer(vanishPlugin, onlinePlayer);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMoveEvent(PlayerMoveEvent playerMoveEvent) {
        
        Player movingPlayer = playerMoveEvent.getPlayer();
        UUID movingPlayerId = movingPlayer.getUniqueId();
        
        if(playerStateInformation.containsKey(movingPlayerId)) {
            if(!silentlyViewableInventoryTypes.contains(movingPlayer.getOpenInventory().getType())) {
                
                playerStateInformation.get(movingPlayerId).setPlayerState(movingPlayer);
                playerStateInformation.remove(movingPlayerId);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent playerPickupItemEvent) {
        
        UUID pickingUpPlayerId = playerPickupItemEvent.getPlayer().getUniqueId();
        if(!vanishPlugin.isPlayerVanished(pickingUpPlayerId)) {
            return;
        }
        
        if(vanishPlugin.isPlayerPickupEnabled(pickingUpPlayerId)) {
            return;
        }
        
        playerPickupItemEvent.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuitEvent(PlayerQuitEvent playerQuitEvent) {
        
        playerQuitEvent.setQuitMessage(null);
        
        Player quittingPlayer = playerQuitEvent.getPlayer();
        UUID quittingPlayerId = quittingPlayer.getUniqueId();
        
        if(playerStateInformation.containsKey(quittingPlayerId)) {
            playerStateInformation.get(quittingPlayerId).setPlayerState(quittingPlayer);
            playerStateInformation.remove(quittingPlayerId);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerTeleportEvent(PlayerTeleportEvent playerTeleportEvent) {
        
        UUID teleportingPlayerId = playerTeleportEvent.getPlayer().getUniqueId();
        
        if(playerStateInformation.containsKey(teleportingPlayerId) && playerTeleportEvent.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE) {
            playerTeleportEvent.setCancelled(true);
        }
    }
    
    //TODO: If a way to avoid arrow contact is found, the EventPriority may change.
    @EventHandler(priority = EventPriority.MONITOR)
    public void onProjectileHitEvent(ProjectileHitEvent projectileHitEvent) {
        
        Entity hitEntity = projectileHitEvent.getHitEntity();
        if(hitEntity == null) {
            return;
        }
        
        if(!(hitEntity instanceof Player)) {
            return;
        }
        
        UUID hitPlayerId = ((Player) hitEntity).getUniqueId();
        if(!vanishPlugin.isPlayerVanished(hitPlayerId)) {
            return;
        }
        
        //TODO: No .setCancelled() method exists.
        //projectileHitEvent.setCancelled(true);
    }
}
