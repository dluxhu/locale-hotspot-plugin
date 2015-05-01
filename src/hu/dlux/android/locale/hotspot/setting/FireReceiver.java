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

import hu.dlux.android.locale.hotspot.LowLevelHotspotApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This is the "fire" {@code BroadcastReceiver} for a <i>Locale</i> plug-in setting.
 */
public final class FireReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(final Context context, final Intent intent)
	{
		if (com.twofortyfouram.locale.Intent.ACTION_FIRE_SETTING.equals(intent.getAction())) {
			boolean on = StateBundle.from(intent).isOn();
			// Toast.makeText(context, "Turning Wifi Hotspot " + (on ? "ON" : "OFF"), 3).show();
			new LowLevelHotspotApi(context).changeHotspotState(on);
		}
	}
}