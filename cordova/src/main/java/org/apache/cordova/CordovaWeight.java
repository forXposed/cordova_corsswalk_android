package org.apache.cordova;

import java.util.ArrayList;
import java.util.Locale;

import org.apache.cordova.Config;
import org.apache.cordova.ConfigXmlParser;
import org.apache.cordova.CordovaInterfaceImpl;
import org.apache.cordova.CordovaPreferences;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewEngine;
import org.apache.cordova.CordovaWebViewImpl;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginEntry;
import org.apache.cordova.PluginManager;
import org.apache.cordova.engine.SystemWebView;
import org.json.JSONException;
import org.json.JSONObject;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebViewClient;

public class CordovaWeight {
	SystemWebView webView;
	public static String TAG = "CordovaActivity";
	
	protected CordovaWebView appView;
	protected boolean keepRunning = true;
	// Read from config.xml:
	protected CordovaPreferences preferences;
	protected String launchUrl;
	protected ArrayList<PluginEntry> pluginEntries;
	protected CordovaInterfaceImpl cordovaInterface;
	Activity activity;
	public CordovaWeight(Activity activity) {
		super();
		this.activity = activity;
	}
	public CordovaWeight() {
	}
 
	public void setWebView(SystemWebView webView,Bundle savedInstanceState){
		if(webView==null) return ;
		loadConfig();
		makeCordovaInterface();
        if(savedInstanceState != null)
        {
        	getCordovaInterface().restoreInstanceState(savedInstanceState);
        }
		setView(webView);
	 }
	
	 public void init() {
	        appView = makeWebView();
	        appView.setView(webView);
	        createViews();
	        if (!appView.isInitialized()) {
	            appView.init(cordovaInterface, pluginEntries, preferences);
	        }
	        cordovaInterface.onCordovaInit(appView.getPluginManager());

	        // Wire the hardware volume controls to control media if desired.
	        String volumePref = preferences.getString("DefaultVolumeStream", "");
	        if ("media".equals(volumePref.toLowerCase(Locale.ENGLISH))) {
	        	activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
	        }
	    }

	    @SuppressWarnings("deprecation")
	    public void loadConfig() {
	        ConfigXmlParser parser = new ConfigXmlParser();
	        parser.parse(activity);
	        preferences = parser.getPreferences();
	        preferences.setPreferencesBundle(activity.getIntent().getExtras());
	        preferences.copyIntoIntentExtras(activity);
	        launchUrl = parser.getLaunchUrl();
	        pluginEntries = parser.getPluginEntries();
	        Config.parser = parser;
	    }

	    //Suppressing warnings in AndroidStudio
	    @SuppressWarnings({"deprecation", "ResourceType"})
	    public void createViews() {
	        //Why are we setting a constant as the ID? This should be investigated
	      /*  appView.getView().setId(100);
	        appView.getView().setLayoutParams(new FrameLayout.LayoutParams(
	                ViewGroup.LayoutParams.MATCH_PARENT,
	                ViewGroup.LayoutParams.MATCH_PARENT));

	        setContentView(appView.getView());*/
	    	

	        if (preferences.contains("BackgroundColor")) {
	            int backgroundColor = preferences.getInteger("BackgroundColor", Color.BLACK);
	            // Background of activity:
	            appView.getView().setBackgroundColor(backgroundColor);
	        }

	        appView.getView().requestFocusFromTouch();
	    }

	    /**
	     * Construct the default web view object.
	     *
	     * Override this to customize the webview that is used.
	     */
	    public CordovaWebView makeWebView() {
	        return new CordovaWebViewImpl(makeWebViewEngine());
	    }

	    public CordovaWebViewEngine makeWebViewEngine() {
	        return CordovaWebViewImpl.createEngine(activity, preferences);
	    }

	    public void makeCordovaInterface() {
	        this.cordovaInterface = new CordovaInterfaceImpl(activity) {
	            @Override
	            public Object onMessage(String id, Object data) {
	                // Plumb this to CordovaActivity.onMessage for backwards compatibility
	                return CordovaWeight.this.onMessage(id, data);
	            }
	        };
	    }

