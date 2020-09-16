package org.archicontribs.leanix.preferences;

import org.archicontribs.leanix.GUI.LeanixGui;

import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.log4j.Level;
import org.archicontribs.leanix.LeanixLogger;
import org.archicontribs.leanix.LeanixPlugin;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

public class LeanixPreferencePage extends FieldEditorPreferencePage	implements IWorkbenchPreferencePage {
	/** Preference store */
	protected static final IPersistentPreferenceStore preferenceStore = LeanixPlugin.INSTANCE.getPreferenceStore();

	/** logger */
	LeanixLogger logger = new LeanixLogger(LeanixPreferencePage.class);

	/** Mode levels to configure the logger */
	private static String[][] LOGGER_MODES = {{"Disabled", "disabled"}, {"Simple mode", "simple"}, {"Expert mode", "expert"}};

	/** Levels of the logger */
	private static String[][] LOGGER_LEVELS = {{"Fatal", "fatal"}, {"Error", "error"}, {"Warn", "warn"}, {"Info", "info"}, {"Debug", "debug"}, {"Trace", "trace"}};

	/** Help Id */
	private static String HELP_ID = "org.archicontribs.database.preferences.configurePlugin";

	/** Composite Window that will allow to configure the logger */
	private Composite loggerComposite;

	/** Tab folder that will holds the behaviour and logger tabs on the preference page */
	private TabFolder tabFolder;

	/** Text zone where the user must configure the URL of LeanIX */
	private Text txtLeanixURL;

	/** Text zone where the user must configure the API Key of LeanIX */
	private Text txtApiKey;

	/** Store the status of the mouse beeing over the Help Button or not */
	protected boolean mouseOverHelpButton = false;
	
	private RadioGroupFieldEditor loggerModeRadioGroupEditor;
	private LeanixTextFieldEditor expertTextFieldEditor;
	private BooleanFieldEditor traceGraphqlFieldEditor;
	private RadioGroupFieldEditor loggerLevelRadioGroupEditor;
	private FileFieldEditor filenameFileFieldEditor;
	
	private Group simpleModeGroup;
	private Group expertModeGroup;


	/**
	 * Creates the preference page
	 */
	public LeanixPreferencePage() {
		super(FieldEditorPreferencePage.GRID);

		this.logger.debug("Setting preference store");
		setPreferenceStore(preferenceStore);
	}

