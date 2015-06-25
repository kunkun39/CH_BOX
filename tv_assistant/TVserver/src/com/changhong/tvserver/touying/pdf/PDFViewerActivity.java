/**
 * 
 */
package com.changhong.tvserver.touying.pdf;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.changhong.tvserver.IDataContainer;
import com.changhong.tvserver.IMessageListener;
import com.changhong.tvserver.R;
import com.changhong.tvserver.TVSocketControllerService;
import com.joanzapata.pdfview.PDFView;

/**
 * @author yves.yang
 *
 */
public class PDFViewerActivity extends Activity implements IMessageListener,PDFConstant{
	
	private PDFView pdfView = null;
	private IDataContainer dataContainer = null;

/**
 * 消息获取
 * =======================================================================================
 */
	private Handler handler = new PDFViewerHandler(PDFViewerActivity.this);


	static class PDFViewerHandler extends Handler
	{
		PDFViewerActivity activity;
		public PDFViewerHandler(PDFViewerActivity activity)
		{
			this.activity = activity;
		}
		
		@Override
		public void handleMessage(Message msg)
		{
			try {						
				String command = (String)msg.obj;
				if (command.equalsIgnoreCase(TYPE + ":" + CMD_LEFT)) {
					activity.scrollPage(true);
				}
				else if(command.equalsIgnoreCase(TYPE + ":" + CMD_RIGHT)){
					activity.scrollPage(false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			super.handleMessage(msg);
		}
	};
	
/**
 * 函数重写
 * ==========================================================================================
 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_pdf_viewer);

		Intent intent = getIntent();
		if (intent != null) {
			String path = String.valueOf(intent.getData());
			pdfView = (PDFView)findViewById(R.id.pdfView);
			pdfView.fromAsset(path).load();
		}	
		
		dataContainer = TVSocketControllerService.dataContainer;		
	}

	@Override
	public void onDataChanged(String message) {
		Message msg = handler.obtainMessage();
		msg.obj = new String(message);
		handler.dispatchMessage(msg);
	}	
		
	@Override
	protected void onResume() {
		super.onResume();
		dataContainer.registListener(this,TYPE);
	}
		
	@Override
	protected void onPause() {
		super.onPause();
		dataContainer.unregistListener(this);
	}	
	
/**
 * 私有方法
 * ===============================================================================================
 */
	private void scrollPage(boolean isLeft)
	{
		if (pdfView == null) {
			return ;
		}
		int pageCount = pdfView.getPageCount();
		int page = pdfView.getCurrentPage();
		if (isLeft && page > 1) {
			pdfView.jumpTo(--page);
		}
		else if (page < pageCount) {
			pdfView.jumpTo(++page);
		}
	}
}
