/**
 * This class describes the properties to match between LeanIX objects and Archi objects
 * 
 * @author Herve Jouin
 */

package org.archicontribs.leanix.graphql;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import lombok.Getter;
import lombok.Setter;

public class LeanixGraphqlMap extends LeanixGraphqlProperty {
	/** ServiceNow class */
	@Getter @Setter private LeanixArchiClass archiClass = null;
	
	/** Other LeanIX properties to set to ServiceNow object */
	@Getter private List<LeanixGraphqlProperty> otherPoperties = null;
	
	public LeanixGraphqlMap(String leanixProperty, LeanixArchiClass archiClass, String archiProperty) {
		super(leanixProperty, archiProperty);
		
		Assert.isNotNull(archiClass, "archiClass must not be null");
		
		this.archiClass = archiClass;
		this.otherPoperties = new ArrayList<LeanixGraphqlProperty>();
	}
	
	//todo: add actions (to upper, to lower, ...)
}
