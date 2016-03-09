package com.changhong.touying.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.storage.StorageManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.NetworkUtils;
import com.changhong.common.utils.StringUtils;
import com.changhong.common.utils.WebUtils;
import com.changhong.common.widgets.BoxSelecter;
import com.changhong.common.widgets.IpSelectorDataServer;
import com.changhong.touying.R;
import com.changhong.touying.adapter.FragmentAdapter;
import com.changhong.touying.file.FileItem;
import com.changhong.touying.nanohttpd.NanoHTTPDService;
import com.changhong.touying.tab.PDFTouyingTab;
import com.changhong.touying.tab.PPTTouyingTab;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jack Wang
 */
public class OtherDetailsActivity extends AppCompatActivity {
    private static final String TAG = "PPTDetailsActivity";

    private DrawerLayout mDrawerLayout;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private static final int PATH_DEEPTH = 5;

    private BoxSelecter ipSelecter;

    private List<FileItem> ppts = new ArrayList<FileItem>();
    private List<FileItem> pdfs = new ArrayList<FileItem>();
    PDFTouyingTab mPDFTouyingTab = new PDFTouyingTab();
    PPTTouyingTab mPPTTouyingTab = new PPTTouyingTab();

    static Intent mIntentForword = null;

    Handler mHandler = null;

    private final static int PPTLIST_REFRESH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        initData();

        initEvent();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.touying, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            finish();
        } else if (item.getItemId() == R.id.ipbutton) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return true;
    }

    private void setupViewPager() {
        mTabLayout = (TabLayout) findViewById(R.id.tabs);


        List<String> titles = new ArrayList<String>();
        titles.add("  PDF  ");
        titles.add("  PPT  ");
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(1)));

        List<Fragment> fragments = new ArrayList<Fragment>();


        fragments.add(mPDFTouyingTab);
        fragments.add(mPPTTouyingTab);


        FragmentAdapter adapter =
                new FragmentAdapter(getSupportFragmentManager(), fragments, titles);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(adapter);

    }

    private void initView() {
        setContentView(R.layout.activity_category_other);
        /**
         * IP连接部分
         */
        mDrawerLayout = (DrawerLayout) findViewById(R.id.touying_drawer);
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        toolbar.setTitle(" ");
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager();


        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                switch (msg.what) {
                    case PPTLIST_REFRESH:
                        Log.d(TAG, ">>>>>>>>>REFRESH");
                        mPDFTouyingTab.setdata(pdfs);
                        mPPTTouyingTab.setdata(ppts);
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }

        };

    }

    private void initData() {
        ppts.clear();
        pdfs.clear();
        if (getIntent() != null) {
            mIntentForword = (Intent) getIntent().getParcelableExtra("forwardIntent");
        }
        Toast.makeText(this, "加载中，请稍候。。。", Toast.LENGTH_LONG).show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                StorageManager sm = (StorageManager) OtherDetailsActivity.this.getSystemService(Context.STORAGE_SERVICE);
                Method method;
                try {
                    method = sm.getClass().getMethod("getVolumePaths", new Class[0]);
                    if (method != null) {

                        String paths[] = (String[]) method.invoke(sm, new Object[]{});
                        for (String path : paths) {
                            getAllPPTFiles(new File(path), 0);
                        }
                    } else {
                        getAllPPTFiles(Environment.getExternalStorageDirectory(), 0);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } finally {
                    mHandler.sendEmptyMessage(PPTLIST_REFRESH);
                }

            }
        }).start();
    }

    private void initEvent() {
        /**
         * IP连接部分
         */
        ipSelecter = new BoxSelecter(this, (TextView) findViewById(R.id.title), (ListView) findViewById(R.id.clients), new Handler(getMainLooper()));

    }


    /**
     * **********************************************投影部分*********************************************************
     */

    public static void touYing(Context context, String pptPath) {
        String ipAddress = null;
        /**
         * first check the wifi is connected
         */
        if (!NetworkUtils.isWifiConnected(context)) {
            Toast.makeText(context, "请连接无线网络", Toast.LENGTH_SHORT).show();
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
            pptPath = WebUtils.convertLocalFileToHttpURL(pptPath);
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
            ClientSendCommandService.msg = "other_open:" + tmpHttpAddress;
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

    private void getAllPPTFiles(File root, int deepth) {
        File files[] = root.listFiles();

        if (deepth >= PATH_DEEPTH)
            return;


        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    String name = f.getName();
                    int nameLen = name.length();

                    switch (nameLen) {
                        case 3:
                            if (name.equalsIgnoreCase("qrc")) {
                                continue;
                            }
                            if (name.equalsIgnoreCase("log")) {
                                continue;
                            }
                            break;
                        case 5:
                            if (name.equalsIgnoreCase("Photo")) {
                                continue;
                            }
                            if (name.equalsIgnoreCase("music")) {
                                continue;
                            }


                            if (name.equalsIgnoreCase("image")) {
                                continue;
                            }

                            if (name.equalsIgnoreCase("video")) {
                                continue;
                            }

                            if (name.equalsIgnoreCase("emoji")) {
                                continue;
                            }
                            break;
                        case 6:
                            if (name.equalsIgnoreCase("lyrics")) {
                                continue;
                            }
                            if (name.equalsIgnoreCase("Camera")) {
                                continue;
                            }
                            break;
                        case 7: {
                            if (name.equalsIgnoreCase("Android")) {
                                continue;
                            }

                            if (name.equalsIgnoreCase("picture")) {
                                continue;
                            }

                        }
                        break;
                        case 8: {
                            if (name.equalsIgnoreCase("internet")) {
                                continue;
                            }
                        }
                        break;

                        default:
                            break;
                    }

                    if (nameLen > 4) {
                        if ((name.matches(".*[cC][aA][cC][hH][eE].*"))
                                || (name.matches(".*[tT][hH][uU][mM][bB].*"))) {
                            continue;
                        }
                    }
                    if (nameLen > 3) {
                        if ((name.matches(".*[tT][Ee][mM][pP].*"))) {
                            continue;
                        }
                    }
                    if (nameLen > 2) {
                        if (name.matches(".*[tT][mM][pP].*")
                                || name.matches(".*[lL][oO][gG].*")
                                || name.matches(".*[mM][sS][gG].*")) {
                            continue;
                        }
                    }
                    getAllPPTFiles(f, deepth + 1);
                } else {
                    String path = f.getAbsolutePath();
                    if (path.endsWith(".PPT")
                            || path.endsWith(".ppt")
                            || path.endsWith("pptx")) {
                        Log.d(TAG, f.getAbsolutePath());
                        FileItem ppt = new FileItem();
                        ppt.setPath(path);
                        ppt.setTitle(path.substring(path.lastIndexOf("/") + 1, path.length()));
                        ppts.add(ppt);
                        mHandler.sendEmptyMessage(PPTLIST_REFRESH);
                    } else if (path.endsWith(".pdf")) {
                        Log.d(TAG, f.getAbsolutePath());
                        FileItem pdf = new FileItem();
                        pdf.setPath(path);
                        pdf.setTitle(path.substring(path.lastIndexOf("/") + 1, path.length()));
                        pdfs.add(pdf);
                        mHandler.sendEmptyMessage(PPTLIST_REFRESH);
                    }
                    Log.d(TAG, path);
                }

            }
        }
    }
}
