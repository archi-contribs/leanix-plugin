/**
 * This class manages the GUI of the LeanIX plugin.
 * 
 * @author Herve Jouin
 */

package org.archicontribs.leanix.GUI;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.archicontribs.leanix.LeanixLogger;
import org.archicontribs.leanix.LeanixPlugin;
import org.archicontribs.leanix.preferences.LeanixGraphql;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

import lombok.Getter;
import lombok.Setter;


public abstract class LeanixGui {
    protected static final LeanixLogger logger = new LeanixLogger(LeanixGui.class);

    @Getter @Setter private boolean closedByUser = false;

    protected static final Display display = Display.getCurrent() == null ? Display.getDefault() : Display.getCurrent();
    protected Shell dialog;

    String HELP_HREF = null;
    boolean mouseOverHelpButton = false;

    protected enum ACTION {One, Two, Three, Four}
    protected enum STATUS {Empty, Selected, Running, Bypassed, Ok, Warn, Error}

    public static final Color LIGHT_GREEN_COLOR = new Color(display, 204, 255, 229);
    public static final Color LIGHT_RED_COLOR = new Color(display, 255, 230, 230);
    public static final Color RED_COLOR = new Color(display, 240, 0, 0);
    public static final Color GREEN_COLOR = new Color(display, 0, 180, 0);
    public static final Color WHITE_COLOR = new Color(display, 255, 255, 255);
    public static final Color GREY_COLOR = new Color(display, 100, 100, 100);
    public static final Color BLACK_COLOR = new Color(display, 0, 0, 0);
    public static final Color YELLOW_COLOR = new Color(display, 255, 255, 0);

    public static final Color COMPO_LEFT_COLOR = new Color(display, 240, 248, 255);			// light blue
    public static final Color COMPO_BACKGROUND_COLOR = new Color(display, 250, 250, 250);	// light grey
    public static final Color GROUP_BACKGROUND_COLOR = new Color(display, 235, 235, 235);	// light grey (a bit darker than compo background)
    public static final Color TABLE_BACKGROUND_COLOR = new Color(display, 225, 225, 225);	// light grey (a bit darker than group background)
    public static final Color HIGHLIGHTED_COLOR = display.getSystemColor(SWT.COLOR_GRAY);

    public static final Color STRATEGY_COLOR = new Color(display, 255, 222, 170);
    public static final Color BUSINESS_COLOR = new Color(display, 255, 255, 181);
    public static final Color APPLICATION_COLOR = new Color(display, 181, 255, 255);
    public static final Color TECHNOLOGY_COLOR = new Color(display, 201, 231, 183);
    public static final Color PHYSICAL_COLOR = new Color(display, 201, 231, 183);
    public static final Color IMPLEMENTATION_COLOR = new Color(display, 255, 224, 224);
    public static final Color MOTIVATION_COLOR = new Color(display, 204, 204, 255);

    public static final Color PASSIVE_COLOR = new Color(display, 250, 250, 250);

    public static final Cursor CURSOR_WAIT = new Cursor(null, SWT.CURSOR_WAIT);
    public static final Cursor CURSOR_ARROW = new Cursor(null, SWT.CURSOR_ARROW);

    public static final FontData SYSTEM_FONT = display.getSystemFont().getFontData()[0];
    public static final Font GROUP_TITLE_FONT = new Font(display, SYSTEM_FONT.getName(), SYSTEM_FONT.getHeight()+2, SWT.BOLD | SWT.ITALIC);
    public static final Font TITLE_FONT = new Font(display, SYSTEM_FONT.getName(), SYSTEM_FONT.getHeight()+2, SWT.BOLD);
    public static final Font BOLD_FONT = new Font(display, SYSTEM_FONT.getName(), SYSTEM_FONT.getHeight(), SWT.BOLD);

    public static final Image LOGO_IMAGE = new Image(display, LeanixGui.class.getResourceAsStream("/img/logo.png"));

    public static final Image HELP_ICON = new Image(display, LeanixGui.class.getResourceAsStream("/img/28x28/help.png"));

