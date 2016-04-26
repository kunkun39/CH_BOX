package com.chome.virtualkey;

import android.view.KeyEvent;

public class virtualkey {
	static {
		System.loadLibrary("virtualkey_cv200_std");
	}
	/*
	 * 	keyValue is the value in .kl
	 * For exmaple: HOME is 102 in .kl, so pass 102 here.
	 * keyRepeatCount can be passed 1, by default.
	*/
	native public void virtualkey_do(int keyValue, int keyRepeatCount);
	
	public void vkey_input(int keyValue, int keyRepeatCount)
	{
		virtualkey_do(keyValue, keyRepeatCount);
		virtualkey_do(keyValue, 0);//means key up.
	}
}
