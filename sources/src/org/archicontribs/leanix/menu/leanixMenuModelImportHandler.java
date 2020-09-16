package org.archicontribs.leanix.menu;

import org.apache.log4j.Level;
import org.archicontribs.leanix.LeanixLogger;
// import org.archicontribs.leanix.GUI.LeanixGui;
// import org.archicontribs.leanix.GUI.LeanixGuiImportModel;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class leanixMenuModelImportHandler extends AbstractHandler {
	private static final LeanixLogger logger = new LeanixLogger(leanixMenuModelImportHandler.class);
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if ( logger.isDebugEnabled() ) logger.debug("Launching Import model window");

        try {
        	// LeanixGuiImportModel importModel = new LeanixGuiImportModel("Import model");
        	// importModel.run();
        } catch (Exception e) {
            // LeanixGui.popup(Level.ERROR,"Cannot import model", e);
        }
        
		return null;
	}
}