    private Composite compoLeft;
    protected Composite compoRight;
    protected Composite compoRightTop;
    protected Composite compoRightBottom;
    private Composite compoBottom;

    private Group grpGraphqls;
    @Getter protected Combo comboGraphqls;
    private Group grpVariables;
    Set<Widget> setVariablesWidgets = new HashSet<Widget>();
    protected Button btnSetPreferences;
    protected Button btnClose;
    protected Button btnDoAction;
    protected Label btnHelp;

    protected Group grpProgressBar = null;
    protected Label lblProgressBar;
    private ProgressBar progressBar;
    
    protected Group grpMessage = null;
    private CLabel lblMessage;

    /** Default height of a Label widget */
    @Getter private int defaultLabelHeight;

    /** Default margin between widgets */
    @Getter private int defaultMargin = 10;
    
    ArrayList<LeanixGraphql> leanixGraphqlList = null;


    /**
     * Create the dialog with minimal graphical objects: 
     * 		left composite: plugin's logo and version 
     * 		bottom composite: Close, doAction button at the right and help button on the left
     * 		right composite: list of s in a combo and a button to directly access preferences
     */
    protected LeanixGui(String title) {
        logger.debug("Creating Form GUI.");

        setArrowCursor();

        this.dialog = new Shell(display, SWT.BORDER | SWT.TITLE | SWT.APPLICATION_MODAL | SWT.RESIZE);
        this.dialog.setText(LeanixPlugin.pluginTitle + " - " + title);
        this.dialog.setMinimumSize(800, 700);
        this.dialog.setSize(1024, 700);
        
        //int scaleFactor = 1;
        //try {
        //	if ( (Toolkit.getDefaultToolkit().getScreenResolution() != 0) && (this.dialog.getDisplay().getDPI() != null) && (this.dialog.getDisplay().getDPI().x != 0) )
        //		scaleFactor = Toolkit.getDefaultToolkit().getScreenResolution() / this.dialog.getDisplay().getDPI().x;
        //} catch ( @SuppressWarnings("unused") HeadlessException ign) {
        //	// nothing to do
        //}
        //if ( scaleFactor == 0 )
        //	scaleFactor = 1;		// just in case
        //this.dialog.setLocation(((Toolkit.getDefaultToolkit().getScreenSize().width / scaleFactor) - this.dialog.getSize().x) / 2, ((Toolkit.getDefaultToolkit().getScreenSize().height / scaleFactor) - this.dialog.getSize().y) / 2);
        //this.dialog.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - this.dialog.getSize().x) / 2, (Toolkit.getDefaultToolkit().getScreenSize().height - this.dialog.getSize().y) / 2);
        this.dialog.setLayout(new FormLayout());

        /**
         * Calculate the default height of a Label widget
         */
        Label label = new Label(this.dialog, SWT.NONE);
        label.setText("Test");
        this.defaultLabelHeight = label.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
        label.dispose();

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////// compoLeft ////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        this.compoLeft = new Composite(this.dialog, SWT.BORDER);
        this.compoLeft.setBackground(COMPO_LEFT_COLOR);
        FormData fd = new FormData();
        fd.top = new FormAttachment(0);
        fd.left = new FormAttachment(0);
        fd.right = new FormAttachment(0, 160);
        fd.bottom = new FormAttachment(100, -40);
        this.compoLeft.setLayoutData(fd);
        this.compoLeft.setLayout(new FormLayout());

        Composite compoTitle = new Composite(this.compoLeft, SWT.BORDER);
        compoTitle.setBackground(COMPO_LEFT_COLOR);
        fd = new FormData(140,50);
        fd.top = new FormAttachment(0, 40);
        fd.left = new FormAttachment(5);
        fd.right = new FormAttachment(100, -5);
        compoTitle.setLayoutData(fd);
        compoTitle.setLayout(new FormLayout());

        Label lblTitle = new Label(compoTitle, SWT.CENTER);
        lblTitle.setBackground(COMPO_LEFT_COLOR);
        lblTitle.setText("Archi LeanIX plugin");
        lblTitle.setFont(TITLE_FONT);
        fd = new FormData();
        fd.top = new FormAttachment(10);
        fd.left = new FormAttachment(0);
        fd.right = new FormAttachment(100);
        lblTitle.setLayoutData(fd);

        Label lblPluginVersion = new Label(compoTitle, SWT.CENTER);
        lblPluginVersion.setBackground(COMPO_LEFT_COLOR);
        lblPluginVersion.setText(LeanixPlugin.pluginVersion.toString());
        fd = new FormData();
        fd.top = new FormAttachment(lblTitle, 5);
        fd.left = new FormAttachment(0);
        fd.right = new FormAttachment(100);
        lblPluginVersion.setLayoutData(fd);

        Label logo = new Label(this.compoLeft, SWT.CENTER);
        logo.setBackground(COMPO_LEFT_COLOR);
        logo.setImage(LOGO_IMAGE);
        fd = new FormData(135,115);
        fd.top = new FormAttachment(compoTitle, 30);
        fd.left = new FormAttachment(0);
        fd.right = new FormAttachment(100);
        logo.setLayoutData(fd);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////// compoRight ///////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        this.compoRight = new Composite(this.dialog, SWT.BORDER);
        this.compoRight.setBackground(COMPO_BACKGROUND_COLOR);
        FormData fd_compoRight = new FormData();
        fd_compoRight.top = new FormAttachment(0);
        fd_compoRight.bottom = new FormAttachment(100, -40);
        fd_compoRight.left = new FormAttachment(this.compoLeft);
        fd_compoRight.right = new FormAttachment(100);
        this.compoRight.setLayoutData(fd_compoRight);
        this.compoRight.setLayout(new FormLayout());

        this.compoRightTop = new Composite(this.compoRight, SWT.NONE);
        this.compoRightTop.setBackground(COMPO_BACKGROUND_COLOR);
        FormData fd_compoRightUp = new FormData();
        fd_compoRightUp.top = new FormAttachment(0, 10);
        fd_compoRightUp.bottom = new FormAttachment(0, 70);
        fd_compoRightUp.left = new FormAttachment(0, 10);
        fd_compoRightUp.right = new FormAttachment(100, -10);
        this.compoRightTop.setLayoutData(fd_compoRightUp);
        this.compoRightTop.setLayout(new FormLayout());

        this.compoRightBottom = new Composite(this.compoRight, SWT.NONE);
        this.compoRightBottom.setBackground(COMPO_BACKGROUND_COLOR);
        FormData fd_compoRightBottom = new FormData();
        fd_compoRightBottom.top = new FormAttachment(this.compoRightTop, 10);
        fd_compoRightBottom.bottom = new FormAttachment(100, -10);
        fd_compoRightBottom.left = new FormAttachment(0, 10);
        fd_compoRightBottom.right = new FormAttachment(100, -10);
        this.compoRightBottom.setLayoutData(fd_compoRightBottom);
        this.compoRightBottom.setLayout(new FormLayout());

        this.grpGraphqls = new Group(this.compoRightTop, SWT.SHADOW_ETCHED_IN);
        this.grpGraphqls.setVisible(true);
        this.grpGraphqls.setData("visible", true);
        this.grpGraphqls.setBackground(GROUP_BACKGROUND_COLOR);
        this.grpGraphqls.setFont(GROUP_TITLE_FONT);
        this.grpGraphqls.setText("GraphQL requests: ");
        fd = new FormData();
        fd.top = new FormAttachment(0);
        fd.left = new FormAttachment(0);
        fd.right = new FormAttachment(100);
        this.grpGraphqls.setLayoutData(fd);
        this.grpGraphqls.setLayout(new FormLayout());
        
        Label lblRegisteredGraphqls= new Label(this.grpGraphqls, SWT.NONE);
        lblRegisteredGraphqls.setText("Registered GraphQL requests:");
        fd = new FormData();
        fd.top = new FormAttachment(0, 10);
        fd.left = new FormAttachment(0, 10);
        lblRegisteredGraphqls.setLayoutData(fd);
        
        this.btnSetPreferences = new Button(this.grpGraphqls, SWT.NONE);
        this.btnSetPreferences.setText("Set preferences ...");
        this.btnSetPreferences.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) { try { setPreferences(); } catch (Exception e) { popup(Level.ERROR, "Failed to set preferences", e); } }
            @Override
            public void widgetDefaultSelected(SelectionEvent event) { widgetSelected(event); }
        });
        fd = new FormData();
        fd.top = new FormAttachment(lblRegisteredGraphqls, 0, SWT.CENTER);
        fd.right = new FormAttachment(100, -10);
        this.btnSetPreferences.setLayoutData(fd);
        
        this.comboGraphqls = new Combo(this.grpGraphqls, SWT.NONE | SWT.READ_ONLY);
        this.comboGraphqls.setBackground(GROUP_BACKGROUND_COLOR);
        this.comboGraphqls.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) { graphqlSelected(); }
            @Override
            public void widgetDefaultSelected(SelectionEvent event) { widgetSelected(event); }
        });
        fd = new FormData();
        fd.top = new FormAttachment(lblRegisteredGraphqls, 0, SWT.CENTER);
        fd.left = new FormAttachment(lblRegisteredGraphqls, 10);
        fd.right = new FormAttachment(this.btnSetPreferences, -40);
        this.comboGraphqls.setLayoutData(fd);
        
        // we ask the grpGraphqls to calculate its height
        this.grpGraphqls.pack();
        
        this.grpVariables = new Group(this.compoRightBottom, SWT.SHADOW_ETCHED_IN);
        this.grpVariables.setVisible(true);
        this.grpVariables.setBackground(GROUP_BACKGROUND_COLOR);
        this.grpVariables.setFont(GROUP_TITLE_FONT);
        this.grpVariables.setText("GraphQL variables: ");
        fd = new FormData();
        fd.top = new FormAttachment(this.grpGraphqls,defaultMargin);
        fd.left = new FormAttachment(0);
        fd.right = new FormAttachment(100);
        fd.bottom = new FormAttachment(100);
        this.grpVariables.setLayoutData(fd);
        this.grpVariables.setLayout(new FormLayout());

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////// compoBottom //////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        this.compoBottom = new Composite(this.dialog, SWT.NONE);
        this.compoBottom.setBackground(COMPO_BACKGROUND_COLOR);
        fd = new FormData();
        fd.top = new FormAttachment(100, -40);
        fd.left = new FormAttachment(0);
        fd.right = new FormAttachment(100);
        fd.bottom = new FormAttachment(100);
        this.compoBottom.setLayoutData(fd);
        this.compoBottom.setLayout(new FormLayout());

        this.btnHelp = new Label(this.compoBottom, SWT.NONE);
        this.btnHelp.setVisible(false);
        this.btnHelp.addListener(SWT.MouseEnter, new Listener() { @Override public void handleEvent(Event event) { LeanixGui.this.mouseOverHelpButton = true; LeanixGui.this.btnHelp.redraw(); } });
        this.btnHelp.addListener(SWT.MouseExit, new Listener() { @Override public void handleEvent(Event event) { LeanixGui.this.mouseOverHelpButton = false; LeanixGui.this.btnHelp.redraw(); } });
        this.btnHelp.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent event)
            {
                if ( LeanixGui.this.mouseOverHelpButton ) event.gc.drawRoundRectangle(0, 0, 29, 29, 10, 10);
                event.gc.drawImage(HELP_ICON, 2, 2);
            }
        });
        this.btnHelp.addListener(SWT.MouseUp, new Listener() { @Override public void handleEvent(Event event) { if ( LeanixGui.this.HELP_HREF != null ) { if ( logger.isDebugEnabled() ) logger.debug("Showing help: /"+LeanixPlugin.PLUGIN_ID+"/help/html/"+LeanixGui.this.HELP_HREF); PlatformUI.getWorkbench().getHelpSystem().displayHelpResource("/"+LeanixPlugin.PLUGIN_ID+"/help/html/"+LeanixGui.this.HELP_HREF); } } });
        fd = new FormData(30,30);
        fd.top = new FormAttachment(0, 5);
        fd.left = new FormAttachment(0, 5);
        this.btnHelp.setLayoutData(fd);


        this.btnClose = new Button(this.compoBottom, SWT.NONE);
        this.btnClose.setText("Close");
        this.btnClose.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                close();
                event.doit = true;
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent event) { widgetSelected(event); }
        });
        fd = new FormData(100,25);
        fd.top = new FormAttachment(0, 8);
        fd.right = new FormAttachment(100, -10);
        this.btnClose.setLayoutData(fd);

        this.btnDoAction = new Button(this.compoBottom, SWT.NONE);
        this.btnDoAction.setEnabled(false);
        this.btnDoAction.setVisible(false);
        fd = new FormData(100,25);
        fd.top = new FormAttachment(0, 8);
        fd.right = new FormAttachment(this.btnClose, -10);
        this.btnDoAction.setLayoutData(fd);

    }

    public void run() {
        this.dialog.open();
        this.dialog.layout();
        refreshDisplay();
    }
    
    /**
     * Gets the list of configured graphqls, fills-in the comboGraphqls and selects the first graphql
     * @throws Exception 
     */
    protected void getGraphqls() {
        refreshDisplay();

        this.leanixGraphqlList = LeanixGraphql.getAllFromPreferenceStore(LeanixPlugin.INSTANCE.getPreferenceStore());
        if ( this.leanixGraphqlList.size() == 0 ) {
            popup(Level.ERROR, "You haven't configure any GraphQL request yet.\n\nPlease setup at least one database in Archi preferences.");
        } else {
            for (LeanixGraphql leanixGraphql: this.leanixGraphqlList)
           		this.comboGraphqls.add(leanixGraphql.getName());
            this.comboGraphqls.setData("LeanixGraphqlList", this.leanixGraphqlList);
            this.comboGraphqls.select(0);
            this.comboGraphqls.notifyListeners(SWT.Selection, new Event());		// calls the graphqlSelected() method
        }
    }
    
    
    /** 
     * Sets the reference of the online help
     */
    protected void setHelpHref(String href) {
        this.HELP_HREF = href;
        this.btnHelp.setVisible(this.HELP_HREF != null);
    }

    static Shell dialogShell = null;
    static Composite dialogComposite = null;
    static Label dialogLabel = null;
    /**
     * shows up an on screen popup displaying the message but does not wait for any user input<br>
     * it is the responsibility of the caller to dismiss the popup 
     */
    public static Shell popup(String msg) {
        logger.info(LeanixGui.class, msg);

        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                if ( dialogShell == null ) {
                    dialogShell = new Shell(display, SWT.APPLICATION_MODAL);
                    dialogShell.setSize(500, 70);
                    dialogShell.setBackground(BLACK_COLOR);
                    dialogShell.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - 500) / 4, (Toolkit.getDefaultToolkit().getScreenSize().height - 70) / 4);

                    int borderWidth = (dialogShell.getBorderWidth()+1)*2;
                    dialogComposite = new Composite(dialogShell, SWT.NONE);
                    dialogComposite.setSize(500-borderWidth, 70-borderWidth);
                    dialogComposite.setLocation(1, 1);
                    dialogComposite.setBackground(COMPO_LEFT_COLOR);
                    dialogComposite.setLayout(new GridLayout( 1, false ) );

                    dialogLabel = new Label(dialogComposite, SWT.CENTER | SWT.WRAP);
                    dialogLabel.setBackground(COMPO_LEFT_COLOR);
                    dialogLabel.setLayoutData( new GridData( SWT.CENTER, SWT.CENTER, true, true ) );
                    dialogLabel.setFont(TITLE_FONT);
                } else {
                    restoreCursors();
                }

                dialogLabel.setText(msg);
                dialogShell.layout(true);
                dialogShell.open();

                dialogComposite.layout();

                setArrowCursor();
            }
        });

        return dialogShell;
    }

    /**
     * dismiss the popup if it is displayed (else, does nothing) 
     */
    public static void closePopup() {
        if ( dialogShell != null ) {
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    dialogShell.close();
                    dialogShell = null;

                    restoreCursors();
                }
            });
        }
    }

    private static Stack<Map<Shell, Cursor>> cursorsStack = new Stack<Map<Shell, Cursor>>();
    public static void setArrowCursor() {
        Map<Shell, Cursor> cursors = new HashMap<Shell, Cursor>();
        for ( Shell shell: display.getShells() ) {
            cursors.put(shell,  shell.getCursor());
            shell.setCursor(CURSOR_WAIT);
        }
        cursorsStack.push(cursors);
        refreshDisplay();
    }

    public static void restoreCursors() {
        Map<Shell, Cursor> cursors = cursorsStack.pop();
        for ( Shell shell: display.getShells() ) {
            Cursor cursor = (cursors==null) ? null : cursors.get(shell);
            shell.setCursor(cursor==null ? CURSOR_ARROW : cursor);
        }
        refreshDisplay();
    }

    /**
     * Shows up an on screen popup displaying the message and wait for the user to click on the "OK" button
     */
    public static void popup(Level level, String msg) {
        popup(level,msg,null);
    }

    /**
     * Shows up an on screen popup, displaying the message (and the exception message if any) and wait for the user to click on the "OK" button<br>
     * The exception stacktrace is also printed on the standard error stream
     */
    public static void popup(Level level, String msg, Exception e) {
        String popupMessage = msg;
        logger.log(LeanixGui.class, level, msg, e);

        Throwable cause = e;
        while ( cause != null ) {
            if ( cause.getMessage() != null ) {
                if ( !popupMessage.endsWith(cause.getMessage()) )
                    popupMessage += "\n\n" + cause.getClass().getSimpleName() + ": " + cause.getMessage();
            } else 
                popupMessage += "\n\n" + cause.getClass().getSimpleName();
            cause = cause.getCause();
        }

        switch ( level.toInt() ) {
            case Priority.FATAL_INT:
            case Priority.ERROR_INT:
                MessageDialog.openError(display.getActiveShell(), LeanixPlugin.pluginTitle, popupMessage);
                break;
            case Priority.WARN_INT:
                MessageDialog.openWarning(display.getActiveShell(), LeanixPlugin.pluginTitle, popupMessage);
                break;
            default:
                MessageDialog.openInformation(display.getActiveShell(), LeanixPlugin.pluginTitle, popupMessage);
                break;
        }

        refreshDisplay();
    }

    static int questionResult;

    /**
     * Shows up an on screen popup displaying the question (and the exception message if any)  and wait for the user to click on the "YES" or "NO" button<br>
     * The exception stacktrace is also printed on the standard error stream
     */
    public static boolean question(String msg) {
        return question(msg, new String[] {"Yes", "No"}) == 0;
    }

    /**
     * Shows up an on screen popup displaying the question (and the exception message if any)  and wait for the user to click on the "YES" or "NO" button<br>
     * The exception stacktrace is also printed on the standard error stream
     */
    public static int question(String msg, String[] buttonLabels) {
        if ( logger.isDebugEnabled() ) logger.debug(LeanixGui.class, "Question: "+msg);

        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                //questionResult = MessageDialog.openQuestion(display.getActiveShell(), LeanixPlugin.pluginTitle, msg);
                Shell shell = new Shell(display, SWT.SHELL_TRIM);
                shell.setSize(0, 0);
                shell.setBackground(BLACK_COLOR);
                shell.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - shell.getSize().x) / 4, (Toolkit.getDefaultToolkit().getScreenSize().height - shell.getSize().y) / 4);
                MessageDialog messageDialog = new MessageDialog(shell, LeanixPlugin.pluginTitle, null, msg, MessageDialog.QUESTION, buttonLabels, 0);
                questionResult = messageDialog.open();
            }
        });

        if ( logger.isDebugEnabled() ) logger.debug(LeanixGui.class, "Answer: "+buttonLabels[questionResult]);
        return questionResult;
    }
    
    static String answeredPassword;
    
    private SelectionListener actionListener = null;
    protected void setBtnAction(String label, SelectionListener listener) {
    	if ( label == null ) {
    		this.btnDoAction.setVisible(false);
    	} else {
	        this.btnDoAction.setText(label);
	        this.btnDoAction.setVisible(true);
	
	        if ( this.actionListener != null ) {
	            this.btnDoAction.removeSelectionListener(this.actionListener);
	            this.actionListener = null;
	        }
	
	        if ( listener != null ) {
	            this.actionListener = listener;
	            this.btnDoAction.addSelectionListener(this.actionListener);
	        }
    	}
    }

    /**
     * Creates the progress bar that will allow to follow the export process
     */
    protected void createProgressBar(String label, int min, int max) {
        if ( this.grpProgressBar == null ) {
            this.grpProgressBar = new Group(this.compoRightTop, SWT.NONE);
            this.grpProgressBar.setBackground(GROUP_BACKGROUND_COLOR);
            FormData fd = new FormData();
            fd.top = new FormAttachment(0);
            fd.left = new FormAttachment(0);
            fd.right = new FormAttachment(100);
            fd.bottom = new FormAttachment(100);
            this.grpProgressBar.setLayoutData(fd);
            this.grpProgressBar.setLayout(new FormLayout());


            this.lblProgressBar = new Label(this.grpProgressBar, SWT.CENTER);
            this.lblProgressBar.setBackground(GROUP_BACKGROUND_COLOR);
            this.lblProgressBar.setFont(TITLE_FONT);
            fd = new FormData();
            fd.top = new FormAttachment(0, -5);
            fd.left = new FormAttachment(0);
            fd.right = new FormAttachment(100);
            this.lblProgressBar.setLayoutData(fd);

            this.progressBar = new ProgressBar(this.grpProgressBar, SWT.NONE);
            fd = new FormData();
            fd.top = new FormAttachment(this.lblProgressBar);
            fd.left = new FormAttachment(25);
            fd.right = new FormAttachment(75);
            fd.height = 15;
            this.progressBar.setLayoutData(fd);
        }

        this.grpProgressBar.setVisible(true);
        this.grpProgressBar.setData("visible", true);

        this.grpProgressBar.moveAbove(null);

        this.lblProgressBar.setText(label);
        logger.info(LeanixGui.class, label);

        this.progressBar.setMinimum(min);
        this.progressBar.setMaximum(max);


        this.compoRightTop.layout();
        refreshDisplay();

        resetProgressBar();
    }

    public void hideProgressBar() {
        if ( this.progressBar != null ) {
            this.grpProgressBar.setVisible(false);
            this.grpProgressBar.setData("visible", false);
            refreshDisplay();
        }
    }

    public void setProgressBarLabel(String label) {
        if ( this.lblProgressBar == null )
            createProgressBar(label, 0, 100);
        else {
            this.lblProgressBar.setText(label);
            logger.info(LeanixGui.class, label);
        }
        refreshDisplay();
    }

    public String getProgressBarLabel() {
        if ( this.lblProgressBar == null )
            return "";

        return this.lblProgressBar.getText();
    }

    /**
     * Sets the min and max values of the progressBar and reset its selection to zero
     */
    public void setProgressBarMinAndMax(int min, int max) {
        if ( this.lblProgressBar != null ) {
            this.progressBar.setMinimum(min);
            this.progressBar.setMaximum(max);
        }
        resetProgressBar();
    }

    /**
     * Resets the progressBar to zero in the SWT thread (thread safe method)
     */
    public void resetProgressBar() {
        if ( this.lblProgressBar != null )
            this.progressBar.setSelection(0);
        refreshDisplay();
    }

    /**
     * Increases the progressBar selection in the SWT thread (thread safe method)
     */
    public void increaseProgressBar() {
        if ( this.lblProgressBar != null )
            this.progressBar.setSelection(this.progressBar.getSelection()+1);
        refreshDisplay();
    }

    public void setMessage(String message) {
        setMessage(message, GROUP_BACKGROUND_COLOR);
    }

    protected void setMessage(String message, Color background) {
        if ( this.grpMessage == null ) {
            this.grpMessage = new Group(this.compoRightTop, SWT.NONE);
            this.grpMessage.setBackground(GROUP_BACKGROUND_COLOR);
            this.grpMessage.setFont(GROUP_TITLE_FONT);
            this.grpMessage.setText("Please wait ... ");
            FormData fd = new FormData();
            fd.top = new FormAttachment(0);
            fd.left = new FormAttachment(0);
            fd.right = new FormAttachment(100);
            fd.bottom = new FormAttachment(100);
            this.grpMessage.setLayoutData(fd);
            this.grpMessage.setLayout(new FormLayout());

            this.lblMessage = new CLabel(this.grpMessage, SWT.CENTER);
            this.lblMessage.setAlignment(SWT.CENTER); 
            this.lblMessage.setBackground(GROUP_BACKGROUND_COLOR);
            this.lblMessage.setFont(TITLE_FONT);
            fd = new FormData();
            fd.top = new FormAttachment(0);
            fd.left = new FormAttachment(0);
            fd.right = new FormAttachment(100);
            fd.bottom = new FormAttachment(100);
            this.lblMessage.setLayoutData(fd);
            refreshDisplay();
        }

        this.grpMessage.setVisible(true);

        if (this.grpProgressBar != null )
            this.grpProgressBar.setVisible(false);

        if ( this.grpGraphqls != null )
            this.grpGraphqls.setVisible(false);

        this.compoRightTop.layout();

        this.lblMessage.setBackground(background);

        String msg = message.replace("\n\n", "\n");
        if ( background == RED_COLOR )
            logger.error(LeanixGui.class, msg);
        else
            logger.info(LeanixGui.class, msg);

        this.lblMessage.setText(msg);
        
        this.grpMessage.moveAbove(null);

        refreshDisplay();
    }


    public void closeMessage() {
        if ( (this.grpMessage != null) && !this.grpMessage.isDisposed() ) {
            this.grpMessage.setVisible(false);

            if (this.grpProgressBar != null && (this.grpProgressBar.getData("visible") != null) )
                this.grpProgressBar.setVisible((boolean)this.grpProgressBar.getData("visible"));

            if ( this.grpGraphqls != null && (this.grpGraphqls.getData("visible") != null) )
                this.grpGraphqls.setVisible((boolean)this.grpGraphqls.getData("visible"));

            this.compoRightTop.layout();
            refreshDisplay();
        }
    }

    /**
     * Method used to close graphical objects if needed
     */
    public void close() {
        this.dialog.dispose();
        this.dialog = null;

        restoreCursors();
    }

    /**
     * @return true if the dialog is disposed
     */
    public boolean isDisposed() {
        return this.dialog==null ? true : this.dialog.isDisposed();
    }

     /**
     * Refreshes the display
     */
    public static void refreshDisplay() {
        while ( LeanixGui.display.readAndDispatch() ) {
            // nothing to do
        }
    }
    
    /**
     * Listener called when a graphql is selected in the combo<br>
     * Must be overridden
     */
    protected abstract void graphqlSelected();
    
    /**
     * Called when the user clicks on the "set preferences" button<br>
     * This method opens up the leanix plugin preference page that the user can configure preferences.
     * @throws Exception 
     */
    protected void setPreferences() throws Exception {
        if ( logger.isDebugEnabled() ) logger.debug("Openning preference page ...");
        PreferenceDialog prefDialog = PreferencesUtil.createPreferenceDialogOn(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "org.archicontribs.leanix.DBPreferencePage", null, null);
        prefDialog.setBlockOnOpen(true);
        if ( prefDialog.open() == 0 ) {
            if ( logger.isDebugEnabled() ) logger.debug("Resetting settings from preferences ...");

            this.comboGraphqls.removeAll();

            //TODO
        } else {
            if ( logger.isDebugEnabled() ) logger.debug("Preferences cancelled ...");
            if ( this.comboGraphqls.getItemCount() == 0 )
                popup(Level.ERROR, "You won't be able to import until a GraphQL request is configured in the preferences.");
        }
        this.comboGraphqls.setFocus();
    }
}