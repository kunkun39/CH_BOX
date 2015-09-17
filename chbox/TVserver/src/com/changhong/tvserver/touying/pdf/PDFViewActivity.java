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
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.changhong.tvserver.R;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;
import com.joanzapata.pdfview.listener.OnErrorListener;

import static java.lang.String.format;

public class PDFViewActivity extends Activity implements OnPageChangeListener,OnErrorListener {

    //public static final String SAMPLE_FILE = "http://192.168.1.101:9999/12345.pdf";	
   
    PDFView pdfView;
    String pdfName = null;//SAMPLE_FILE;

    Integer pageNumber = 1;
    boolean isZooming = false;
    int pdfPosition = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_pdf);
    	pdfView = (PDFView)findViewById(R.id.pdfView);    	
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
    	super.onNewIntent(intent);
    	if(getIntent()!= null)
    	{
    		pdfName = getIntent().getData().toString();
    		display(pdfName, true); 
    	}
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
            .onError(this)
            .load();          	
		}else if(fileUri.getScheme().equals("file")){
			pdfView.fromFile(new File(fileUri.getEncodedPath()))
            .defaultPage(pageNumber)
            .onPageChange(this)
            .onError(this)
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
    
    void reset()
    {
    	pdfPosition = 0;
    	isZooming = false;
    	//pdfView.resetZoomWithAnimation();
    }
    /* （非 Javadoc）
     * @see android.app.Activity#onKeyUp(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if (pageNumber > 1) {
				reset();
    			pdfView.jumpTo(--pageNumber);
    			return true;
			}
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if (pageNumber < pdfView.getPageCount()) {
				reset();
    			pdfView.jumpTo(++pageNumber);
    			return true;
			}
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
		{
			if (isZooming) {
    			pdfView.resetZoomWithAnimation();    			
			}
    		else {
    			pdfView.zoomCenteredTo(2.0f, new PointF(0.0f, 0.0f));
			}
    		isZooming = !isZooming;	
		}
		break;
		case KeyEvent.KEYCODE_DPAD_UP:
		{			
			if (isZooming)
				pdfView.moveTo(0.0f, pdfView.getCurrentYOffset() + 50.0f);
		}
		break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
		{
			if (isZooming)
				pdfView.moveTo(0.0f, pdfView.getCurrentYOffset() - 50.0f);
		}
		break;

		default:
			break;
		}    	
    	return super.onKeyUp(keyCode, event);
    }

    private boolean displaying(String fileName) {
        return fileName.equals(pdfName);
    }

	@Override
	public void OnError(Exception e) {
		Intent intent = new Intent("android.intent.action.VIEW");		
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath()+"/tmp.pdf"));
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        startActivity(intent);
        Toast.makeText(this.getApplicationContext(), "切换使用WPS打开，打开中。。。", Toast.LENGTH_SHORT).show();
        finish();
	}
}
