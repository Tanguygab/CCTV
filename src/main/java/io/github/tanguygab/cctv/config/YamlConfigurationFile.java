package io.github.tanguygab.cctv.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import io.github.tanguygab.cctv.CCTV;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

/**
 * YAML implementation of ConfigurationFile
 */
public class YamlConfigurationFile extends ConfigurationFile {
	
	//instance of snakeyaml
	private final Yaml yaml;
	
	/**
	 * Constructs new instance and tries to load configuration file
	 * @param source - source to copy file from if it does not exist
	 * @param destination - destination of the file to be copied file to if needed and loaded
	 * @throws IllegalStateException - when file does not exist and source is null
	 * @throws YAMLException - when file has invalid yaml syntax
	 * @throws IOException - when an I/O operation with the file fails
	 */
	public YamlConfigurationFile(InputStream source, File destination) throws IllegalStateException, YAMLException, IOException {
		super(source, destination);
		FileInputStream input = null;
		try {
			DumperOptions options = new DumperOptions();
			options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
			yaml = new Yaml(options);
			input = new FileInputStream(file);
			values = yaml.load(new InputStreamReader(input, StandardCharsets.UTF_8));
			if (values == null) values = new HashMap<>();
			input.close();
		} catch (YAMLException e) {
			assert input != null;
			input.close();
			CCTV.getInstance().getLogger().severe("File " + destination + " has broken syntax.");
			CCTV.getInstance().getLogger().severe("Error message from yaml parser: " + e.getMessage());
			throw e;
		}
	}
	
	@Override
	public void save() {
		try {
			Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
			yaml.dump(values, writer);
			writer.close();
		} catch (Throwable e) {
			CCTV.getInstance().getLogger().severe("Failed to save yaml file " + file.getPath() + " with content " + values.toString());
			e.printStackTrace();
		}
	}
}