/**
 * This class opens up the import from leanix window 
 * 
 * @author Herve Jouin
 **/
package org.archicontribs.leanix.GUI;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Level;
import org.archicontribs.leanix.LeanixPlugin;
import org.archicontribs.leanix.graphql.LeanixGraphqlVariable;
import org.archicontribs.leanix.preferences.LeanixGraphql;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.archimatetool.model.IArchimateModel;

public class LeanixGuiImportFromLeanix extends LeanixGui {
	IPersistentPreferenceStore preferenceStore = LeanixPlugin.INSTANCE.getPreferenceStore(); 

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
		String authHost = preferenceStore.getString("leanixHost");
		String apiToken = preferenceStore.getString("leanixApiToken");

		String authUrl = "https://"+authHost+"/services/mtm/v1/oauth2/token";

		logger.debug ("Authenticating to \"" + authUrl + "\" with apiToken \""+apiToken+"\"");

		URL url;
		try {
			url = new URL(authUrl);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}

		HttpsURLConnection httpsConnection;
		try {
			httpsConnection = (HttpsURLConnection)url.openConnection();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}

		httpsConnection.setDoOutput(true);
		httpsConnection.setDoInput(true);
		try {
			httpsConnection.setRequestMethod("POST");
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			httpsConnection.disconnect();
			return;
		}
		
		
		int responseCode;
		try {
			String content = "grant_type=client_credentials";
			String encoded = Base64.getEncoder().encodeToString(("apitoken:"+apiToken).getBytes(StandardCharsets.UTF_8));
			httpsConnection.setRequestProperty("Accept", "application/json");
			httpsConnection.setRequestProperty("Authorization", "Basic " + encoded);
			httpsConnection.setRequestProperty("Content-Length", Integer.toString(content.length()));
			
			httpsConnection.getOutputStream().write(content.getBytes("UTF8"));
			
			httpsConnection.connect();
			
			logger.debug("GET Response Code: " + httpsConnection.getResponseCode());
			logger.debug("GET Response Mesg: " );
			try(BufferedReader br = new BufferedReader(new InputStreamReader(httpsConnection.getInputStream(), "utf-8"))) {
				StringBuilder response = new StringBuilder();
				String responseLine = null;
				while ((responseLine = br.readLine()) != null) {
					response.append(responseLine.trim());
				}
				logger.debug(response.toString());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			httpsConnection.disconnect();
			return;
		}


		




			/*
		response = requests.post(authUrl, auth=('apitoken', self._apiToken), data={'grant_type': 'client_credentials'}, proxies=self._proxies)
		        response.raise_for_status() 
		        self._access_token = response.json()['access_token']

		        if self._access_token is not None:
		            self.logger.debug ("successful")
		            self.authorization = {'Authorization': 'Bearer ' + self._access_token, "Content-Type":"application/json"}
		            self.authenticationSuccessful = True
		        else:
		            self.logger.error ("Authorization failed !")
		            self.authorization = ""
		            self.authenticationSuccessful = False

		graphQlUrl = self._url+"services/pathfinder/v1/graphql"
				query = query.replace("\n"," ").replace("\t"," ").replace("  "," ")
		        req = {"query":query}
		        req['variables'] = variables

		        self.logger.debug (f"Sending query to {graphQlUrl}: {' '.join(query.split())}")

		        requests.headers = { 'content-encoding': 'application/json', 'Accept-Language': 'en-US' }
		        response = requests.post(graphQlUrl, headers=self.authorization, data=json.dumps(req), proxies=self._proxies)


			 */


			//todo : get result and create Archimate objects
		}

		@Override protected void graphqlSelected() {
			// TODO Auto-generated method stub
			int index = comboGraphqls.getSelectionIndex();
			@SuppressWarnings("unchecked")
			LeanixGraphql leanixGraphql = ((ArrayList<LeanixGraphql>)comboGraphqls.getData("LeanixGraphqlList")).get(index);

			logger.debug("GraphQL selected: ");
			logger.debug("     name: "+leanixGraphql.getName());
			logger.debug("     desc: "+leanixGraphql.getDescription());

			disposeVariableWidgets();
			createVariableWidgets(leanixGraphql.getVariables());

			this.btnDoAction.setEnabled(true);
		}

