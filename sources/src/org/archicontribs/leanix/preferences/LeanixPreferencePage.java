package org.archicontribs.leanix.preferences;

import org.archicontribs.leanix.LeanixLogger;
import org.archicontribs.leanix.LeanixPlugin;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class LeanixPreferencePage extends FieldEditorPreferencePage	implements IWorkbenchPreferencePage {
	/**
	 * Preference store
	 */
	private static final IPersistentPreferenceStore preferenceStore = LeanixPlugin.INSTANCE.getPreferenceStore();

	/**
	 * logger
	 */
	LeanixLogger logger = new LeanixLogger(LeanixPreferencePage.class);

	
	/**
	 * Creates the preference page
	 */
	public LeanixPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
		
		this.logger.debug("Setting preference store");
		setPreferenceStore(preferenceStore);
	}

	@Override
	public void init(IWorkbench workbench) {
		// nothing to do
	}

	@Override
	protected void createFieldEditors() {
		// TODO Auto-generated method stub
		
	}

}
