package airstrike;

import java.lang.reflect.Constructor;

import net.minecraft.server.Entity;
import net.minecraft.server.WorldServer;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;

public class AirstrikeSpawner {
	
	@SuppressWarnings("unchecked")
	public  CraftEntity create(Player player, Airstrike plugin, String craftClass, String entityClass) throws SpawnException {
		
		try {
			WorldServer world = ((org.bukkit.craftbukkit.CraftWorld) player.getWorld()).getHandle();
			Constructor<CraftEntity> craft = (Constructor<CraftEntity>) ClassLoader.getSystemClassLoader().loadClass("org.bukkit.craftbukkit.entity.Craft"+craftClass).getConstructors()[0];
			Constructor<Entity> entity = (Constructor<Entity>) ClassLoader.getSystemClassLoader().loadClass("net.minecraft.server.Entity"+entityClass).getConstructors()[0];
			return craft.newInstance((CraftServer) plugin.getServer(), entity.newInstance( world ) );
		}catch (Exception e) {
			System.out.println("Could not create Entity");
			e.printStackTrace();
			throw new SpawnException();
		}
		
	}
	public class SpawnException extends Exception {
		private static final long serialVersionUID = 1L;		
	}
}

