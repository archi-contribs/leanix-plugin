/**
 * This class opens up the import from leanix window 
 * 
 * @author Herve Jouin
 **/
package org.archicontribs.leanix.GUI;

import java.util.ArrayList;

import org.apache.log4j.Level;
import org.archicontribs.leanix.preferences.LeanixGraphql;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Group;

import com.archimatetool.model.IArchimateModel;

public class LeanixGuiImportFromLeanix extends LeanixGui {
	private Group grpChooseTemplate = null;
	private Group grpFillInVariables = null;

	public LeanixGuiImportFromLeanix(IArchimateModel model, String title) {
		super(title);
		
		// We activate the Eclipse Help framework
		setHelpHref("importFromLeanix.html");
		
		// We activate the btnDoAction button: if the user select the "Import" button --> call the doImport() method
		setBtnAction("Import", new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				LeanixGuiImportFromLeanix.this.btnDoAction.setEnabled(false);
				try {
					doImport();
				} catch (Exception err) {
					LeanixGui.popup(Level.ERROR, "An exception has been raised during import.", err);
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent event) { widgetSelected(event); }
		});
		
		// We rename the "close" button to "cancel"
        this.btnClose.setText("Cancel");
        
        getGraphqls();
	}
	
	void doImport() {
		this.grpFillInVariables = new Group(this.compoRightBottom, SWT.SHADOW_ETCHED_IN);
        this.grpFillInVariables.setVisible(false);
        this.grpFillInVariables.setBackground(GROUP_BACKGROUND_COLOR);
        this.grpFillInVariables.setFont(GROUP_TITLE_FONT);
        this.grpFillInVariables.setText("Your model's components: ");
        
        FormData fd = new FormData();
        fd.top = new FormAttachment(this.grpChooseTemplate, getDefaultMargin());
        fd.left = new FormAttachment(0);
        fd.right = new FormAttachment(100);
        fd.bottom = new FormAttachment(100);
        this.grpFillInVariables.setLayoutData(fd);
        this.grpFillInVariables.setLayout(new FormLayout());
	}

	@Override protected void graphqlSelected() {
		// TODO Auto-generated method stub
		int index = comboGraphqls.getSelectionIndex();
		@SuppressWarnings("unchecked")
		LeanixGraphql leanixGraphql = ((ArrayList<LeanixGraphql>)comboGraphqls.getData("LeanixGraphqlList")).get(index);
		
		logger.debug("GraphQL selected: ");
		logger.debug("     name: "+leanixGraphql.getName());
		logger.debug("     desc: "+leanixGraphql.getDescription());
	}
}