	    /**
	     * Load the url into the webview.
	     */
	    public void loadUrl(String url) {
	    	if(webView==null){
	    		Log.e("CordovaWeight", "webViewΪnull");
	    		return ;
	    	} 
	        if (appView == null) {
	            init();
	        }

	        // If keepRunning
	        this.keepRunning = preferences.getBoolean("KeepRunning", true);

	        appView.loadUrlIntoView(url, true);
	    }

	    /**
	     * Called when the system is about to start resuming a previous activity.
	     */
	    
	    public void onPause() {
	        LOG.d(TAG, "Paused the activity.");

	        if (this.appView != null) {
	            this.appView.handlePause(this.keepRunning);
	        }
	    }

	    /**
	     * Called when the activity receives a new intent
	     **/
	    
	    public void onNewIntent(Intent intent) {
	        //Forward to plugins
	        if (this.appView != null)
	           this.appView.onNewIntent(intent);
	    }

	    /**
	     * Called when the activity will start interacting with the user.
	     */
	    
	    public void onResume() {
	        LOG.d(TAG, "Resumed the activity.");
	        
	        if (this.appView == null) {
	            return;
	        }
	        // Force window to have focus, so application always
	        // receive user input. Workaround for some devices (Samsung Galaxy Note 3 at least)
	        activity.getWindow().getDecorView().requestFocus();

	        this.appView.handleResume(this.keepRunning);
	    }

	    /**
	     * Called when the activity is no longer visible to the user.
	     */
	    
	    public void onStop() {
	        LOG.d(TAG, "Stopped the activity.");

	        if (this.appView == null) {
	            return;
	        }
	        this.appView.handleStop();
	    }

	    /**
	     * Called when the activity is becoming visible to the user.
	     */
	    
	    public void onStart() {
	        LOG.d(TAG, "Started the activity.");

	        if (this.appView == null) {
	            return;
	        }
	        this.appView.handleStart();
	    }

	    /**
	     * The final call you receive before your activity is destroyed.
	     */
	    
