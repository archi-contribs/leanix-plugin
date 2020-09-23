/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */

package org.archicontribs.leanix.preferences;

import java.sql.SQLException;
import java.util.ArrayList;

import org.archicontribs.leanix.LeanixPlugin;
import org.archicontribs.leanix.GUI.LeanixGui;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * A field editor that manages the list of databases configurations
 * 
 * @author Herve Jouin
 */
public class LeanixGraphqlVariablesEditor extends FieldEditor {
	Group grpGraphql;
	Table tblGraphql;

	Button btnUp;
	Button btnNew;
	Button btnCheck;
	Button btnRemove;
	Button btnEdit;
	Button btnDown;
	
	Composite parent = null;
	
	private int defaultMargin = 10;

	/**
	 * Creates a table field editor.
	 * @param name 
	 * @param labelText 
	 * @param parent 
	 */
	public LeanixGraphqlVariablesEditor(String name, String labelText, Composite parent) {
		init(name, labelText);
		createControl(parent);		// calls doFillIntoGrid
	}
	
	public void setLayoutData(Object layoutData) {
		this.grpGraphql.setLayoutData(layoutData);
	}

	/*
	 * (non-Javadoc) Method declared in FieldEditor.
	 * 
	 * called by createControl(parent)
	 */
	@Override
    @SuppressWarnings("unused")
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		// we create a composite with layout as FormLayout
		this.grpGraphql = new Group(parent, SWT.NONE);
		this.grpGraphql.setFont(parent.getFont());
		this.grpGraphql.setLayout(new FormLayout());
		this.grpGraphql.setBackground(LeanixGui.COMPO_BACKGROUND_COLOR);
		this.grpGraphql.setText("Databases: ");
		
        /*
         * We calculate the default height of a Text widget
         */
        Button button = new Button(this.grpGraphql, SWT.NONE);
        button.setText("Test");
        int defaultButtonHeight = button.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
        button.dispose();
        
		// we calculate the required height of the group:
		//    height of the graphql table  = height of 5 text widgets
		int requiredHeight = 5 * (defaultButtonHeight + this.defaultMargin/2);
		
		GridData gd = new GridData();
		gd.heightHint = requiredHeight;
		gd.minimumHeight = requiredHeight;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		this.grpGraphql.setLayoutData(gd);
		
		this.btnUp = new Button(grpGraphql, SWT.NONE);
		this.btnUp.setText("^");
		FormData fd = new FormData();
		fd.top = new FormAttachment(0, this.defaultMargin);
		fd.left = new FormAttachment(100, -80);
		fd.right = new FormAttachment(100, -45);
		this.btnUp.setLayoutData(fd);
		this.btnUp.addSelectionListener(new SelectionAdapter() {
			/** this callback is called each time the button is pressed */
			@Override public void widgetSelected(SelectionEvent e) { swapGraphqleEntries(-1); }
		});
		this.btnUp.setEnabled(false);

		this.btnDown = new Button(grpGraphql, SWT.NONE);
		this.btnDown.setText("v");
		fd = new FormData();
		fd.top = new FormAttachment(this.btnUp, 0, SWT.TOP);
		fd.left = new FormAttachment(100, -40);
		fd.right = new FormAttachment(100, -this.defaultMargin);
		this.btnDown.setLayoutData(fd);
		this.btnDown.addSelectionListener(new SelectionAdapter() {
			/** this callback is called each time the button is pressed */
			@Override public void widgetSelected(SelectionEvent e) { swapGraphqleEntries(1); }
		});
		this.btnDown.setEnabled(false);

		this.btnNew = new Button(grpGraphql, SWT.NONE);
		this.btnNew.setText("New");
		fd = new FormData();
		fd.top = new FormAttachment(this.btnUp, this.defaultMargin/2);
		fd.left = new FormAttachment(100, -80);
		fd.right = new FormAttachment(100, -this.defaultMargin);
		this.btnNew.setLayoutData(fd);
		this.btnNew.addSelectionListener(new SelectionAdapter() {
			/** this callback is called each time the button is pressed */
			@Override public void widgetSelected(SelectionEvent e) { newCallback(parent); }
		});

