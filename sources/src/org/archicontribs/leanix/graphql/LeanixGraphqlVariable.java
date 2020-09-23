/**
 * This class describes the graphql variables 
 * 
 * @author Herve Jouin
 */

package org.archicontribs.leanix.graphql;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@AllArgsConstructor
public class LeanixGraphqlVariable {
	/** Name of the variable */
	@Getter @Setter @NonNull String variable = null;
	
	/** label to show to the user */
	@Getter @Setter @NonNull String label = null;
	
	// todo: add type (int, string, ...), validation (regexp), ...
}
