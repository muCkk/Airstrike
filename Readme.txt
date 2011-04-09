Airstrike v1.1

Installation:
- Put the Airstrike folder and the jar into Bukkit's plugin folder
- If you use Permissions configurate these nodes:
    airstrike.as         Grants acess to /as tnt and /as cr
    airstrike.as.tnt     Access to tnt only
    airstrike.as.cr      Access to creepers only
    airstrike.asset      (Admins) Grants access to /asset command
- Customize Airstrike.properties or use the default values


Commands:
    /as help  - lists all commands
    /as <tnt/cr> <player> <amount> <height>
    	
    	<tnt/cr> - TNT or Creeper?
		<player> - Victim
		The plugin will use the default values if not given:
		<amount> - Set the amount of TNT blocks/creepers
        <height> - TNT only - Sets the vertical distance to the victim
    
    For admins only (have to be listed inside ops.txt):
    /asset <key> <value>
    Example: /asset destroyBlocks true -> enables destruction of blocks
    keys:	destroyBlocks
			creeperDistance
			creeperAmount
			TNTAmount
			height
			area
			adminsOnly
			
Version 1.1
- Commands can now be used in console
- Bugfixes


Version 1.0
- added Permissions support
- changed the way the plugin handles commands

Version 0.3
	- Added 'destroyBlocks' (default false) to the config. true: blocks get destroyed. false: your world is safe
	- Added 'adminsOnly'. If set true only admins can use the plugin
	- Added creeperssssSSS. 
		'creeperDistance' sets the distance to the victim when a creeper is spawned
		'creeperAmount' sets how many creepers will spawn by default
		creepers will also spawn randomly around the player
	- Changed command structure	

Version 0.2
    - TNT now rains on the player and ..
    - .. it is now spawning randomly arround the player (use the config to control the size of the area)
    - Added new commands
    - Added config file

Version 0.1
    - First release