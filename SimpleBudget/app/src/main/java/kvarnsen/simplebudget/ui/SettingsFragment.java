package kvarnsen.simplebudget.ui;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import kvarnsen.simplebudget.R;

/**
 * Created by joshuapancho on 3/01/15.
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

}