		this.btnEdit = new Button(grpGraphql, SWT.NONE);
		this.btnEdit.setText("Edit");
		fd = new FormData();
		fd.top = new FormAttachment(this.btnNew, this.defaultMargin/2);
		fd.left = new FormAttachment(this.btnNew, 0, SWT.LEFT);
		fd.right = new FormAttachment(this.btnNew, 0, SWT.RIGHT);
		this.btnEdit.setLayoutData(fd);
		this.btnEdit.addSelectionListener(new SelectionAdapter() {
			/** this callback is called each time the button is pressed */
			@Override public void widgetSelected(SelectionEvent e) { editCallback(parent); }
		});
		this.btnEdit.setEnabled(false);

		this.btnRemove = new Button(grpGraphql, SWT.NONE);
		this.btnRemove.setText("Remove");
		fd = new FormData();
		fd.top = new FormAttachment(this.btnEdit, this.defaultMargin/2);
		fd.left = new FormAttachment(this.btnNew, 0, SWT.LEFT);
		fd.right = new FormAttachment(this.btnNew, 0, SWT.RIGHT);
		this.btnRemove.setLayoutData(fd);
		this.btnRemove.addSelectionListener(new SelectionAdapter() {
			/** this callback is called each time the button is pressed */
			@Override public void widgetSelected(SelectionEvent e) { removeCallback(parent);}
		});
		this.btnRemove.setEnabled(false);


		this.tblGraphql = new Table(grpGraphql, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.SINGLE);
		this.tblGraphql.setLinesVisible(true);
		fd = new FormData();
		fd.top = new FormAttachment(this.btnUp, 0, SWT.TOP);
		fd.left = new FormAttachment(0, 10);
		fd.right = new FormAttachment(this.btnNew, -10, SWT.LEFT);
		fd.bottom = new FormAttachment(this.btnRemove, 0, SWT.BOTTOM);
		this.tblGraphql.setLayoutData(fd);
		this.tblGraphql.addControlListener(new ControlAdapter() {
			/** this listener is called each time the table is resized */
			@Override public void controlResized(ControlEvent event) {
				LeanixGraphqlVariablesEditor.this.tblGraphql.getColumns()[0].setWidth(LeanixGraphqlVariablesEditor.this.tblGraphql.getClientArea().width);
			}
		});
		this.tblGraphql.addSelectionListener(new SelectionAdapter() {
			/** this callback is called each time an entry is selected in the table */
			@Override public void widgetSelected(SelectionEvent e) {
				btnEdit.setEnabled(tblGraphql.getSelectionIndex() != -1);
				btnRemove.setEnabled(tblGraphql.getSelectionIndex() != -1);
			}
		});
		new TableColumn(this.tblGraphql, SWT.NONE);

