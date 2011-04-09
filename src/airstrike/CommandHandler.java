package airstrike;

import java.util.List;
import java.util.Random;

import net.minecraft.server.EntityTNTPrimed;
import net.minecraft.server.World;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftTNTPrimed;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import airstrike.AirstrikeSpawner.SpawnException;

public class CommandHandler implements CommandExecutor {
	private Airstrike plugin;
	private PluginProperties config;
	private Plugin perm;
	
	private static int area;
	private static int height;
	private int amount, camount;
	
	
	public CommandHandler(Airstrike instance, PluginProperties config, Plugin permissions) {
		plugin = instance;
		plugin.getCommand("as").setExecutor(this);
		plugin.getCommand("asset").setExecutor(this);
		this.config = config;
		this.perm = permissions;
	}
	private boolean checkPermission(CommandSender sender, String node) {
		// Check if the command was sent from console
		if(sender.toString().contains("ColouredConsoleSender")){
			return true;
		}
		Player player = (Player) sender;
		//Check if Permissions is running, else check Ops.txt
		if(perm != null) {
			return plugin.Permissions.has(player, node);
		}
		if(player.isOnline()) {
			return true;
		}		
		return false;
	}
	private void sendMSG(CommandSender sender, String msg) {
		// Console
		if(sender.toString().contains("ColouredConsoleSender")) {
			if(msg.equalsIgnoreCase("help")) {
				System.out.println("Command: as <tnt/cr> <player> <amount> <height>");
				System.out.println("<tnt/cr> TNT or Creeper? If no <amount> is given the default value is used.");
				System.out.println("<height> is for TNT only.");
				System.out.println("Command: asset help - shows admin commands");
				return;
			}
			if(msg.equalsIgnoreCase("notfound")) {
				System.out.println("Player not found!");
				return;
			}
			if(msg.equalsIgnoreCase("morefound")) {
				System.out.println("Found more than one Player!");
				return;
			}
			if(msg.equalsIgnoreCase("assethelp")) {
				System.out.println("Command: asset adminsOnly <true/false>");	
				System.out.println("Command: asset destroyBlocks <true/false>");
				System.out.println("Command: asset creeperDistance <number>");
				System.out.println("Command: asset creeperAmount <number>");
				System.out.println("Command: asset TNTAmount <number>");
				System.out.println("Command: asset height <number>");
				System.out.println("Command: asset area <number>");
	    		return;
			}
			if(msg.equalsIgnoreCase("valuedb")) {
				System.out.println("Value set. Type '/reload' for the change to take effect (will reload all plugins).");
				return;
			}
			if(msg.equalsIgnoreCase("value")) {
				System.out.println("Value set.");
				return;
			}
		}
		
		// Player
		Player player = (Player) sender;
		if(msg.equalsIgnoreCase("help")) {
			player.sendMessage(ChatColor.YELLOW+"Airstrike:");
			player.sendMessage(ChatColor.GOLD+"/as <tnt/cr> <player> <amount> <height>");
			player.sendMessage(ChatColor.GOLD+"<tnt/cr> TNT or Creeper? If no <amount> is given the default value is used.");
			player.sendMessage(ChatColor.GOLD+"<height> is for TNT only.");
			if(checkPermission(sender, "airstrike.asset")) {
				player.sendMessage(ChatColor.LIGHT_PURPLE+"/asset help - shows admin commands");
			}
			return;
		}
		if(msg.equalsIgnoreCase("notfound")) {
			player.sendMessage(ChatColor.GOLD+"Player not found!");
			return;
		}
		if(msg.equalsIgnoreCase("morefound")) {
			player.sendMessage(ChatColor.GOLD+"Found more than one Player!");
			return;
		}
		if(msg.equalsIgnoreCase("assethelp")) {
			player.sendMessage(ChatColor.LIGHT_PURPLE+"/asset adminsOnly <true/false>");	
    		player.sendMessage(ChatColor.LIGHT_PURPLE+"/asset destroyBlocks <true/false>");
    		player.sendMessage(ChatColor.LIGHT_PURPLE+"/asset creeperDistance <number>");
    		player.sendMessage(ChatColor.LIGHT_PURPLE+"/asset creeperAmount <number>");
    		player.sendMessage(ChatColor.LIGHT_PURPLE+"/asset TNTAmount <number>");
    		player.sendMessage(ChatColor.LIGHT_PURPLE+"/asset height <number>");
    		player.sendMessage(ChatColor.LIGHT_PURPLE+"/asset area <number>");
    		return;
		}
		if(msg.equalsIgnoreCase("valuedb")) {
			player.sendMessage(ChatColor.LIGHT_PURPLE+"Value set. Type '/reload' for the change to take effect (will reload all plugins).");
			return;
		}
		if(msg.equalsIgnoreCase("value")) {
			player.sendMessage(ChatColor.LIGHT_PURPLE+"Value set.");
			return;
		}
	}
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		Player player = null;
		try {
			player = (Player) sender;
		}catch (ClassCastException e) {
			// Sender is org.bukkit.craftbukkit.command.ColouredConsoleSender
		}
		String cmd = command.getName().toLowerCase();
		final Server server = plugin.getServer();
		
		
    	
