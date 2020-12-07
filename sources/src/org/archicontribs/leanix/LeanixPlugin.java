/**
 * LeanIX component Importer
 * 
 * The LeanixPlugin class implements static methods and properties used everywhere else in the Leanix plugin. 
 * 
 * @author Herve Jouin
 **/
package org.archicontribs.leanix;

import java.io.File;

import org.archicontribs.leanix.GUI.LeanixGui;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.swt.widgets.Display;
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
		preferenceStore.setDefault("leanixHost",				"www.leanix.com");
		preferenceStore.setDefault("leanixApiToken",			"");
		preferenceStore.setDefault("nbLeanixGraphql",			1);
		preferenceStore.setDefault("leanixGraphql_0",			"{\n"
																+ "   \"name\": \"Get application by name\",\n"
																+ "   \"description\": \"Get an application from Leanix provided its name\",\n"
																+ "   \"query\": \"query {\n   factSheet(type: Application, name: $name) {\n      id\n      displayName\n      ... on Application {\n         businessCriticality\n      }\n   }\n}\",\n"
																+ "   \"variables\": [\n      {\n         \"variable\": \"$name\",\n         \"label\": \"Nom de l'application :\",         \"defaultValue\": \"\"\n      }\n   ],\n"
																+ "   \"maps\": [\n      {\n         \"leanixProperty\": \"id\",\n         \"archiClass\": \"ApplicationComponent\",\n         \"archiProperty\": \"property:Leanix ID\",\n         \"otherProperties\": [\n            {\n               \"leanix_property\": \"displayName\",\n               \"archi_property\": \"name\"\n            }\n         ]\n      }\n   ]\n"
																+ "}");
		preferenceStore.setDefault("loggerMode",				"simple");
		preferenceStore.setDefault("loggerLevel",				"INFO");
		preferenceStore.setDefault("loggerFilename",			System.getProperty("user.home")+File.separator+pluginName+".log");
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
		
		// we force the class initialization by the SWT thread
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				LeanixGui.closePopup();
			}
		});
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
	
	/**
	 * Check if two strings are equals
	 * <br><br>
	 * Replaces string.equals() to avoid nullPointerException
	 * @param str1 first string to compare
	 * @param str2 secong string to compare
	 * @return true if the strings are both null or have the same content, false if they are different
	 */
	public static boolean areEqual(String str1, String str2) {
		if ( str1 == null )
			return str2 == null;

		if ( str2 == null )
			return false;			// as str1 cannot be null at this stage

		return str1.equals(str2);
	}
	
	/**
	 * Check if a string  is null or empty
	 * <br><br>
	 * Replaces string.isEmpty() to avoid nullPointerException
	 * @param str string to check
	 * @return true if the string is null or empty, false if the string contains at least one char
	 */
	public static boolean isEmpty(String str) {
		return (str==null) || str.isEmpty();
	}
}