		grpGraphql.layout();
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
    protected void doLoad() {
		//nothing to do
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
    protected void doStore() {
		// nothing to do
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
    public int getNumberOfControls() {
		return 1;
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
    public void setFocus() {
		if ( this.tblGraphql != null )
			this.tblGraphql.setFocus();
	}

	/**
	 * Called when the "new" button has been pressed
	 * @throws SQLException 
	 */
	void newCallback(Composite parent) {
		// we unselect all the lines of the tblGraphql table
		this.tblGraphql.deselectAll();
		LeanixGraphql newGraphqlEntry = editGraphql(parent, null);
		if ( newGraphqlEntry != null ) {
			/* todo */
		}
	}
	
	/**
	 * Called when the "new" button has been pressed
	 * @throws SQLException 
	 */
	void editCallback(Composite parent) {
		int index = this.tblGraphql.getSelectionIndex();
		
		if ( index != -1 ) {
			TableItem sourceItem = this.tblGraphql.getItem(index);
			LeanixGraphql oldGraphqlEntry = (LeanixGraphql)sourceItem.getData("LeanixGraphql");
			LeanixGraphql newGraphqlEntry = editGraphql(parent, oldGraphqlEntry);
			if ( newGraphqlEntry != null ) {
				/* todo */
			}
		}
	}

	/**
	 * Called when the "remove" button has been pressed
	 * @throws SQLException 
	 */
	void removeCallback(Composite parent) {
		// setPresentsDefaultValue(false);
		int index = this.tblGraphql.getSelectionIndex();

		this.tblGraphql.remove(index);
	}
	
	Shell editGraphqlDialogArea = null;
	
	LeanixGraphql editGraphql(Composite parent, LeanixGraphql leanixGraphql) {
		Display display = parent.getDisplay();
		
		editGraphqlDialogArea = new Shell(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE | SWT.BORDER | SWT.TITLE);
		editGraphqlDialogArea.setText(LeanixPlugin.pluginTitle);
		editGraphqlDialogArea.setSize(1024, 768);
		editGraphqlDialogArea.setMinimumSize(800, 600);
		editGraphqlDialogArea.setLayout(new FormLayout());
		
		editGraphqlDialogArea.setData("page", 1);
		
        Button btnNext = new Button(editGraphqlDialogArea, SWT.PUSH);
        Button btnPrevious = new Button(editGraphqlDialogArea, SWT.PUSH);
        Button btnCancel = new Button(editGraphqlDialogArea, SWT.PUSH);
        
        Label graphqlNameLabel = new Label(editGraphqlDialogArea, SWT.NONE);
        Text graphqlName = new Text(editGraphqlDialogArea, SWT.BORDER);
        Label graphqlRequestLabel = new Label(editGraphqlDialogArea, SWT.NONE);
        Text graphqlRequest = new Text(editGraphqlDialogArea, SWT.MULTI | SWT.BORDER | SWT.HORIZONTAL | SWT.VERTICAL);
        Label graphqlVariablesLabel = new Label(editGraphqlDialogArea, SWT.NONE);
        Text graphqlVariables = new Text(editGraphqlDialogArea, SWT.MULTI | SWT.BORDER | SWT.HORIZONTAL | SWT.VERTICAL);
        
        FormData fd = new FormData();
        fd.bottom = new FormAttachment(100, -10);
        fd.width = 100;
        fd.right = new FormAttachment(100, -10);
        btnNext.setLayoutData(fd);
        btnNext.setText("Next");
        btnNext.addSelectionListener(new SelectionAdapter() {
        	/** This callback is called when the next button is pressed */
            @Override public void widgetSelected(SelectionEvent e) {
                int page = (int)editGraphqlDialogArea.getData("page");
                switch ( page ) {
                	// clicking Next on page 1 switches to page 2
                	case 1: editGraphqlDialogArea.setData("page", 2);
                	graphqlNameLabel.setVisible(false);
                	graphqlName.setVisible(false);
                	graphqlRequestLabel.setVisible(false);
                	graphqlRequest.setVisible(false);
                	graphqlVariablesLabel.setVisible(false);
                	graphqlVariables.setVisible(false);
                	btnNext.setText("Ok");
                	btnPrevious.setEnabled(true);
                	break;
                }
            }
        });
        
        fd = new FormData();
        fd.bottom = new FormAttachment(100, -10);
        fd.width = 100;
        fd.right = new FormAttachment(btnNext, -10, SWT.LEFT);
        btnPrevious.setLayoutData(fd);
        btnPrevious.setText("Previous");
        btnPrevious.setEnabled(false);
        btnPrevious.addSelectionListener(new SelectionAdapter() {
        	/** this callback is called when the previous button is called */
            @Override public void widgetSelected(SelectionEvent e) {
                int page = (int)editGraphqlDialogArea.getData("page");
                switch ( page ) {
                	// clicking on previous when on page 2 switches to page 1
                	case 2: editGraphqlDialogArea.setData("page", 1);
                	graphqlNameLabel.setVisible(true);
                	graphqlName.setVisible(true);
                	graphqlRequestLabel.setVisible(true);
                	graphqlRequest.setVisible(true);
                	graphqlVariablesLabel.setVisible(true);
                	graphqlVariables.setVisible(true);
                	btnPrevious.setEnabled(true);
                	btnNext.setText("Next");;
                	break;
                }
            }
        });
        
        fd = new FormData();
        fd.bottom = new FormAttachment(100, -10);
        fd.width = 100;
        fd.left = new FormAttachment(0, 10);
        btnCancel.setLayoutData(fd);
        btnCancel.setText("Cancel");
        btnCancel.addSelectionListener(new SelectionAdapter() {
        	/** this callback is called when the cancel button is pressed */
            @Override public void widgetSelected(SelectionEvent e) {
                if ( LeanixGui.question("Are you sure you wish to cancel ?") ) {
                	editGraphqlDialogArea.setData("hasBeenCanceled", true);
                	editGraphqlDialogArea.dispose();
                }
            }
        });
        
        
        graphqlNameLabel.setText("GraphQL name:");
        fd = new FormData();
        fd.top = new FormAttachment(0, 10);
        fd.left = new FormAttachment(0, 10);
        graphqlNameLabel.setLayoutData(fd);
        
        fd = new FormData();
        fd.top = new FormAttachment(graphqlNameLabel, 0, SWT.CENTER);
        fd.left = new FormAttachment(graphqlNameLabel, 30);
        fd.right = new FormAttachment(100, -10);
        graphqlName.setLayoutData(fd);
        
        graphqlRequestLabel.setText("GraphQL request (must be JSON):");
        fd = new FormData();
        fd.top = new FormAttachment(graphqlName, 10, SWT.BOTTOM);
        fd.left = new FormAttachment(0, 10);
        graphqlRequestLabel.setLayoutData(fd);
        
        fd = new FormData();
        fd.top = new FormAttachment(graphqlRequestLabel, 10);
        fd.bottom = new FormAttachment(60, 0);
        fd.left = new FormAttachment(0, 10);
        fd.right = new FormAttachment(100, -10);
        graphqlRequest.setLayoutData(fd);
        
        graphqlRequestLabel.setText("GraphQL variables (must be JSON):");
        fd = new FormData();
        fd.top = new FormAttachment(graphqlRequest, 10, SWT.BOTTOM);
        fd.left = new FormAttachment(0, 10);
        graphqlVariablesLabel.setLayoutData(fd);
        
        fd = new FormData();
        fd.top = new FormAttachment(graphqlVariablesLabel, 10);
        fd.bottom = new FormAttachment(btnCancel, -10);
        fd.left = new FormAttachment(0, 10);
        fd.right = new FormAttachment(100, -10);
        graphqlVariables.setLayoutData(fd);
        
        
        if ( leanixGraphql == null ) {
        	graphqlName.setText("");
        	graphqlRequest.setText("");
        	graphqlVariables.setText("");
        }
        
        editGraphqlDialogArea.open();
        editGraphqlDialogArea.layout();
        
        
        while (!editGraphqlDialogArea.isDisposed()) {
        	if (!display.readAndDispatch())
        		display.sleep();
        }
		
        if ( (boolean)editGraphqlDialogArea.getData("hasBeenCanceled") )
        		return null;
        
        LeanixGraphql newGraphqlEntry = new LeanixGraphql();
		//TODO: fill in entry variable
		
		return newGraphqlEntry;
		
	}

	/**
	 * Moves the currently selected item up or down.
	 *
	 * @param direction
	 *            <code>true</code> if the item should move up, and
	 *            <code>false</code> if it should move down
	 */
	void swapGraphqleEntries(int direction) {
		int source = this.tblGraphql.getSelectionIndex();
		int target = this.tblGraphql.getSelectionIndex()+direction;

		TableItem sourceItem = this.tblGraphql.getItem(source);
		String sourceText = sourceItem.getText();
		LeanixGraphql sourceData = (LeanixGraphql)sourceItem.getData("LeanixGraphql");

		TableItem targetItem = this.tblGraphql.getItem(target);
		String targetText = targetItem.getText();
		LeanixGraphql targetData = (LeanixGraphql)targetItem.getData("LeanixGraphql");

		sourceItem.setText(targetText);
		sourceItem.setData("LeanixGraphqlTableEditorEntry", targetData);
		targetItem.setText(sourceText);
		targetItem.setData("LeanixGraphqlTableEditorEntry", sourceData);

		this.tblGraphql.setSelection(target);
		this.tblGraphql.notifyListeners(SWT.Selection, new Event());
	}

	/**
	 * If we are in edit mode, then ask the user is if wants to save or discard
	 * @throws SQLException 
	 */
	public void close() throws SQLException {
		//todo: check if something has been modified and ask for saving if required
	}

    @Override
    protected void adjustForNumColumns(int numColumns) {
        // nothing to do
    }

    @Override
    protected void doLoadDefault() {
        // nothing to do
    }
    
    public void setVisible(boolean mustBeVisible) {
    	this.editGraphqlDialogArea.setVisible(mustBeVisible);
    }
}
