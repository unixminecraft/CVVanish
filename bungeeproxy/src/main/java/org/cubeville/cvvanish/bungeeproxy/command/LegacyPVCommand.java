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

package org.cubeville.cvvanish.bungeeproxy.command;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cubeville.common.bungeecord.command.PlayerCommand;
import org.cubeville.cvvanish.bungeeproxy.CVVanish;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public final class LegacyPVCommand extends PlayerCommand {
    
	private static final String SYNTAX = "&cSyntax: /pv&r";
	
    private static final String USE_PERMISSION = "cvvanish.legacypv.use";
    
    private final Logger logger;
    
    public LegacyPVCommand(final CVVanish vanishPlugin) {
        
    	super("pv", USE_PERMISSION, convertSyntax(SYNTAX));
    	
    	addOptionalBaseParameters(2);
    	
    	this.logger = vanishPlugin.getLogger();
    }
    
    @Override
    public void execute(final ProxiedPlayer player, final Set<String> flags, final Map<String, String> parameters, final List<String> baseParameters) {
        
		final String logHeader = getClass().getSimpleName() + " (" + player.getName() + ") :";
		logger.log(Level.INFO, logHeader + "Execution starting.");
    	
        final TextComponent thisIsTheLegacyVanishCommand = new TextComponent();
        final TextComponent pleaseCheckTheStaffForums = new TextComponent();
        
        thisIsTheLegacyVanishCommand.setText("This is the legacy vanish command.");
        pleaseCheckTheStaffForums.setText("Please check the staff forums for an updated list of commands.");
        
        thisIsTheLegacyVanishCommand.setColor(ChatColor.RED);
        pleaseCheckTheStaffForums.setColor(ChatColor.AQUA);
        
        player.sendMessage(thisIsTheLegacyVanishCommand);
        player.sendMessage(pleaseCheckTheStaffForums);
    }
}
