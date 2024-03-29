/*
 *  Copyright 2010 Emmanuel Astier & Kevin Gaudin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package acra;

import com.bankicp.app.model.URLs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;


/** 
* @ClassName: CrashReport 
* @Description: 发送奔溃报告的处理程序对象
Application可以继承这个类或创建这个类的对象（单例）
 如果有些崩溃报告无法发送（由于网络连接等问题），其数据会被存储在扩展存储路径（一般是SD卡或内置ROM）根目录下/0CRASH/Log/的目录下。
可以在应用下次启动时发送
如果想尽快收到报告，可以在Activity中调用checkReportsOnApplicationStart
* @author 广东省电信工程有限公司信息技术研发中心 
* @date 2014-4-23 上午11:28:51 
*  
*/
public class CrashReport implements OnSharedPreferenceChangeListener {
	protected static final String LOG_TAG = "ACRA";

	/**
	 * Bundle key for the icon in the status bar notification.
	 * 
	 * @see #getCrashResources()
	 */
	public static final String RES_NOTIF_ICON = "RES_NOTIF_ICON";
	/**
	 * Bundle key for the ticker text in the status bar notification.
	 * 
	 * @see #getCrashResources()
	 */
	public static final String RES_NOTIF_TICKER_TEXT = "RES_NOTIF_TICKER_TEXT";
	/**
	 * Bundle key for the title in the status bar notification.
	 * 
	 * @see #getCrashResources()
	 */
	public static final String RES_NOTIF_TITLE = "RES_NOTIF_TITLE";
	/**
	 * Bundle key for the text in the status bar notification.
	 * 
	 * @see #getCrashResources()
	 */
	public static final String RES_NOTIF_TEXT = "RES_NOTIF_TEXT";
	/**
	 * Bundle key for the icon in the crash dialog.
	 * 
	 * @see #getCrashResources()
	 */
	public static final String RES_DIALOG_ICON = "RES_DIALOG_ICON";
	/**
	 * Bundle key for the title in the crash dialog.
	 * 
	 * @see #getCrashResources()
	 */
	public static final String RES_DIALOG_TITLE = "RES_DIALOG_TITLE";
	/**
	 * Bundle key for the text in the crash dialog.
	 * 
	 * @see #getCrashResources()
	 */
	public static final String RES_DIALOG_TEXT = "RES_DIALOG_TEXT";
	/**
	 * Bundle key for the user comment input label in the crash dialog. If not
	 * provided, disables the input field.
	 * 
	 * @see #getCrashResources()
	 */
	public static final String RES_DIALOG_COMMENT_PROMPT = "RES_DIALOG_COMMENT_PROMPT";
	/**
	 * Bundle key for the Toast text triggered when the user accepts to send a
	 * report in the crash dialog.
	 * 
	 * @see #getCrashResources()
	 */
	public static final String RES_DIALOG_OK_TOAST = "RES_DIALOG_OK_TOAST";
	/**
	 * Bundle key for the Toast text triggered when the application crashes if
	 * the notification+dialog mode is not used.
	 * 
	 * @see #getCrashResources()
	 */
	public static final String RES_TOAST_TEXT = "RES_TOAST_TEXT";

	/**
	 * This is the identifier (value = 666) use for the status bar notification
	 * issued when crashes occur.
	 */
	public static final int NOTIF_CRASH_ID = 666;

	/**
	 * The key of the application default SharedPreference where you can put a
	 * 'true' Boolean value to disable ACRA.
	 */
	// public static final String PREF_DISABLE_ACRA = "acra.disable";
	//
	// /**
	// * Alternatively, you can use this key if you prefer your users to have
	// the
	// * checkbox ticked to enable crash reports. If both acra.disable and
	// * acra.enable are set, the value of acra.disable takes over the other.
	// */
	// public static final String PREF_ENABLE_ACRA = "acra.enable";
	private Context mApp = null;
	SharedPreferences mSharedPreferences = null;
	private final static String PREFERENCEKEY = "pref_key_report";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Application#onCreate()
	 */
	public void start(Context context) {

		mApp = context;
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		// SharedPreferences prefs = getACRASharedPreferences();
		// prefs.registerOnSharedPreferenceChangeListener(this);
		mSharedPreferences.registerOnSharedPreferenceChangeListener(this);

		// If the application default shared preferences contains true for the
		// key "acra.disable", do not activate ACRA. Also checks the alternative
		// opposite setting "acra.enable" if "acra.disable" is not found.
		boolean ableAcra = false;
		try {
			ableAcra = mSharedPreferences.getBoolean(PREFERENCEKEY, true);
		} catch (Exception e) {
			// In case of a ClassCastException
		}

		if (ableAcra) {
			initAcra();

		} else {
			return;
		}
	}

	/**
	 * Activate ACRA.
	 */
	private void initAcra() {
		// Initialise ErrorReporter with all required data
		ErrorReporter errorReporter = ErrorReporter.getInstance();
		ErrorReporter.setFormUri(getFormUri());
		errorReporter.setReportingInteractionMode(getReportingInteractionMode());
		errorReporter.setCrashResources(getCrashResources());
		
		errorReporter.init(mApp.getApplicationContext());
		errorReporter.checkReportsOnApplicationStart();
	}

