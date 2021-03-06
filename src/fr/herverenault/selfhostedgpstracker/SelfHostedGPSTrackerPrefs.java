package fr.herverenault.selfhostedgpstracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class SelfHostedGPSTrackerPrefs extends PreferenceActivity {
	
	static Context context;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
		context = getApplicationContext();
	}
	
	public static class MyPreferenceFragment extends PreferenceFragment{
		@Override
		public void onCreate(final Bundle savedInstanceState){
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);

			Preference pref;
			
			pref = findPreference("pref_gps_updates");
			pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
					int oldValue = Integer.parseInt(preferences.getString("pref_gps_updates", "0"));
					if (newValue == null 
							|| newValue.toString().length() == 0 
							|| Integer.parseInt(newValue.toString()) < 30) { // user has been warned
				        Toast.makeText(context, getString(R.string.pref_gps_updates_too_low), Toast.LENGTH_SHORT).show();
				        return false;
					} else if (SelfHostedGPSTrackerService.isRunning
							&& Integer.parseInt(newValue.toString()) != oldValue) {
						Toast.makeText(context, getString(R.string.toast_prefs_restart), Toast.LENGTH_LONG).show();
					}
					return true;
				}
			});
			
			pref = findPreference("pref_max_run_time");
			pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) { // hours
					SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
					int prefGpsUpdates = Integer.parseInt(preferences.getString("pref_gps_updates", "0")); // seconds
					int oldValue = Integer.parseInt(preferences.getString("pref_max_run_time", "0"));
					if (newValue == null 
							|| newValue.toString().length() == 0 
							|| (Integer.parseInt(newValue.toString()) * 3600) < prefGpsUpdates) { // would not make sense...
				        Toast.makeText(context, getString(R.string.pref_max_run_time_too_low), Toast.LENGTH_LONG).show();
				        return false;
					} else if (SelfHostedGPSTrackerService.isRunning
							&& Integer.parseInt(newValue.toString()) != oldValue) {
						Toast.makeText(context, getString(R.string.toast_prefs_restart), Toast.LENGTH_SHORT).show();
					}
					return true;
				}
			});
		}
	}
}
