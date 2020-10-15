package de.hs.settlers.configuration;

import java.io.File;

public class Configuration extends ConfigurationHolderConfiguration {

	public static final ConfigurationHolder<String> USERNAME = new ConfigurationHolder<>("", "username");
	public static final ConfigurationHolder<String> PASSWORD = new ConfigurationHolder<>("", "userpasshash");
	public static final ConfigurationHolder<String> SERVER_ADDRESS = new ConfigurationHolder<>("localhost", "serveraddress");
	public static final ConfigurationHolder<Integer> SERVER_PORT = new ConfigurationHolder<>(4242, "serverport");
	public static final ConfigurationHolder<Boolean> SAVE_PASSWORD = new ConfigurationHolder<>(false, "save-password");

	public Configuration()
	{
		super(new YamlConfiguration(new File("config.yml")));
	}

	public void load() throws ConfigurationException {

	}

	public void save() throws ConfigurationException {

	}
}
