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

import hu.dlux.android.locale.hotspot.Constants;
import hu.dlux.android.locale.hotspot.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.twofortyfouram.locale.BreadCrumber;
import com.twofortyfouram.locale.SharedResources;

public class EditActivity extends Activity {

	private boolean isCancelled = false;
	
	// Dialog ID-s.
	private static final int DIALOG_LICENSE = 0;
	
	private EULAHandler eulaHandler = new EULAHandler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_setting);

		// Sets the state of the widget from the saved instance state or from
		// the extra provided from Locale. If savedInstanceState is not null, then
		// the state is automatically restored.
		if (savedInstanceState == null) {
			((ToggleButton) findViewById(R.id.toggle)).setChecked(StateBundle.from(getIntent()).isOn());
		}

		// Boilerplate from the Locale example application:
		setTitle(BreadCrumber.generateBreadcrumb(getApplicationContext(),
				getIntent(), getString(R.string.app_name)));

		final Drawable borderDrawable = SharedResources.getDrawableResource(
				getPackageManager(), getCallingPackage(),
				SharedResources.DRAWABLE_LOCALE_BORDER);
		final FrameLayout frame = (FrameLayout) findViewById(R.id.frame); 
		if (borderDrawable == null) {
			frame.setBackgroundColor(Color.WHITE);
		} else {
			frame.setBackgroundDrawable(borderDrawable);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.twofortyfouram_locale_help_save_dontsave, menu);

		// Boilerplate from the Locale example application:
		final PackageManager manager = getPackageManager();

		final MenuItem helpItem = menu.findItem(R.id.twofortyfouram_locale_menu_help);
		final CharSequence helpString = SharedResources.getTextResource(manager, getCallingPackage(),
				SharedResources.STRING_MENU_HELP);
		final Drawable helpIcon = SharedResources.getDrawableResource(manager, getCallingPackage(),
				SharedResources.DRAWABLE_MENU_HELP);
		if (helpString != null) {
			helpItem.setTitle(helpString);
		}
		if (helpIcon != null) {
			helpItem.setIcon(helpIcon);
		}

		final MenuItem dontSaveItem = menu.findItem(R.id.twofortyfouram_locale_menu_dontsave);
		final CharSequence dontSaveTitle = SharedResources.getTextResource(manager, getCallingPackage(),
				SharedResources.STRING_MENU_DONTSAVE);
		final Drawable dontSaveIcon = SharedResources.getDrawableResource(manager, getCallingPackage(),
				SharedResources.DRAWABLE_MENU_DONTSAVE);
		if (dontSaveTitle != null) {
			dontSaveItem.setTitle(dontSaveTitle);
		}
		if (dontSaveIcon != null) {
			dontSaveItem.setIcon(dontSaveIcon);
		}

		final MenuItem saveItem = menu.findItem(R.id.twofortyfouram_locale_menu_save);
		final CharSequence saveTitle = SharedResources.getTextResource(manager,
				getCallingPackage(), SharedResources.STRING_MENU_SAVE);
		final Drawable saveIcon = SharedResources.getDrawableResource(manager,
				getCallingPackage(), SharedResources.DRAWABLE_MENU_SAVE);
		if (saveTitle != null) {
			saveItem.setTitle(saveTitle);
		}
		if (saveIcon != null) {
			saveItem.setIcon(saveIcon);
		}

		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.twofortyfouram_locale_menu_help:
			try {
				startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(Constants.HELP_URL)));
			}
			catch (final Exception e) {
				Toast.makeText(getApplicationContext(),
					com.twofortyfouram.locale.platform.R.string.twofortyfouram_locale_application_not_available,
						Toast.LENGTH_LONG).show();
			}
			return true;
		case R.id.twofortyfouram_locale_menu_save:
			finish();
			return true;
		case R.id.twofortyfouram_locale_menu_dontsave:
			isCancelled = true;
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void prepareResultForFinish() {
		if (isCancelled) {
			setResult(RESULT_CANCELED);
			return;
		}
		final Intent result = new Intent();
		final ToggleButton toggle = ((ToggleButton) findViewById(R.id.toggle));
		result.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE,
				new StateBundle().setOn(toggle.isChecked()).build());
		result.putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, toggle.getText());
		setResult(RESULT_OK, result);
	}
	
	@Override
	public void finish() {
		prepareResultForFinish();
		super.finish();
	}
	
	@Override
	protected void onResume() {
		eulaHandler.onResume();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		eulaHandler.onPause();
		super.onPause();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = eulaHandler.onCreateDialog(id);
		if (dialog != null) {
			return dialog;
		}
		return super.onCreateDialog(id);
	}

	/**
	 * Private inner class to encapsulate the logic to handle the EULA dialog.
	 * The logic is copied from the 'Toast' example code.
	 */
	private class EULAHandler {
		
		private AsyncTask<Void, Void, Boolean> prefReadTask;

		public void onResume() {
			prefReadTask = new AsyncTask<Void, Void, Boolean>() {

				@Override
				protected Boolean doInBackground(Void... params) {
					return getPreferences(MODE_PRIVATE).getBoolean(Constants.PREF_LICENSE_AGREED, false);
				}

				@Override
				protected void onPostExecute(Boolean result) {
					if (!result) {
						showDialog(DIALOG_LICENSE);
					}
				}
			};
			
			prefReadTask.execute();
		}

		public void onPause() {
			prefReadTask.cancel(true);
		}

		public Dialog onCreateDialog(int id) {
			// We handle only the license dialog.
			if (id != DIALOG_LICENSE) {
				return null;
			}
			final AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
			builder.setTitle(R.string.license_title);
			builder.setMessage(R.string.license_message);
			builder.setNegativeButton(R.string.license_disagree, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					isCancelled = true;
					finish();
				}
			});
			builder.setPositiveButton(R.string.license_agree, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					final SharedPreferences.Editor editor =
						getPreferences(MODE_PRIVATE).edit().putBoolean(Constants.PREF_LICENSE_AGREED, true);
					try {
						SharedPreferences.Editor.class.getMethod("apply").invoke(editor); //$NON-NLS-1$
					}
					catch (final Exception e) {
						editor.commit();
					}
				}
			});
			builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					isCancelled = true;
					finish();
				}
			});
			return builder.create();
		}
	}
}
