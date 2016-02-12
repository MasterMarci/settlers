package de.hs.settlers;

import java.io.File;

import org.spout.api.util.config.ConfigurationHolder;
import org.spout.api.util.config.ConfigurationHolderConfiguration;
import org.spout.api.util.config.yaml.YamlConfiguration;

public class Configuration extends ConfigurationHolderConfiguration {

	public static final ConfigurationHolder USERNAME = new ConfigurationHolder("", "username");
	public static final ConfigurationHolder PASSWORD = new ConfigurationHolder("", "userpasshash");
	public static final ConfigurationHolder SERVER_ADDRESS = new ConfigurationHolder("se1.cs.uni-kassel.de", "serveraddress");
	public static final ConfigurationHolder SERVER_PORT = new ConfigurationHolder(5000, "serverport");
	public static final ConfigurationHolder SAVE_PASSWORD = new ConfigurationHolder(false, "save-password");

	public Configuration()
	{
		super(new YamlConfiguration(new File("config.yml")));
	}
}
