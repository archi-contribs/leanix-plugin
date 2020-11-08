/**
 * This class is instantiated each time the user clicks on the "import from meanix" menu
 * 
 * @author Herve JOUIN
 */

package org.archicontribs.leanix;

import java.io.IOException;

import org.apache.log4j.Level;
import org.archicontribs.leanix.GUI.LeanixGui;
import org.archicontribs.leanix.GUI.LeanixGuiImportFromLeanix;

import com.archimatetool.editor.model.ISelectedModelImporter;
import com.archimatetool.model.IArchimateModel;

public class LeanixImporter implements ISelectedModelImporter {
	
	@Override
	public void doImport(IArchimateModel model) throws IOException {
		try {
			LeanixGuiImportFromLeanix importFromLeanix = new LeanixGuiImportFromLeanix(model, "Import from LeanIX");
			importFromLeanix.run();
		} catch (Exception e) {
            LeanixGui.popup(Level.ERROR,"Import failed.", e);
        }
	}
}