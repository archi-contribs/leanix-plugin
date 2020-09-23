/**
 * This class describes the properties to match between LeanIX objects and Archi objects
 * 
 * @author Herve Jouin
 */

package org.archicontribs.leanix.graphql;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@AllArgsConstructor
public class LeanixGraphqlProperty {
	/** LeanIX property */
	@Getter @Setter @NonNull private String leanixProperty = null;
	
	/** ServiceNow property */
	@Getter @Setter @NonNull private String archiProperty = null;
	
	//todo: add actions (to upper, to lower, ...)
}