	@Override
	protected void createFieldEditors() {
		this.logger.debug("Creating preference page");

		this.tabFolder = new TabFolder(getFieldEditorParent(), SWT.NONE);
		this.tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		this.tabFolder.setBackground(LeanixGui.GROUP_BACKGROUND_COLOR);

		// ******************************** */
		// * Behaviour tab **************** */
		// ******************************** */
		Composite behaviourComposite = new Composite(this.tabFolder, SWT.NULL);
		RowLayout rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
		rowLayout.pack = true;
		rowLayout.marginTop = 5;
		rowLayout.marginBottom = 5;
		rowLayout.justify = false;
		rowLayout.fill = false;
		behaviourComposite.setLayoutData(rowLayout);
		behaviourComposite.setBackground(LeanixGui.GROUP_BACKGROUND_COLOR);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 0;
		layout.marginHeight = 10;
		layout.horizontalSpacing = 8;
		behaviourComposite.setLayout(layout);

		TabItem behaviourTabItem = new TabItem(this.tabFolder, SWT.NONE);
		behaviourTabItem.setText("  Behaviour  ");
		behaviourTabItem.setControl(behaviourComposite);

		Group grpVersion = new Group(behaviourComposite, SWT.NONE);
		grpVersion.setBackground(LeanixGui.COMPO_BACKGROUND_COLOR);
		grpVersion.setLayout(new FormLayout());
		grpVersion.setText("Version: ");

		Label versionLbl = new Label(grpVersion, SWT.NONE);
		versionLbl.setText("Actual version:");
		versionLbl.setBackground(LeanixGui.COMPO_BACKGROUND_COLOR);
		FormData fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.left = new FormAttachment(0, 10);
		versionLbl.setLayoutData(fd);

		Label versionValue = new Label(grpVersion, SWT.NONE);
		versionValue.setText(LeanixPlugin.pluginVersion.toString());
		versionValue.setBackground(LeanixGui.COMPO_BACKGROUND_COLOR);
		versionValue.setFont(LeanixGui.BOLD_FONT);
		fd = new FormData();
		fd.top = new FormAttachment(versionLbl, 0, SWT.TOP);
		fd.left = new FormAttachment(versionLbl, 5);
		versionValue.setLayoutData(fd);
		
		GridData gd = new GridData();
		//gd.heightHint = 45;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		grpVersion.setLayoutData(gd);

		Group grpMiscellaneous = new Group(behaviourComposite, SWT.NONE);
		grpMiscellaneous.setBackground(LeanixGui.COMPO_BACKGROUND_COLOR);
		grpMiscellaneous.setText("Miscellaneous:");
		grpMiscellaneous.setLayout(new FormLayout());

		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		grpMiscellaneous.setLayoutData(gd);

		Label lblURL = new Label(grpMiscellaneous, SWT.NONE);
		lblURL.setBackground(LeanixGui.COMPO_BACKGROUND_COLOR);
		lblURL.setText("LeanIX URL:");
		fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.left = new FormAttachment(0, 10);
		lblURL.setLayoutData(fd);

		this.txtLeanixURL = new Text(grpMiscellaneous, SWT.BORDER);
		this.txtLeanixURL.setText(preferenceStore.getString("leanixURL"));
		fd = new FormData();
		fd.top = new FormAttachment(lblURL, -3, SWT.TOP);
		fd.bottom = new FormAttachment(lblURL, 3, SWT.BOTTOM);
		fd.left = new FormAttachment(lblURL, 10);
		fd.right = new FormAttachment(lblURL, 450, SWT.RIGHT);
		this.txtLeanixURL.setLayoutData(fd);

		Label lblApiKey = new Label(grpMiscellaneous, SWT.NONE);
		lblApiKey.setBackground(LeanixGui.COMPO_BACKGROUND_COLOR);
		lblApiKey.setText("Api Key:");
		fd = new FormData();
		fd.top = new FormAttachment(lblURL, 10);
		fd.left = new FormAttachment(0, 10);
		lblApiKey.setLayoutData(fd);

		this.txtApiKey = new Text(grpMiscellaneous, SWT.BORDER);
		this.txtApiKey.setText(preferenceStore.getString("leanixApiKey"));
		fd = new FormData();
		fd.top = new FormAttachment(lblApiKey, -3, SWT.TOP);
		fd.bottom = new FormAttachment(lblApiKey, 3, SWT.BOTTOM);
		fd.left = new FormAttachment(txtLeanixURL, 0, SWT.LEFT);
		fd.right = new FormAttachment(txtLeanixURL, 0, SWT.RIGHT);
		this.txtApiKey.setLayoutData(fd);

		Group grpHelp = new Group(behaviourComposite, SWT.NONE);
		grpHelp.setBackground(LeanixGui.COMPO_BACKGROUND_COLOR);
		grpHelp.setLayout(new FormLayout());
		grpHelp.setText("Online help: ");

		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		grpHelp.setLayoutData(gd);

		Label btnHelp = new Label(grpHelp, SWT.MULTI);
		btnHelp.setText("          \n          ");
		//btnHelp.setBounds(11,10,30,30);
		btnHelp.addListener(SWT.MouseEnter, new Listener() { @Override public void handleEvent(Event event) { LeanixPreferencePage.this.mouseOverHelpButton = true; btnHelp.redraw(); } });
		btnHelp.addListener(SWT.MouseExit, new Listener() { @Override public void handleEvent(Event event) { LeanixPreferencePage.this.mouseOverHelpButton = false; btnHelp.redraw(); } });
		btnHelp.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e)
			{
				if ( LeanixPreferencePage.this.mouseOverHelpButton ) e.gc.drawRoundRectangle(0, 0, 29, 29, 10, 10);
				e.gc.drawImage(LeanixGui.HELP_ICON, 2, 2);
			}
		});
		btnHelp.addListener(SWT.MouseUp, new Listener() { @Override public void handleEvent(Event event) { if ( LeanixPreferencePage.this.logger.isDebugEnabled() ) LeanixPreferencePage.this.logger.debug("Showing help: /"+LeanixPlugin.PLUGIN_ID+"/help/html/configurePlugin.html"); PlatformUI.getWorkbench().getHelpSystem().displayHelpResource("/"+LeanixPlugin.PLUGIN_ID+"/help/html/configurePlugin.html"); } });
		fd = new FormData(30,30);
		fd.top = new FormAttachment(0, 11);
		fd.bottom = new FormAttachment(0, 41);
		fd.left = new FormAttachment(0, 10);
		fd.right= new FormAttachment(0, 40);
		btnHelp.setLayoutData(fd);

		Label helpLbl1 = new Label(grpHelp, SWT.NONE);
		helpLbl1.setText("Please be informed that a help button like this one is available on every plugin window.");
		helpLbl1.setBackground(LeanixGui.COMPO_BACKGROUND_COLOR);
		fd = new FormData();
		fd.top = new FormAttachment(0, 10);
		fd.left = new FormAttachment(btnHelp, 10);
		helpLbl1.setLayoutData(fd);

		Label helpLbl2 = new Label(grpHelp, SWT.NONE);
		helpLbl2.setText("The online help is also available at any time using the menu Help / Help content.");
		helpLbl2.setBackground(LeanixGui.COMPO_BACKGROUND_COLOR);
		fd = new FormData();
		fd.top = new FormAttachment(helpLbl1, 5);
		fd.left = new FormAttachment(btnHelp, 10);
		helpLbl2.setLayoutData(fd);

		// ********************************* */
		// * Logger tab  ******************* */
		// ********************************* */
		this.loggerComposite = new Composite(this.tabFolder, SWT.NONE);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
		rowLayout.pack = true;
		rowLayout.marginTop = 5;
		rowLayout.marginBottom = 5;
		rowLayout.justify = false;
		rowLayout.fill = false;
		this.loggerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		this.loggerComposite.setBackground(LeanixGui.GROUP_BACKGROUND_COLOR);

		TabItem loggerTabItem = new TabItem(this.tabFolder, SWT.NONE);
		loggerTabItem.setText("  Logger  ");
		loggerTabItem.setControl(this.loggerComposite);

		Label note = new Label(this.loggerComposite, SWT.NONE);
		note.setText(" Please be aware that enabling debug or, even more, trace level has got important impact on performances!\n Activate only if required.");
		note.setBackground(LeanixGui.GROUP_BACKGROUND_COLOR);
		note.setForeground(LeanixGui.RED_COLOR);

		this.loggerModeRadioGroupEditor = new RadioGroupFieldEditor("loggerMode", "", 1, LOGGER_MODES, this.loggerComposite, true);
		addField(this.loggerModeRadioGroupEditor);

		this.traceGraphqlFieldEditor = new BooleanFieldEditor("traceSQL", "Include GraphQL requests in trace mode", this.loggerComposite);
		addField(this.traceGraphqlFieldEditor);

		this.simpleModeGroup = new Group(this.loggerComposite, SWT.NONE);
		this.simpleModeGroup.setLayout(new GridLayout());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		//gd.widthHint = 300;
		this.simpleModeGroup.setLayoutData(gd);
		this.simpleModeGroup.setBackground(LeanixGui.GROUP_BACKGROUND_COLOR);


		this.loggerLevelRadioGroupEditor = new RadioGroupFieldEditor("loggerLevel", "", 6, LOGGER_LEVELS, this.simpleModeGroup, false);
		addField(this.loggerLevelRadioGroupEditor);

		this.filenameFileFieldEditor = new LeanixFileFieldEditor("loggerFilename", "Log filename: ", false, StringFieldEditor.VALIDATE_ON_KEY_STROKE, this.simpleModeGroup);
		addField(this.filenameFileFieldEditor);



		this.expertModeGroup = new Group(this.loggerComposite, SWT.NONE);
		this.expertModeGroup.setLayout(new GridLayout());
		gd = new GridData(GridData.FILL_BOTH);
		//gd.widthHint = 350;
		this.expertModeGroup.setLayoutData(gd);
		this.expertModeGroup.setBackground(LeanixGui.GROUP_BACKGROUND_COLOR);

		this.expertTextFieldEditor = new LeanixTextFieldEditor("loggerExpert", "", this.expertModeGroup);
		this.expertTextFieldEditor.getTextControl().setLayoutData(gd);
		this.expertTextFieldEditor.getTextControl().setFont(JFaceResources.getFont(JFaceResources.TEXT_FONT));
		addField(this.expertTextFieldEditor);

		// We activate the Eclipse Help framework
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getFieldEditorParent().getParent(), HELP_ID);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(behaviourComposite, HELP_ID);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this.loggerComposite, HELP_ID);


		showLogger();
	}
	
	@Override
    public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
		
		if ( event.getSource() == this.loggerModeRadioGroupEditor )
			showLogger();
		
		 if( event.getSource() == this.filenameFileFieldEditor )
			 setValid(true);
	}
	
	private void showLogger() {
		String mode = null;
		
		// If the user changes the value, we get it
		for ( Control control: this.loggerModeRadioGroupEditor.getRadioBoxControl(this.loggerComposite).getChildren() ) {
			if (((Button)control).getSelection())
				mode = (String)((Button)control).getData();
		}
		
		// when the preference page initialize, the radioButton selection is not (yet) made.
		// so we get the value from the preferenceStore
		if ( mode == null ) {
			mode = preferenceStore.getString("loggerMode");
    		if ( mode == null ) {
    			mode = preferenceStore.getDefaultString("loggerMode");
    		}
		}
		
		// Defining of the user's choice, we show up the simple or expert parameters or none of them
		switch ( mode ) {
		case "disabled":
			this.expertModeGroup.setVisible(false);
			this.simpleModeGroup.setVisible(false);
			break;
		case "simple":
			this.expertModeGroup.setVisible(false);
			this.simpleModeGroup.setVisible(true);
			break;
		case "expert":
			this.expertModeGroup.setVisible(true);
			this.simpleModeGroup.setVisible(false);
			break;
		default: 
			this.expertModeGroup.setVisible(false);
			this.simpleModeGroup.setVisible(false);
			this.logger.error("Unknown value \""+mode+"\" in loggerModeRadioGroupEditor.");
		}
	}
	
	@Override
    public boolean performOk() {
		this.logger.debug("Saving preferences in preference store");
		
    	preferenceStore.setValue("leanixURL", this.txtLeanixURL.getText());
    	preferenceStore.setValue("leanixApiKey", this.txtApiKey.getText());
		
    	// the loggerMode is a private property, so we use reflection to access it
    			try {
    				Field field = RadioGroupFieldEditor.class.getDeclaredField("value");
    				field.setAccessible(true);
    				if ( this.logger.isTraceEnabled() ) this.logger.trace("   loggerMode = "+(String)field.get(this.loggerModeRadioGroupEditor));
    				field.setAccessible(false);
    			} catch (Exception err) {
    				this.logger.error("Failed to retrieve the \"loggerMode\" value from the preference page", err);
    			}
    			this.loggerModeRadioGroupEditor.store();
    	    	
    	    	// the loggerLevel is a private property, so we use reflection to access it
    			try {
    				Field field = RadioGroupFieldEditor.class.getDeclaredField("value");
    				field.setAccessible(true);
    				if ( this.logger.isTraceEnabled() ) this.logger.trace("   loggerLevel = "+(String)field.get(this.loggerLevelRadioGroupEditor));
    				field.setAccessible(false);
    			} catch (Exception err) {
    				this.logger.error("Failed to retrieve the \"loggerLevel\" value from the preference page", err);
    			}
    			this.loggerLevelRadioGroupEditor.store();
    			
    			this.traceGraphqlFieldEditor.store();
    			
    			if ( this.logger.isTraceEnabled() ) this.logger.trace("   loggerFilename = "+this.filenameFileFieldEditor.getStringValue());
    			this.filenameFileFieldEditor.store();
    			
    			if ( this.logger.isTraceEnabled() ) this.logger.trace("   loggerExpert = "+this.expertTextFieldEditor.getStringValue());
    			this.expertTextFieldEditor.store();
    			
    	        try {
    	        	if ( this.logger.isDebugEnabled() ) this.logger.debug("Saving the preference store to disk.");
    	            preferenceStore.save();
    	        } catch (IOException err) {
    	        	LeanixGui.popup(Level.ERROR, "Failed to save the preference store to disk.", err);
    	        }
    			
    			try {
    				this.logger.configure();
    			} catch (Exception e) {
    				LeanixGui.popup(Level.ERROR, "Faied to configure logger", e);
    			}
    			
    			return true;
	}

	@Override
	public void init(IWorkbench workbench) {
		// nothing to do
	}
}
