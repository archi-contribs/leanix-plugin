/**
 * This class contains all the information required to send a GraphQL query to LeanIX 
 * 
 * @author Herve Jouin
 */

package org.archicontribs.leanix.preferences;

import java.util.ArrayList;

import org.archicontribs.leanix.graphql.LeanixGraphqlMap;
import org.archicontribs.leanix.graphql.LeanixGraphqlVariable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class LeanixGraphql {
	/** Entry's name */
	@Getter @Setter private String name = null;
	
	/** Entry's graphql request */
	@Getter @Setter private String request = null;
	
	/** Entry's variables */
	@Getter private ArrayList<LeanixGraphqlVariable> variables = null;
	
	/** Entry's maps */
	@Getter private ArrayList<LeanixGraphqlMap> maps = null;
}
