/**
 * This class contains all the information required to send a GraphQL query to LeanIX 
 * 
 * @author Herve Jouin
 */

package org.archicontribs.leanix.preferences;

import java.util.ArrayList;

import org.apache.log4j.Level;
import org.archicontribs.leanix.LeanixLogger;
import org.archicontribs.leanix.GUI.LeanixGui;
import org.archicontribs.leanix.graphql.LeanixGraphqlMap;
import org.archicontribs.leanix.graphql.LeanixGraphqlVariable;
import org.eclipse.jface.preference.IPreferenceStore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@NoArgsConstructor
public class LeanixGraphql {
	private final static LeanixLogger logger = new LeanixLogger(LeanixGraphql.class);
	private final static Gson gson = new GsonBuilder().create();
	
	/** Entry's name */
	@Getter @Setter private String name = null;
	
	/** Entry's dexcription */
	@Getter @Setter private String description = null;
	
	/** Entry's graphql query */
	@Getter @Setter private String query = null;
	
	/** Entry's variables */
	@Getter private ArrayList<LeanixGraphqlVariable> variables = null;
	
	/** Entry's maps */
	@Getter private ArrayList<LeanixGraphqlMap> maps = null;
	
	public static ArrayList<LeanixGraphql> getAllFromPreferenceStore(IPreferenceStore preferenceStore) {
		ArrayList<LeanixGraphql> leanixGraphqlList = new ArrayList<LeanixGraphql>();

    	int nbLeanixGraphql = preferenceStore.getInt("nbLeanixGraphql");
    	logger.debug("Getting "+nbLeanixGraphql+" Leanix GrapqhQL entries from the preference store");
    	
    	for ( int i = 0; i < nbLeanixGraphql; ++i ) {
    		try {
    			LeanixGraphql leanixGraphql = gson.fromJson(preferenceStore.getString("leanixGraphql_"+i), LeanixGraphql.class);
    			logger.trace("Got graphql entry \""+leanixGraphql.getName()+"\"");
    			leanixGraphqlList.add(leanixGraphql);
    		} catch (Exception err) { // MalformedJsonException
    			LeanixGui.popup(Level.ERROR, "Failed to parse graphql entry from preference store.\n\nPlease check \"leanixGraphql_"+i+"\" entry.", err);
    		}
    	}
		
		return leanixGraphqlList;
	}
	
	public static void storeAllToPreferenceStore(IPreferenceStore preferenceStore, ArrayList<LeanixGraphql> leanixGraphqlList) {
		int nbLeanixGraphql = leanixGraphqlList.size(); 
		preferenceStore.setValue("nbLeanixGraphql", nbLeanixGraphql);
		
		logger.debug("Storing "+nbLeanixGraphql+" Leanix GrapqhQL entries to the preference store");
		
		for ( int i = 0; i < nbLeanixGraphql; ++i ) {
			LeanixGraphql leanixGraphql = leanixGraphqlList.get(i);
			preferenceStore.setValue("leanixGraphql_"+i, gson.toJson(leanixGraphql));
			
			logger.trace("Store graphql entry \""+leanixGraphql.getName()+"\"");
		}
	}
	
	/** we delete all the graphql entries from the preference store */
	public static void cleanupPreferenceStore(IPreferenceStore preferenceStore) {
		logger.debug("Cleaning up all LeanIX GrapqhQL entries to the preference store");

		preferenceStore.setDefault("nbLeanixGraphql", 0);
		preferenceStore.setValue("nbLeanixGraphql", 0);
		
		// it is unlikely that user has got more than 100 configured GraphQLs
		for ( int i = 0; i < 100 ; ++i ) {
			preferenceStore.setDefault("leanixGraphql_"+i, "");
			preferenceStore.setValue("leanixGraphql_"+i, "");
		}
	}
}
