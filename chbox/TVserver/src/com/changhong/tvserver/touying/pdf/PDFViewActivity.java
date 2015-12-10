/**
 * Copyright 2014 Joan Zapata
 *
 * This file is part of Android-pdfview.
 *
 * Android-pdfview is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Android-pdfview is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Android-pdfview.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.changhong.tvserver.touying.pdf;

import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

import com.changhong.tvserver.R;

public class PDFViewActivity extends Activity implements
		FilePicker.FilePickerSupport {

	private MuPDFCore core;
	private MuPDFReaderView mDocView;
	private View mButtonsView;
	private EditText mPasswordView;

	private int  pageNumer=0;
	private TextView mPageNumberView;

	private AlertDialog.Builder mAlertBuilder;

	static private AlertDialog.Builder gAlertBuilder;

	static public AlertDialog.Builder getAlertBuilder() {
		return gAlertBuilder;
	}

	private MuPDFCore openFile(String path) {

		try {
			core = new MuPDFCore(this, path);
			// New file: drop the old outline data
			OutlineActivityData.set(null);
		} catch (Exception e) {
			System.out.println(e);
			return null;
		} catch (java.lang.OutOfMemoryError e) {
			// out of memory is not an Exception, so we catch it separately.
			System.out.println(e);
			return null;
		}
		return core;
	}

	private MuPDFCore openBuffer(byte buffer[], String magic) {

		System.out.println("Trying to open byte buffer");

		try {
			core = new MuPDFCore(this, buffer, magic);
			// New file: drop the old outline data
			OutlineActivityData.set(null);
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
		return core;
	}

	@Override
    protected void onNewIntent(Intent intent) {
    	super.onNewIntent(intent);
    	if(getIntent()!= null)
    	{
    		display(getIntent()); 
    	}
    }

	private void display(Intent intent) {
		
		Log.i(null, "display is running ");
		
		byte buffer[] = null;
		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			Uri uri = intent.getData();
			System.out.println("URI to open is: " + uri);
			if (uri.toString().startsWith("content://")) {
				String reason = null;
				try {
					InputStream is = getContentResolver().openInputStream(
							uri);
					int len = is.available();
					buffer = new byte[len];
					is.read(buffer, 0, len);
					is.close();
				} catch (java.lang.OutOfMemoryError e) {
					System.out
							.println("Out of memory during buffer reading");
					reason = e.toString();
				} catch (Exception e) {

					System.out.println("Exception reading from stream: "
							+ e);

					try {
						Cursor cursor = getContentResolver().query(uri,
								new String[] { "_data" }, null, null, null);
						if (cursor.moveToFirst()) {
							String str = cursor.getString(0);
							if (str == null) {
								reason = "Couldn't parse data in intent";
							} else {
								uri = Uri.parse(str);
							}
						}
					} catch (Exception e2) {
						System.out
								.println("Exception in Transformer Prime file manager code: "
										+ e2);
						reason = e2.toString();
					}
				}
				if (reason != null) {
					buffer = null;
					Resources res = getResources();
					AlertDialog alert = mAlertBuilder.create();
					setTitle(String
							.format(res
									.getString(R.string.cannot_open_document_Reason),
									reason));
					alert.setButton(AlertDialog.BUTTON_POSITIVE,
							getString(R.string.dismiss),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							});
					alert.show();
					return;
				}
			}
			if (buffer != null) {
				core = openBuffer(buffer, intent.getType());
			} else {
				String path = Uri.decode(uri.getEncodedPath());
				if (path == null) {
					path = uri.toString();
				}
				core = openFile(path);
			}
		}
		if (core != null && core.needsPassword()) {
			requestPassword();
			return;
		}
		if (core != null && core.countPages() == 0) {
			core = null;
		}

	if (core == null) {
		AlertDialog alert = mAlertBuilder.create();
		alert.setTitle(R.string.cannot_open_document);
		alert.setButton(AlertDialog.BUTTON_POSITIVE,
				getString(R.string.dismiss),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
		alert.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				finish();
			}
		});
		alert.show();
		return;
	}

	createUI();
		
	}
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		mAlertBuilder = new AlertDialog.Builder(this);
		gAlertBuilder = mAlertBuilder; // keep a static copy of this that other classes can use
		
		display(getIntent());

	}

	public void requestPassword() {
		mPasswordView = new EditText(this);
		mPasswordView.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
		mPasswordView
				.setTransformationMethod(new PasswordTransformationMethod());

		AlertDialog alert = mAlertBuilder.create();
		alert.setTitle(R.string.enter_password);
		alert.setView(mPasswordView);
		alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.okay),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (core.authenticatePassword(mPasswordView.getText()
								.toString())) {
							createUI();
						} else {
							requestPassword();
						}
					}
				});
		alert.setButton(AlertDialog.BUTTON_NEGATIVE,
				getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
		alert.show();
	}

	public void createUI() {
		if (core == null)
			return;

		// Now create the UI.
		// First create the document view
		mDocView = new MuPDFReaderView(this) {
			@Override
			protected void onMoveToChild(int i) {
				if (core == null)
					return;

				mPageNumberView.setText(String.format("%d / %d", i + 1,
						core.countPages()));
				super.onMoveToChild(i);
			}

		};
		mDocView.setAdapter(new MuPDFPageAdapter(this, this, core));

		// Make the buttons overlay, and store all its
		// controls in variables
		makeButtonsView();

		mDocView.setDisplayedViewIndex(pageNumer);

		// Stick the document view and the buttons overlay into a parent view
		RelativeLayout layout = new RelativeLayout(this);
		layout.addView(mDocView);
		layout.addView(mButtonsView);
		setContentView(layout);

	}

	public void onDestroy() {
		if (mDocView != null) {
			mDocView.applyToChildren(new ReaderView.ViewMapper() {
				void applyToView(View view) {
					((MuPDFView) view).releaseBitmaps();
				}
			});
		}
		if (core != null)
			core.onDestroy();

		core = null;
		super.onDestroy();
	}

	private void updatePageNumView(int index) {
		if (core == null)
			return;
		mPageNumberView.setText(String.format("%d / %d", index + 1,
				core.countPages()));
	}

	private void makeButtonsView() {

		mButtonsView = getLayoutInflater().inflate(R.layout.buttons, null);
		mPageNumberView = (TextView) mButtonsView.findViewById(R.id.pageNumber);

	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			{
				
				if(pageNumer>=1){
					mDocView.moveToPrevious();
					updatePageNumView(--pageNumer);
				}
				
			}
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			{

				if(pageNumer<core.countPages()-1){
					mDocView.moveToNext();
					updatePageNumView(++pageNumer);
				}
			}
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:

			Log.i(null, "KEYCODE_DPAD_CENTER");

			break;
		case KeyEvent.KEYCODE_DPAD_UP:

			mDocView.scrollDistance(100);
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:

			mDocView.scrollDistance(-100);
			break;

		default:
			break;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void performPickFor(FilePicker picker) {

	}
}
