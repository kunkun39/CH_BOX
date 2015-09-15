package com.changhong.touying.activity;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.StringUtils;
import com.changhong.common.utils.NetworkUtils;
import com.changhong.common.widgets.BoxSelecter;
import com.changhong.common.widgets.IpSelectorDataServer;
import com.changhong.touying.R;
import com.changhong.touying.file.FileItem;
import com.changhong.touying.nanohttpd.NanoHTTPDService;

import com.changhong.touying.tab.PDFTouyingTab;
import com.changhong.touying.tab.PPTTouyingTab;

/**
 * Created by Jack Wang
 */
public class OtherDetailsActivity extends FragmentActivity {
	private String TAG = "PPTDetailsActivity";
	private Button back;
    private BoxSelecter ipSelecter;		
	
	private List<FileItem> ppts=new ArrayList<FileItem>();
	private List<FileItem> pdfs=new ArrayList<FileItem>();
	PDFTouyingTab mPDFTouyingTab = new PDFTouyingTab();
	PPTTouyingTab mPPTTouyingTab = new PPTTouyingTab();	
	
	static Intent mIntentForword = null;
	
	Handler mHandler=null;
	
	private final static int PPTLIST_REFRESH=1;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        initView();

        initData();

        initEvent();
        
    }

    private void initView() {
        setContentView(R.layout.activity_ppt_category);
        
        /**
		 * IP连接部分
		 */
		back = (Button) findViewById(R.id.btn_back);				
		
		mHandler=new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case PPTLIST_REFRESH:
					Log.d(TAG, ">>>>>>>>>REFRESH");					
					mPDFTouyingTab.setList(pdfs);
					mPPTTouyingTab.setList(ppts);
					break;
				default:
					break;
				}
				super.handleMessage(msg);
			}
			
		};
		TextView pdf_tab =  (TextView)findViewById(R.id.other_category_pdf);
		pdf_tab.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getSupportFragmentManager().beginTransaction().hide(mPPTTouyingTab).show(mPDFTouyingTab).commitAllowingStateLoss();
				((TextView)findViewById(R.id.other_category_ppt)).setTextColor(getResources().getColor(R.color.white));
				((TextView)findViewById(R.id.other_category_pdf)).setTextColor(getResources().getColor(R.color.orange));
			}
		});
		TextView ppt_tab =  (TextView)findViewById(R.id.other_category_ppt);
		ppt_tab.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {			
				getSupportFragmentManager().beginTransaction().hide(mPDFTouyingTab).show(mPPTTouyingTab).commitAllowingStateLoss();
				((TextView)findViewById(R.id.other_category_ppt)).setTextColor(getResources().getColor(R.color.orange));
				((TextView)findViewById(R.id.other_category_pdf)).setTextColor(getResources().getColor(R.color.white));
			}
		});
		
		getSupportFragmentManager().beginTransaction().add(R.id.otherlist, mPDFTouyingTab).hide(mPDFTouyingTab).commitAllowingStateLoss();
		getSupportFragmentManager().beginTransaction().add(R.id.otherlist, mPPTTouyingTab).commitAllowingStateLoss();
    }

	private void initData() {
		ppts.clear();
		pdfs.clear();
		if(getIntent() != null)
		{
			mIntentForword = (Intent) getIntent().getParcelableExtra("forwardIntent");
		}
		
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				getAllPPTFiles(Environment.getExternalStorageDirectory());
				mHandler.sendEmptyMessage(PPTLIST_REFRESH);
			}
		}).start();
    }

    private void initEvent() {
    	/**
		 * IP连接部分
		 */
    	ipSelecter = new BoxSelecter(this, (TextView) findViewById(R.id.title), (ListView) findViewById(R.id.clients), (Button) findViewById(R.id.btn_list), new Handler(getMainLooper()));        
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);
				finish();
			}
		});
    }


    /**
     * **********************************************投影部分*********************************************************
     */

    public static void touYing(Context context,String pptPath) {
    	String ipAddress=null;
        /**
         * first check the wifi is connected
         */
        if (!NetworkUtils.isWifiConnected(context)) {
            Toast.makeText(context, "请链接无线网络", Toast.LENGTH_SHORT).show();
            return;
        }

        /**
         * second check the mobile is connect to box
         */
        if (!StringUtils.hasLength(IpSelectorDataServer.getInstance().getCurrentIp())) {
            Toast.makeText(context, "手机未连接机顶盒，请检查网络", Toast.LENGTH_SHORT).show();
            return;
        }

        /**
         * fourth begin to tou ying
         */
        try {
            MyApplication.vibrator.vibrate(100);

            //获取IP和外部存储路径
            ipAddress = NetworkUtils.getLocalHostIp();
            String httpAddress = "http://" + ipAddress + ":" + NanoHTTPDService.HTTP_PORT;

            //生成访问图片的HTTP URL
            String newImagePath = null;
            if (pptPath.startsWith(NanoHTTPDService.defaultHttpServerPath)) {
                newImagePath = pptPath.replace(NanoHTTPDService.defaultHttpServerPath, "").replace(" ", "%20");
            } else {
                for (String otherHttpServerPath : NanoHTTPDService.otherHttpServerPaths) {
                    if (pptPath.startsWith(otherHttpServerPath)) {
                        newImagePath = pptPath.replace(otherHttpServerPath, "").replace(" ", "%20");
                    }
                }
            }

            String tmpHttpAddress = httpAddress + newImagePath;

            //判断URL是否符合规范，如果不符合规范，就1重命名文件
            try {
                URI.create(tmpHttpAddress);
            } catch (Exception e) {
            	Toast.makeText(context, "PDF文件推送失败", Toast.LENGTH_SHORT).show();
            	return;
            }

            /**
             * 有时候用户在进入投影页面，但是确没有投影动作，http服务关闭，但是用户现在点击投影，所以这里需要先检查有没有HTTP服务
             */
            if (NanoHTTPDService.httpServer == null) {
                Intent http = new Intent(context, NanoHTTPDService.class);
                context.startService(http);
                //Sleep 1s is used for let http service started fully
                SystemClock.sleep(1000);
            }

            //发送播放地址
            ClientSendCommandService.msg = "other_open:"+tmpHttpAddress;
            ClientSendCommandService.handler.sendEmptyMessage(4);
            
            if (mIntentForword != null) {            	
                context.startActivity(mIntentForword);
			}
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "PDF文件获取失败", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * **********************************************系统重载********************************************************
     */

    @Override
    protected void onDestroy() {
        super.onDestroy();
    	if (ipSelecter != null) {
			ipSelecter.release();
		}
    	mIntentForword = null;
    }
    
    private void getAllPPTFiles(File root){ 
        File files[] = root.listFiles();  
        if(files != null){  
            for (File f : files){  
                if(f.isDirectory()){  
                	getAllPPTFiles(f);
                }else{
                	String path = f.getAbsolutePath();
                    if(path.endsWith(".PPT")
                    		||path.endsWith(".ppt")
                    		|| path.endsWith("pptx")){
                    	Log.d(TAG, f.getAbsolutePath());                    	
                    	FileItem ppt=new FileItem();
                    	ppt.setPath(path);
                    	ppt.setTitle(path.substring(path.lastIndexOf("/")+1, path.length()));
                    	ppts.add(ppt);
                    }
                    else if(path.endsWith(".pdf")){
                    	Log.d(TAG, f.getAbsolutePath());                    	
                    	FileItem pdf=new FileItem();
                    	pdf.setPath(path);
                    	pdf.setTitle(path.substring(path.lastIndexOf("/")+1, path.length()));
                    	pdfs.add(pdf);
                    }
                }  
            }  
        }  
    }      
}
