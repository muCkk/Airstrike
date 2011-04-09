package airstrike;

import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;

public class AirstrikeEntityListener extends EntityListener {
	private final Airstrike plugin;
	PluginProperties config;
	
	public AirstrikeEntityListener(Airstrike instance) {
		plugin = instance;
	}
	public void onEntityExplode(EntityExplodeEvent event) {
		if (event.isCancelled()) {
            return;
        }
		event.setCancelled(true);
		return; 
	}
	public void setConfig(PluginProperties config) {
		this.config = config;
	}
}
