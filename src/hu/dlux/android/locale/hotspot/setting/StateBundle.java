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
package hu.dlux.android.locale.hotspot.setting;

import android.content.Intent;
import android.os.Bundle;

/**
 * Bundle object to store the hotspot setting state. This is automatically passed by Locale.
 *  
 * @author dlux
 */
public class StateBundle {
	private static final String KEY = "on";
	private boolean on;

	public StateBundle() {};

	public static StateBundle from(Intent intent) {
		final StateBundle retval = new StateBundle();
		final Bundle forwardedBundle = intent.getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
		if (forwardedBundle == null) {
			return retval;
		}
		retval.on = forwardedBundle.getBoolean(KEY);
		return retval;
	}
	
	public Bundle build() {
		final Bundle bundle = new Bundle();
		bundle.putBoolean(KEY, on);
		return bundle;
	}
	
	public boolean isOn() {
		return on;
	}
	
	public StateBundle setOn(boolean on) {
		this.on = on;
		return this;
	}
}