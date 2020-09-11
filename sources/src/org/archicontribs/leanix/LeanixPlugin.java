/**
 * LeanIX Model Importer / Exporter
 * 
 * The LeanixPlugin class implements static methods and properties used everywhere else in the Leanix plugin. 
 * 
 * @author Herve Jouin
 **/
package org.archicontribs.leanix;

import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.Version;

public class LeanixPlugin extends Plugin {
	/**
	 * ID of the plugin
	 */
	public static final String PLUGIN_ID = "org.archicontribs.leanix";
	
	/** 
	 * Name of the plugin
	 */
	public static final String pluginName = "LeanixPlugin";
	
	/**
	 * version of the plugin
	 */
	public static Version pluginVersion = Platform.getBundle(PLUGIN_ID).getVersion();
	
	/**
	 * Title of the plugin's windows
	 */
	public static final String pluginTitle = "Leanix import/export plugin v" + pluginVersion.toString();
	
	/**
	 * static instance that allow to keep information between calls of the plugin
	 */
	public static LeanixPlugin INSTANCE;
	
	/**
	 * PreferenceStore allowing to store the plugin configuration.
	 */
	private static IPersistentPreferenceStore preferenceStore = null;
	
	/**
	 * logger used to log messages
	 */
	static LeanixLogger logger;
	
	/**
	 * The DBPlugin class is instantiated when Archi starts<b>
	 * It:<br>
	 *    1- configures the preference store,<br>
	 *    2- defines default values for standard options (in case they've not be defined in the preference store)
	 *    3- checks if a new version of the plugin is available on GitHub
	 */
	public LeanixPlugin() {
		INSTANCE = this;
		
		// forcing UTF-8
		System.setProperty("client.encoding.override", "UTF-8");
		System.setProperty("file.encoding", "UTF-8");
		
		preferenceStore = this.getPreferenceStore();
		preferenceStore.setDefault("loggerMode",		      "simple");
		preferenceStore.setDefault("loggerLevel",		      "INFO");
		preferenceStore.setDefault("loggerFilename",	      System.getProperty("user.home")+File.separator+pluginName+".log");
		preferenceStore.setDefault("loggerExpert",
		        "log4j.rootLogger                               = INFO, stdout, file\n"+
				"\n"+
				"log4j.appender.stdout                          = org.apache.log4j.ConsoleAppender\n"+
				"log4j.appender.stdout.Target                   = System.out\n"+
				"log4j.appender.stdout.layout                   = org.apache.log4j.PatternLayout\n"+
				"log4j.appender.stdout.layout.ConversionPattern = %d{yyyy-MM-dd HH:mm:ss} %-5p %4L:%-40.40C{1} %m%n\n"+
				"\n"+
				"log4j.appender.file                            = org.apache.log4j.FileAppender\n"+
				"log4j.appender.file.ImmediateFlush             = true\n"+
				"log4j.appender.file.Append                     = false\n"+
				"log4j.appender.file.Encoding                   = UTF-8\n"+
				"log4j.appender.file.File                       = "+(System.getProperty("user.home")+File.separator+pluginName+".log").replace("\\", "\\\\")+"\n"+
				"log4j.appender.file.layout                     = org.apache.log4j.PatternLayout\n"+
				"log4j.appender.file.layout.ConversionPattern   = %d{yyyy-MM-dd HH:mm:ss} %-5p %4L:%-40.40C{1} %m%n");
		
		logger = new LeanixLogger(LeanixPlugin.class);
		logger.info("Initialising "+pluginName+" plugin ...");
		
		logger.info("===============================================");
		System.out.println("Im here !");
	}
	
	/**
	 * gets the preference store
	 */
	public IPersistentPreferenceStore getPreferenceStore() {
		if (preferenceStore == null) {
			preferenceStore = new ScopedPreferenceStore( InstanceScope.INSTANCE, PLUGIN_ID );
		}
		return preferenceStore;
	}
}
