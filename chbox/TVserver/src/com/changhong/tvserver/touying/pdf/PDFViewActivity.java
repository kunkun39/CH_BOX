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

import java.io.File;
import java.net.URI;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.changhong.tvserver.R;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import static java.lang.String.format;

public class PDFViewActivity extends Activity implements OnPageChangeListener {

    //public static final String SAMPLE_FILE = "http://192.168.1.101:9999/12345.pdf";	
   
    PDFView pdfView;
    String pdfName = null;//SAMPLE_FILE;

    Integer pageNumber = 1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_pdf);
    	pdfView = (PDFView)findViewById(R.id.pdfView);    	
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	if(getIntent()!= null)
    	{
    		pdfName = getIntent().getData().toString();
    		display(pdfName, true); 
    	}
    }
    
    void afterViews() {
        display(pdfName, false);
    }

    private void display(String assetFileName, boolean jumpToFirstPage) {
        if (jumpToFirstPage) pageNumber = 1;
        setTitle(pdfName = assetFileName);
        Uri fileUri = Uri.parse(assetFileName);
        if (fileUri.getScheme().equals("http")) {
        	pdfView.fromUrl(fileUri)
            .defaultPage(pageNumber)
            .onPageChange(this)
            .load();          	
		}else if(fileUri.getScheme().equals("file")){
			pdfView.fromFile(new File(fileUri.getEncodedPath()))
            .defaultPage(pageNumber)
            .onPageChange(this)
            .load();
		}
		else {
			Toast.makeText(this, "文件格式不支持", Toast.LENGTH_SHORT).show();
	        finish();
		}        
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(format("%s %s / %s", pdfName, page, pageCount));
    }
    
    /* （非 Javadoc）
     * @see android.app.Activity#onKeyUp(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
    		if (pageNumber > 1) {
    			pdfView.jumpTo(--pageNumber);
    			return true;
			}
			
		}
    	else if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
    		if (pageNumber < pdfView.getPageCount()) {
    			pdfView.jumpTo(++pageNumber);
    			return true;
			}
		}
    	return super.onKeyUp(keyCode, event);
    }

    private boolean displaying(String fileName) {
        return fileName.equals(pdfName);
    }
}