		if (cmd.equals("as")) {		
			if(!checkPermission(sender, "airstrike.as") ) {	
				sendMSG(sender, "help");
        		return true;
        	}
			String args0;
			
			try {
	    		args0 = args[0];
	    	} catch(ArrayIndexOutOfBoundsException ex) {
	    		sendMSG(sender, "help");
	    		return true;
	    	}
			
        	if (args0.equalsIgnoreCase("help")) {
    			sendMSG(sender, "help");
        		return true;
        	}
    		else {
    			final List<Player> victims = server.matchPlayer(args[1]);
        		if (victims.size() < 1) {
        			sendMSG(sender, "notfound");
            		return true;
        		}
        		if (victims.size() > 1) {
        			sendMSG(sender, "morefound");
            		return true;
        		}
    			final AirstrikeSpawner spawner = new AirstrikeSpawner();
    			final Player victim  = victims.get(0);
    			final CraftWorld cWorld = (CraftWorld) victim.getWorld();
    			final Location loc = victim.getLocation();
    			final Random rg = new Random();
    			
    			// Creeper
    			
    			if (args0.equalsIgnoreCase("cr") && (checkPermission(sender, "airstrike.as.cr") || checkPermission(sender, "airstrike.as"))) {
    				final int distance = config.getInteger("creeperDistance", PluginProperties.creeperDistance);
    				if(args.length>2) camount = Integer.valueOf(args[2]); 
    				else camount = config.getInteger("creeperAmount", PluginProperties.creeperAmount);
    				new Thread()  {
    					@Override public void run() {
    						setPriority( Thread.MIN_PRIORITY );
    						CraftEntity spawn;
    						World world = ((org.bukkit.craftbukkit.CraftWorld)victim.getWorld()).getHandle();
    						for (int i=0; i<camount; i++) {  		
    							int roll = (rg.nextBoolean() ? distance : -distance);
    		    				loc.setX( loc.getX() + (roll) );
    		    				roll = (rg.nextBoolean() ? distance : -distance);
    		        			loc.setZ( loc.getZ() + (roll) );
    							
								try {
									spawn = spawner.create(victim, plugin, "Creeper", "Creeper");
									spawn.teleportTo(loc);
									world.a(spawn.getHandle());
								} catch (SpawnException e1) {
									e1.printStackTrace();
								}
    		        			
    							try {
    		        				sleep(500);
    		        			}catch (InterruptedException e) {
    							}
    		    			}
    					}
    				}.start();
    				return true;         				
    			}
    			
    			// TNT
    			if (args0.equalsIgnoreCase("tnt") && (checkPermission(sender, "airstrike.as.tnt") || checkPermission(sender, "airstrike.as"))) {
    				if(args.length>2) amount = Integer.valueOf(args[2]); 
    				else amount = config.getInteger("TNTAmount", PluginProperties.TNTAmount);
    				
    				if(args.length>3) height = Integer.valueOf(args[3]);
    				else height = config.getInteger("height", PluginProperties.height);
    				area = config.getInteger("area", PluginProperties.area);
    				new Thread()  {
    					@Override 
    					public void run() {
    						setPriority( Thread.MIN_PRIORITY );
    						for (int i=0; i<amount; i++) {  
    							EntityTNTPrimed tnt = new EntityTNTPrimed(cWorld.getHandle(),0,0,0);
    		        			CraftTNTPrimed tntp = new CraftTNTPrimed((CraftServer) server, tnt);
    		        			cWorld.getHandle().a(tnt);
    		        			Location loc = victims.get(0).getLocation();
    		        			loc.setY(loc.getY()+height);
    		        			loc.setX( loc.getX() + (rg.nextInt((2*area)+1)-area) );
    		        			loc.setZ( loc.getZ() + (rg.nextInt((2*area)+1)-area) );
    		        			tntp.teleportTo(loc);
    		        			try {
    		        				sleep(500);
    		        			}catch (InterruptedException e) {
    							}
    		    			}
    					}
    				}.start();
    				return true;
    			}
    		}
    		return true;
    	}
		
		if(cmd.equals("asset")) {
        	if(!checkPermission(sender, "airstrike.asset")) {
        		player.sendMessage(ChatColor.RED +"Only Admins can use this command!");
        		return true;
        	}
        	if(args[0].equalsIgnoreCase("help")) {
        		sendMSG(sender, "assethelp");
        		return true;
        	}
        	
        	String value;
        	try {
        		value = args[1];
        	} catch(ArrayIndexOutOfBoundsException ex) {
        		player.sendMessage(ChatColor.LIGHT_PURPLE+"Wrong Input");
        		return true;
        	}
        	
        	if(args[0].equalsIgnoreCase("destroyBlocks")) {
        		config.setProperty("destroyBlocks", value);
        		sendMSG(sender, "valuedb");
        		return true;
        	}
        	if(args[0].equalsIgnoreCase("creeperDistance")) {
        		config.setProperty("creeperDistance", value);
        		sendMSG(sender, "value");
        		return true;
        	}
        	if(args[0].equalsIgnoreCase("creeperAmount")) {
        		config.setProperty("creeperAmount", value);
        		sendMSG(sender, "value");
        		return true;
        	}
			if(args[0].equalsIgnoreCase("TNTAmount")) {
				config.setProperty("TNTAmount", value);
				sendMSG(sender, "value");
				return true;
			}
			if(args[0].equalsIgnoreCase("height")) {
				config.setProperty("height", value);
				sendMSG(sender, "value");
				return true;
			}
			if(args[0].equalsIgnoreCase("area")) {
				config.setProperty("area", value);
				sendMSG(sender, "value");
				return true;
			}
			if(args[0].equalsIgnoreCase("adminsOnly")) {
				config.setProperty("adminsOnly", value);
				sendMSG(sender, "value");
				return true;
			}
        }
		return false;      	
    }	
}