	    public void onDestroy() {
	        LOG.d(TAG, "CordovaActivity.onDestroy()");

	        if (this.appView != null) {
	            appView.handleDestroy();
	        }
	    }

	    
	    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
	        // Capture requestCode here so that it is captured in the setActivityResultCallback() case.
	        cordovaInterface.setActivityResultRequestCode(requestCode);
	    }

	    /**
	     * Called when an activity you launched exits, giving you the requestCode you started it with,
	     * the resultCode it returned, and any additional data from it.
	     *
	     * @param requestCode       The request code originally supplied to startActivityForResult(),
	     *                          allowing you to identify who this result came from.
	     * @param resultCode        The integer result code returned by the child activity through its setResult().
	     * @param intent            An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
	     */
	    
	    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	        LOG.d(TAG, "Incoming Result. Request code = " + requestCode);
	        cordovaInterface.onActivityResult(requestCode, resultCode, intent);
	    }

	    /**
	     * Report an error to the host application. These errors are unrecoverable (i.e. the main resource is unavailable).
	     * The errorCode parameter corresponds to one of the ERROR_* constants.
	     *
	     * @param errorCode    The error code corresponding to an ERROR_* value.
	     * @param description  A String describing the error.
	     * @param failingUrl   The url that failed to load.
	     */
	    public void onReceivedError(final int errorCode, final String description, final String failingUrl) {

	        // If errorUrl specified, then load it
	        final String errorUrl = preferences.getString("errorUrl", null);
	        if ((errorUrl != null) && (!failingUrl.equals(errorUrl)) && (appView != null)) {
	            // Load URL on UI thread
	        	activity.runOnUiThread(new Runnable() {
	                public void run() {
	                    appView.showWebPage(errorUrl, false, true, null);
	                }
	            });
	        }
	        // If not, then display error dialog
	        else {
	            final boolean exit = !(errorCode == WebViewClient.ERROR_HOST_LOOKUP);
	            activity.runOnUiThread(new Runnable() {
	                public void run() {
	                    if (exit) {
	                    	appView.getView().setVisibility(View.GONE);
	                    	displayError("Application Error", description + " (" + failingUrl + ")", "OK", exit);
	                    }
	                }
	            });
	        }
	    }

	    /**
	     * Display an error dialog and optionally exit application.
	     */
	    public void displayError(final String title, final String message, final String button, final boolean exit) {
	        activity.runOnUiThread(new Runnable() {
	            public void run() {
	                try {
	                    AlertDialog.Builder dlg = new AlertDialog.Builder(activity);
	                    dlg.setMessage(message);
	                    dlg.setTitle(title);
	                    dlg.setCancelable(false);
	                    dlg.setPositiveButton(button,
	                            new AlertDialog.OnClickListener() {
	                                public void onClick(DialogInterface dialog, int which) {
	                                    dialog.dismiss();
	                                    if (exit) {
	                                        activity.finish();
	                                    }
	                                }
	                            });
	                    dlg.create();
	                    dlg.show();
	                } catch (Exception e) {
	                    activity.finish();
	                }
	            }
	        });
	    }

	    /*
	     * Hook in Cordova for menu plugins
	     */
	    
	    public void onCreateOptionsMenu(Menu menu) {
	        if (appView != null) {
	            appView.getPluginManager().postMessage("onCreateOptionsMenu", menu);
	        }
	       // return super.onCreateOptionsMenu(menu);
	    }

	    
	    public boolean onPrepareOptionsMenu(Menu menu ) {
	        if (appView != null) {
	            appView.getPluginManager().postMessage("onPrepareOptionsMenu", menu);
	        }
	        return true;
	    }

	    
	    public boolean onOptionsItemSelected(MenuItem item) {
	        if (appView != null) {
	            appView.getPluginManager().postMessage("onOptionsItemSelected", item);
	        }
	        return true;
	    }

	    /**
	     * Called when a message is sent to plugin.
	     *
	     * @param id            The message id
	     * @param data          The message data
	     * @return              Object or null
	     */
	    public Object onMessage(String id, Object data) {
	        if ("onReceivedError".equals(id)) {
	            JSONObject d = (JSONObject) data;
	            try {
	                this.onReceivedError(d.getInt("errorCode"), d.getString("description"), d.getString("url"));
	            } catch (JSONException e) {
	                e.printStackTrace();
	            }
	        } else if ("exit".equals(id)) {
	            activity.finish();
	        }
	        return null;
	    }

	    public void onSaveInstanceState(Bundle outState)
	    {
	        cordovaInterface.onSaveInstanceState(outState);
	      //  super.onSaveInstanceState(outState);
	    }

	    /**
	     * Called by the system when the device configuration changes while your activity is running.
	     *
	     * @param newConfig		The new device configuration
	     */
	    
	    public void onConfigurationChanged(Configuration newConfig) {
	       // super.onConfigurationChanged(newConfig);
	        if (this.appView == null) {
	            return;
	        }
	        PluginManager pm = this.appView.getPluginManager();
	        if (pm != null) {
	            pm.onConfigurationChanged(newConfig);
	        }
	    }


		public SystemWebView getWebView() {
			return webView;
		}


		public void setView(SystemWebView webView) {
			this.webView = webView;
		}


		public Activity getActivity() {
			return activity;
		}


		public void setActivity(Activity activity) {
			this.activity = activity;
		}
		public CordovaInterfaceImpl getCordovaInterface() {
			return cordovaInterface;
		}
		public void setCordovaInterface(CordovaInterfaceImpl cordovaInterface) {
			this.cordovaInterface = cordovaInterface;
		}
		public String getLaunchUrl() {
			return launchUrl;
		}
		public void setLaunchUrl(String launchUrl) {
			this.launchUrl = launchUrl;
		}
	

}
