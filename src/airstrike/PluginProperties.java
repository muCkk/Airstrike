package airstrike;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class PluginProperties extends Properties {
	static final long serialVersionUID = 0L;
	static int	creeperDistance = 5;
	static int	creeperAmount = 1;
	static int	TNTAmount = 1;
	static int	height = 8;
	static int	area = 6;
	static boolean adminsOnly = false;
	
	private String fileName, dir;
	FileWriter writer;
	FileReader reader;
	private String info = "destroyBlocks: true: Blocks get destroyed - false: Block won't get touched\n" 
								+"height: Sets height in which the TNT will spawn\n"
								+"area: Defines the cuboid area size in which the TNT will spawn\n"
								+"TNTAmount: Sets the amount for /as <player>"
								+"creeperDistance: Distance in which creepers will spawn"
								+"creeperAmount: Amount of creepers";
	
	public PluginProperties(String file, String dir) {
		this.fileName = file;
		this.dir = dir;
	}
	
	public void load() {
		File file = new File(this.fileName);
		if (file.exists()) {
			try {
				load(new FileInputStream(this.fileName));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		else {
			File directory = new File(dir);
			if(!directory.exists()) directory.mkdirs();
			 getBoolean("destroyBlocks", false);
			 getBoolean("adminsOnly", false);
			 getInteger("creeperDistance", creeperDistance);
			 getInteger("creeperAmount", creeperAmount);
			 getInteger("TNTAmount", TNTAmount);
			 getInteger("height", height);
			 getInteger("area", area);
		}
	}	

	public void save() {
		try {
			store(new FileOutputStream(this.fileName), info);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public int getInteger(String key, int value) {
		if (containsKey(key)) {
			return Integer.parseInt(getProperty(key));
		}
		
		put(key, String.valueOf(value));
		return value;
	}

	public String getString(String key, String value) {
		if (containsKey(key)) {
			return getProperty(key);
		}

		put(key, value);
		return value;
	}

	public boolean getBoolean(String key, boolean value) {
		if (containsKey(key)) {
			String boolString = getProperty(key);
			return (boolString.length() > 0)
					&& (boolString.toLowerCase().charAt(0) == 't');
		}
		put(key, value ? "true" : "false");
		return value;
	}

}