		private void disposeVariableWidgets() {
			logger.trace("disposing old variable widgets");

			@SuppressWarnings("unchecked")
			ArrayList<Label> labelList = (ArrayList<Label>) this.compoRightBottom.getData("labelList");
			if ( labelList != null )
				for ( Label label: labelList ) {
					logger.trace("     disposing label \""+label.getText()+"\"");
					label.dispose();
				}
			this.compoRightBottom.setData("labelList", null);

			@SuppressWarnings("unchecked")
			ArrayList<Text> textList = (ArrayList<Text>) this.compoRightBottom.getData("textList");
			if ( textList != null )
				for ( Text text: textList )
					text.dispose();
			this.compoRightBottom.setData("textList", null);

		}

		private void createVariableWidgets(ArrayList<LeanixGraphqlVariable> variables) {
			logger.trace("creating new variable widgets");

			@SuppressWarnings("unchecked")
			ArrayList<Label> labelList = (ArrayList<Label>) this.compoRightBottom.getData("labelList");
			if ( labelList == null )
				labelList = new ArrayList<Label>();


			@SuppressWarnings("unchecked")
			ArrayList<Text> textList = (ArrayList<Text>) this.compoRightBottom.getData("textList");
			if ( textList == null )
				textList = new ArrayList<Text>();

			Label previousLabel = null;
			for ( LeanixGraphqlVariable variable: variables ) {
				logger.trace("     creating label \"" + variable.getLabel() + "\"");

				Label label = new Label(this.grpVariables, SWT.NONE);
				label.setBackground(GROUP_BACKGROUND_COLOR);
				label.setText(variable.getLabel());
				labelList.add(label);

				Text text = new Text(this.grpVariables, SWT.BORDER);
				text.setToolTipText(variable.getVariable());
				text.setText(variable.getDefaultValue());
				text.setData("variable", variable);
				text.addPaintListener(new PaintListener() {
					@SuppressWarnings("unchecked")
					@Override public void paintControl(PaintEvent e) {
						final Color red = new Color(null, 255, 0 ,0);
						try {
							Text source = (Text)e.getSource();
							if ( source.getText().equals("") ) {
								GC gc = e.gc;
								gc.setForeground(red);
								gc.drawRectangle(e.x, e.y, e.width-1, e.height-1);
								// we deactivate the doAction button
								source.setData("isValid", false);
								btnDoAction.setEnabled(false);
							} else {
								source.setData("isValid", true);
								// we check all the text fields and if they are all valid, we activate back the doAction button
								ArrayList<Text> textList = (ArrayList<Text>) compoRightBottom.getData("textList");
								boolean areAllValid = true;
								for ( Text text: textList ) {
									if ( !(Boolean)(text.getData("isValid")) ) {
										areAllValid = false;
										break;
									}
								}
								btnDoAction.setEnabled(areAllValid);
							}
						} catch (Exception ign) {
							// just in case
						}
					}
				});
				text.addModifyListener(new ModifyListener() {

					@Override
					public void modifyText(ModifyEvent e) {
						((Text)e.getSource()).redraw();		// call paint listener
					}
				});
				textList.add(text);

				//todo : manage type : String, Numerical, list of value, ...
				//todo : manage conditions (not null, greater than, less than, positive, ...)

				FormData fd = new FormData();
				if ( previousLabel == null )
					fd.top = new FormAttachment(0, getDefaultMargin());
				else
					fd.top = new FormAttachment(previousLabel, getDefaultMargin());
				fd.left = new FormAttachment(0, getDefaultMargin());
				label.setLayoutData(fd);

				fd = new FormData();
				fd.top = new FormAttachment(label, 0, SWT.CENTER);
				fd.left = new FormAttachment(label, getDefaultMargin());
				fd.right = new FormAttachment(100, -getDefaultMargin());
				text.setLayoutData(fd);

				previousLabel = label;
			}

			this.grpVariables.layout();

			this.compoRightBottom.setData("labelList", labelList);
			this.compoRightBottom.setData("textList", textList);

		}
	}
