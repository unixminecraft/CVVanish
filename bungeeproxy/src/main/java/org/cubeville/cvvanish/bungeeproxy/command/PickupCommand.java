package org.cubeville.cvvanish.bungeeproxy.command;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.cubeville.common.bungeecord.command.PlayerCommand;
import org.cubeville.cvvanish.bungeeproxy.CVVanish;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PickupCommand extends PlayerCommand {
    
    private static final String USE_COMMAND = "cvvanish.pickup.use";
    
    private CVVanish vanishPlugin;
    
    public PickupCommand(CVVanish vanishPlugin) {
        
        super("vpickup");
        this.vanishPlugin = vanishPlugin;
        setMinimumPermissionToUse(USE_COMMAND);
        setNoPermissionMessage("Unknown command. Type \"/help\" for help.", ChatColor.WHITE);
        setSyntax("Syntax: /vpickup [on|off]");
        addFlag("on");
        addFlag("off");
    }
    
    @Override
    public void execute(ProxiedPlayer commandSenderPlayer, Set<String> flags, List<String> arguments) {
        
        UUID commandSenderPlayerId = commandSenderPlayer.getUniqueId();
        boolean pickupEnabled = vanishPlugin.isPlayerPickupEnabled(commandSenderPlayerId);
        
        if(flags.size() == 0) {
            
            TextComponent pickingUpItems = new TextComponent();
            TextComponent pickupStatusValue = new TextComponent();
            TextComponent period = new TextComponent();
            TextComponent toTurnIt = new TextComponent();
            TextComponent toggleValue = new TextComponent();
            TextComponent use = new TextComponent();
            TextComponent toggleCommandValue = new TextComponent();
            
            pickingUpItems.setText("Picking up items is currently ");
            period.setText(".");
            toTurnIt.setText("To turn it ");
            use.setText(", use ");
            
            pickingUpItems.setColor(ChatColor.YELLOW);
            period.setColor(ChatColor.YELLOW);
            toTurnIt.setColor(ChatColor.YELLOW);
            use.setColor(ChatColor.YELLOW);
            toggleCommandValue.setColor(ChatColor.AQUA);
            
            if(pickupEnabled) {
                
                pickupStatusValue.setText("ENABLED");
                toggleValue.setText("off");
                toggleCommandValue.setText("/vpickup off");
                
                pickupStatusValue.setColor(ChatColor.GREEN);
                toggleValue.setColor(ChatColor.RED);
            }
            else {
                
                pickupStatusValue.setText("DISABLED");
                toggleValue.setText("on");
                toggleCommandValue.setText("/vpickup on");
                
                pickupStatusValue.setColor(ChatColor.RED);
                toggleValue.setColor(ChatColor.GREEN);
            }
            
            commandSenderPlayer.sendMessage(pickingUpItems, pickupStatusValue, period);
            commandSenderPlayer.sendMessage(toTurnIt, toggleValue, use, toggleCommandValue, period);
        }
        else if(flags.size() == 1) {
            
            if(flags.contains("on")) {
                if(pickupEnabled) {
                    
                    TextComponent pickupAlreadyEnabled = new TextComponent();
                    pickupAlreadyEnabled.setText("You already have item pickup enabled, you can't enable it more.");
                    pickupAlreadyEnabled.setColor(ChatColor.RED);
                    commandSenderPlayer.sendMessage(pickupAlreadyEnabled);
                    return;
                }
                
                if(!vanishPlugin.enablePlayerPickup(commandSenderPlayerId)) {
                    
                    //TODO: Log error.
                    TextComponent internalError = new TextComponent();
                    internalError.setText("Internal error, please try again. If this error persists, please contact a server administrator.");
                    internalError.setColor(ChatColor.RED);
                    commandSenderPlayer.sendMessage(internalError);
                    return;
                }
                
                TextComponent itemPickupEnabled = new TextComponent();
                itemPickupEnabled.setText("Item pickup enabled.");
                itemPickupEnabled.setColor(ChatColor.GREEN);
                commandSenderPlayer.sendMessage(itemPickupEnabled);
            }
            else if(flags.contains("off")) {
                if(!pickupEnabled) {
                    
                    TextComponent pickupAlreadyDisabled = new TextComponent();
                    pickupAlreadyDisabled.setText("You don't have item pickup enabled, disabling more would probably cause you to drop your items.");
                    pickupAlreadyDisabled.setColor(ChatColor.RED);
                    commandSenderPlayer.sendMessage(pickupAlreadyDisabled);
                    return;
                }
                
                if(!vanishPlugin.disablePlayerPickup(commandSenderPlayerId)) {
                    
                    //TODO: Log error.
                    TextComponent internalError = new TextComponent();
                    internalError.setText("Internal error, please try again. If this error persists, please contact a server administrator.");
                    internalError.setColor(ChatColor.RED);
                    commandSenderPlayer.sendMessage(internalError);
                    return;
                }
                
                TextComponent itemPickupDisabled = new TextComponent();
                itemPickupDisabled.setText("Item pickup disabled.");
                itemPickupDisabled.setColor(ChatColor.GREEN);
                commandSenderPlayer.sendMessage(itemPickupDisabled);
            }
            else {
                
                //TODO: Log error.
                TextComponent unlikelyError = new TextComponent();
                unlikelyError.setText("An unlikely error has occurred. Please report this to a server administrator.");
                unlikelyError.setColor(ChatColor.RED);
                commandSenderPlayer.sendMessage(unlikelyError);
            }
        }
        else {
            
            TextComponent pleaseOnlyUseOne = new TextComponent();
            pleaseOnlyUseOne.setText("Please only use 1 of the following with this command: [on|off]");
            pleaseOnlyUseOne.setColor(ChatColor.RED);
            commandSenderPlayer.sendMessage(pleaseOnlyUseOne);
        }
    }
}
