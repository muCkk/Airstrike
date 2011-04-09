package airstrike;

import java.util.List;
import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftTNTPrimed;

import airstrike.AirstrikeSpawner.SpawnException;

import net.minecraft.server.EntityTNTPrimed;
import net.minecraft.server.World;



public class AirstrikePlayerListener extends PlayerListener {
    private final Airstrike plugin;
    private static int area;
	private static int height;
	private int amount, camount;
    private PluginProperties config;
    
    
    public AirstrikePlayerListener(Airstrike instance) {
        plugin = instance;
    }
    @Override
    public void onPlayerCommandPreprocess(PlayerChatEvent event) {
    	String[] split = event.getMessage().split(" ");
        final Player player = event.getPlayer();
        final Server server = plugin.getServer();
        
        
        if(split[0].equalsIgnoreCase("/as")) {
        	event.setCancelled(true);
        	if(config.getBoolean("adminsOnly", PluginProperties.adminsOnly) && !player.isOp()) {	
        		player.sendMessage(ChatColor.RED +"Only Admins can use this command!");
        		return;
        	}
        	if (split.length == 1) {
        		player.sendMessage(ChatColor.YELLOW +"Airstrike:");
    			player.sendMessage(ChatColor.GOLD+"/as <tnt/cr> <player> <amount> <height>");
    			player.sendMessage(ChatColor.GOLD+"<tnt/cr> TNT or Creeper? If no <amount> is given the default value is used.");
    			player.sendMessage(ChatColor.GOLD+"<height> is for TNT only.");
    			if(player.isOp()) {
    				player.sendMessage(ChatColor.LIGHT_PURPLE+"/asset help - shows admin commands");
    			}
        		return;
        	}
        	if (split.length > 1) {
        		if (split[1].equals("help")) {
        			player.sendMessage(ChatColor.YELLOW+"Airstrike:");
        			player.sendMessage(ChatColor.GOLD+"/as <tnt/cr> <player> <amount> <height>");
        			player.sendMessage(ChatColor.GOLD+"<tnt/cr> TNT or Creeper? If no <amount> is given the default value is used.");
        			player.sendMessage(ChatColor.GOLD+"<height> is for TNT only.");
        			if(player.isOp()) {
        				player.sendMessage(ChatColor.LIGHT_PURPLE+"/asset help - shows admin commands");
        			}
            		return;
        		}
        		else {
        			final List<Player> victims = server.matchPlayer(split[2]);
            		if (victims.size() < 1) {
            			player.sendMessage(ChatColor.GOLD+"Player not found!");
                		return;
            		}
            		if (victims.size() > 1) {
            			player.sendMessage(ChatColor.GOLD+"Found more than one Player!");
                		return;
            		}
            		else {
            			final AirstrikeSpawner spawner = new AirstrikeSpawner();
            			final Player victim  = victims.get(0);
            			final CraftWorld cWorld = (CraftWorld) victim.getWorld();
            			final Location loc = victim.getLocation();
            			final Random rg = new Random();
            			
            			// Creeper
            			boolean explosions = config.getBoolean("destroyBlocks", false);
            			
            			if (!split[1].equalsIgnoreCase("tnt") && !explosions){
            				player.sendMessage(ChatColor.GOLD+"Creepers are not available (Block-protection is active).");
            				return;
            			}
            			if (split[1].equalsIgnoreCase("cr") && explosions) {
            				final int distance = config.getInteger("creeperDistance", PluginProperties.creeperDistance);
            				if(split.length>3) camount = Integer.valueOf(split[3]); 
            				else camount = config.getInteger("creeperAmount", PluginProperties.creeperAmount);
            				new Thread()  {
            					@Override public void run() {
            						setPriority( Thread.MIN_PRIORITY );
            						CraftEntity spawn;
            						World world = ((org.bukkit.craftbukkit.CraftWorld)player.getWorld()).getHandle();
            						for (int i=0; i<camount; i++) {  		
            							int roll = (rg.nextBoolean() ? distance : -distance);
            		    				loc.setX( loc.getX() + (roll) );
            		    				roll = (rg.nextBoolean() ? distance : -distance);
            		        			loc.setZ( loc.getZ() + (roll) );
            							
										try {
											spawn = spawner.create(player, plugin, "Creeper", "Creeper");
											spawn.teleportTo(loc);
											world.a(spawn.getHandle());
											System.out.println("creeper teleported");
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
            				return;         				
            			}
            			// TNT
            			if (split[1].equalsIgnoreCase("tnt")) {
            				if(split.length>3) amount = Integer.valueOf(split[3]); 
            				else amount = config.getInteger("TNTAmount", PluginProperties.TNTAmount);
            				
            				if(split.length>4) height = Integer.valueOf(split[4]);
            				else height = config.getInteger("height", PluginProperties.height);
            				area = config.getInteger("area", PluginProperties.area);
            				new Thread()  {
            					@Override public void run() {
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
            			}
            		}
        		}
        	}      	
        }
        if(split[0].equalsIgnoreCase("/asset")) {
        	event.setCancelled(true);
        	if(!player.isOp()) {
        		player.sendMessage(ChatColor.RED +"Only Admins can use this command!");
        		return;
        	}
        	if(split[1].equalsIgnoreCase("help")) {
        		player.sendMessage(ChatColor.LIGHT_PURPLE+"/asset adminsOnly <true/false>");	
        		player.sendMessage(ChatColor.LIGHT_PURPLE+"/asset destroyBlocks <true/false>");
        		player.sendMessage(ChatColor.LIGHT_PURPLE+"/asset creeperDistance <number>");
        		player.sendMessage(ChatColor.LIGHT_PURPLE+"/asset creeperAmount <number>");
        		player.sendMessage(ChatColor.LIGHT_PURPLE+"/asset TNTAmount <number>");
        		player.sendMessage(ChatColor.LIGHT_PURPLE+"/asset height <number>");
        		player.sendMessage(ChatColor.LIGHT_PURPLE+"/asset area <number>");
        		return;
        	}
        	
        	event.setCancelled(true);
        	String value;
        	try {
        		value = split[2];
        	} catch(ArrayIndexOutOfBoundsException ex) {
        		player.sendMessage(ChatColor.LIGHT_PURPLE+"Wrong Input");
        		return;
        	}
        	
        	if(split[1].equalsIgnoreCase("destroyBlocks")) {
        		config.setProperty("destroyBlocks", value);
        		player.sendMessage(ChatColor.LIGHT_PURPLE+split[1] +" set to "+value +". Type '/reload' for the change to take effect (will reload all plugins).");
        		return;
        	}
        	if(split[1].equalsIgnoreCase("creeperDistance")) {
        		config.setProperty("creeperDistance", value);
        		player.sendMessage(ChatColor.LIGHT_PURPLE+split[1] +" set to "+value);
        		return;
        	}
        	if(split[1].equalsIgnoreCase("creeperAmount")) {
        		config.setProperty("creeperAmount", value);
        		player.sendMessage(ChatColor.LIGHT_PURPLE+split[1] +" set to "+value);
        		return;
        	}
			if(split[1].equalsIgnoreCase("TNTAmount")) {
				config.setProperty("TNTAmount", value);
				player.sendMessage(ChatColor.LIGHT_PURPLE+split[1] +" set to "+value);
				return;
			}
			if(split[1].equalsIgnoreCase("height")) {
				config.setProperty("height", value);
				player.sendMessage(ChatColor.LIGHT_PURPLE+split[1] +" set to "+value);
				return;
			}
			if(split[1].equalsIgnoreCase("area")) {
				config.setProperty("area", value);
				player.sendMessage(ChatColor.LIGHT_PURPLE+split[1] +" set to "+value);
				return;
			}
			if(split[1].equalsIgnoreCase("adminsOnly")) {
				config.setProperty("adminsOnly", value);
				player.sendMessage(ChatColor.LIGHT_PURPLE+split[1] +" set to "+value);
				return;
			}
        }
    }
   public void setConfig(PluginProperties config) {
    	this.config = config;
    }
}
