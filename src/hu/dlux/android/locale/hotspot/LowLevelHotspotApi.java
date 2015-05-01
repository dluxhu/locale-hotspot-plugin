/*
 * Copyright 2011 Balazs Szabo (dLux) <http://www.dlux.hu>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hu.dlux.android.locale.hotspot;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

// This code is based on the Android source code from here:
// http://www.google.com/codesearch/p?hl=en#ohAXAHj6Njg/src/com/android/settings/wifi/WifiApEnabler.java&q=Settings%20WifiApEnabler&l=110

public class LowLevelHotspotApi {
	private final Context context;

	public LowLevelHotspotApi(Context context) {
		this.context = context;
	}

	public void changeHotspotState(boolean enable) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		// Turn off Wifi if the hotspot is to be enabled.
		if (enable) {
			wifiManager.setWifiEnabled(false);
		}
		try {
			wifiManager.getClass()
				.getMethod("setWifiApEnabled",
						   WifiConfiguration.class,
						   boolean.class)
			    .invoke(wifiManager, null, enable);
		} catch (Exception e) {
			Log.e(Constants.TAG, "Cannot call: setWifiApEnabled", e);
		}
	}
}