	/**
	 * <p>
	 * Override this method to send the crash reports to your own server script.
	 * Your script will have to get HTTP POST request parameters named as
	 * described in {@link ErrorReporter} source code (*_KEY fields values).
	 * </p>
	 * <p>
	 * If you override this method with your own url, your implementation of the
	 * abstract {@link #getFormId()} can be empty as it will not be called by
	 * any other method or object.
	 * </p>
	 * 
	 * @return A String containing the Url of your custom server script.
	 */
	public Uri getFormUri() {
		return Uri.parse(URLs.URL_APP_ERROR);
	}

	/**
	 * Implement this method by returning a String containing the id of a valid
	 * GoogleDocs Form.
	 * 
	 * @return The Id of your GoogleDoc Form generated by importing ACRA's
	 *         spreadsheet template.
	 */
	public String getFormId() {
		return "dEM4SDNGX0tvaDVxSjk0NVM5ZTl4Y3c6MQ";
	}

	/**
	 * Guess the ReportingInteractionMode chosen by the developer by analysing
	 * the content of the Bundle provided by {@link #getCrashResources()}. If it
	 * contains {@link #RES_TOAST_TEXT}, TOAST mode is activated. Otherwise,
	 * NOTIFICATION mode is used if the Bundle contains the minimal set of
	 * resources required. In any other cases, activates the SILENT mode.
	 * 
	 * @return The interaction mode
	 */
	ReportingInteractionMode getReportingInteractionMode() {
		Bundle res = getCrashResources();
		if (res != null && res.getInt(RES_TOAST_TEXT) != 0) {
			// Loger.d(LOG_TAG, "Using TOAST mode.");
			return ReportingInteractionMode.TOAST;
		} else if (res != null && res.getInt(RES_NOTIF_TICKER_TEXT) != 0
				&& res.getInt(RES_NOTIF_TEXT) != 0
				&& res.getInt(RES_NOTIF_TITLE) != 0
				&& res.getInt(RES_DIALOG_TEXT) != 0) {
			// Loger.d(LOG_TAG, "Using NOTIFICATION mode.");
			return ReportingInteractionMode.NOTIFICATION;
		} else {
			// Loger.d(LOG_TAG, "Using SILENT mode.");
			return ReportingInteractionMode.SILENT;
		}
		
//		return ReportingInteractionMode.SILENT;
	}

	/**
	 * Override this method to activate user notifications. Return a Bundle
	 * containing :
	 * <ul>
	 * <li>{@link #RES_TOAST_TEXT} to activate the Toast notification mode</li>
	 * <li>At least {@link #RES_NOTIF_TICKER_TEXT}, {@link #RES_NOTIF_TEXT},
	 * {@link #RES_NOTIF_TITLE} and {@link #RES_DIALOG_TEXT} to activate status
	 * bar notifications + dialog mode. You can additionally set
	 * {@link #RES_DIALOG_COMMENT_PROMPT} to activate an input field for the
	 * user to add a comment. Use {@link #RES_NOTIF_ICON},
	 * {@link #RES_DIALOG_ICON}, {@link #RES_DIALOG_TITLE} or
	 * {@link #RES_DIALOG_OK_TOAST} for further UI tweaks.</li>
	 * </ul>
	 * 
	 * @return A Bundle containing the resource Ids necessary to interact with
	 *         the user.
	 */
	public Bundle getCrashResources() {
		Bundle result = new Bundle();
		result.putInt(RES_NOTIF_ICON, CrashReportConfig.RES_NOTIF_ICON);
		result.putInt(RES_NOTIF_TICKER_TEXT, CrashReportConfig.RES_NOTIF_TICKER_TEXT);
		result.putInt(RES_NOTIF_TITLE, CrashReportConfig.RES_NOTIF_TITLE);
		result.putInt(RES_NOTIF_TEXT, CrashReportConfig.RES_NOTIF_TEXT);
		result.putInt(RES_DIALOG_ICON, CrashReportConfig.RES_DIALOG_ICON);
		result.putInt(RES_DIALOG_TITLE, CrashReportConfig.RES_DIALOG_TITLE);
		result.putInt(RES_DIALOG_TEXT, CrashReportConfig.RES_DIALOG_TEXT);
//		result.putInt(RES_TOAST_TEXT, CrashReportConfig.RES_DIALOG_TEXT);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.SharedPreferences.OnSharedPreferenceChangeListener#
	 * onSharedPreferenceChanged(android.content.SharedPreferences,
	 * java.lang.String)
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (PREFERENCEKEY.equals(key)) {
			Boolean ableAcra = false;
			try {
				ableAcra = mSharedPreferences.getBoolean(PREFERENCEKEY, true);
			} catch (Exception e) {
				// In case of a ClassCastException
			}
			if (ableAcra) {
				initAcra();

			} else {
				ErrorReporter.getInstance().disable();
			}
		}
	}

	/**
	 * Override this method if you need to store "acra.disable" or "acra.enable"
	 * in a different SharedPrefence than the application's default.
	 * 
	 * @return The Shared Preferences where ACRA will check the value of the
	 *         setting which disables/enables it's action.
	 */
	// private SharedPreferences getACRASharedPreferences() {
	// return PreferenceManager.getDefaultSharedPreferences(mApp);
	// }
}
