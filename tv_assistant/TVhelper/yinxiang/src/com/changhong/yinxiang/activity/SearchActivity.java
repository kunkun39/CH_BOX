package com.changhong.yinxiang.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.voicerecognition.android.Candidate;
import com.baidu.voicerecognition.android.VoiceRecognitionClient;
import com.baidu.voicerecognition.android.VoiceRecognitionConfig;
import com.changhong.baidu.BaiDuVoiceConfiguration;
import com.changhong.yinxiang.R;

public class SearchActivity extends Activity {

	private EditText recognitionWord;
	
	/**********************************************语音部分代码*********************************************************/

    /**
     * baidu recognition client, void to init many times, so use static here
     */
    private static VoiceRecognitionClient recognitionClient;
    /**
     * 识别回调接口
     */
    private BaiDuVoiceChannelDialogRecogListener recogListener = new BaiDuVoiceChannelDialogRecogListener();


    /**
     * 初始化百度的配置
     */
    private void initBaiduConfiguration() {
        if (recognitionClient == null) {
            recognitionClient = VoiceRecognitionClient.getInstance(SearchActivity.this);
            recognitionClient.setTokenApis(BaiDuVoiceConfiguration.API_KEY, BaiDuVoiceConfiguration.SECRET_KEY);
        }
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		initBaiduConfiguration();
        
        ImageView colection=(ImageView)findViewById(R.id.voice);
        recognitionWord=(EditText)findViewById(R.id.tv1);
        colection.setOnTouchListener(myClick);
	}

	private OnTouchListener myClick=new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				svr();
                break;
            case MotionEvent.ACTION_UP:
                SystemClock.sleep(1000);
                recognitionClient.speakFinish();
                break;
            default:
                break;
        }
			return false;
		}
	};
	
	private void svr(){
		/**
         * stop first, because last action maybe not finished
         */
       recognitionClient.stopVoiceRecognition();
       /**
        * 语音的配置
        */
       VoiceRecognitionConfig config = BaiDuVoiceConfiguration.getVoiceRecognitionConfig();
       /**
        * 下面发起识别
        */
       int code = recognitionClient.startVoiceRecognition(recogListener, config);
       if (code != VoiceRecognitionClient.START_WORK_RESULT_WORKING) {
           Toast.makeText(SearchActivity.this, "网络连接出错，请重新尝试" , Toast.LENGTH_LONG).show();
       }
	}
	
	
	/**
     * 百度语音监听器
     */
    public class BaiDuVoiceChannelDialogRecogListener implements VoiceRecognitionClient.VoiceClientStatusChangeListener {
        /**
         * 正在识别中
         */
        private boolean isRecognitioning = false;

        private int recognitioningFailedTimes = 0;

        /**
         * channel match list, integer value stand for match time, compare han zi one by one
         */
        private Map<String, Integer> matchChannel = new HashMap<String, Integer>();

        @Override
        public void onClientStatusChange(int status, Object obj) {
            switch (status) {
                // 语音识别实际开始，这是真正开始识别的时间点，需在界面提示用户说话。
                case VoiceRecognitionClient.CLIENT_STATUS_START_RECORDING:
                    isRecognitioning = true;
                    break;
                // 检测到语音起点
                case VoiceRecognitionClient.CLIENT_STATUS_SPEECH_START:
                    break;
                // 已经检测到语音终点，等待网络返回
                case VoiceRecognitionClient.CLIENT_STATUS_SPEECH_END:
                    break;
                // 语音识别完成，显示obj中的结果
                case VoiceRecognitionClient.CLIENT_STATUS_FINISH:
                    isRecognitioning = false;
                    updateRecognitionResult(obj);
                    break;
                // 处理连续上屏
                case VoiceRecognitionClient.CLIENT_STATUS_UPDATE_RESULTS:
                    break;
                // 用户取消
                case VoiceRecognitionClient.CLIENT_STATUS_USER_CANCELED:
                    recognitionClient.stopVoiceRecognition();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onError(int errorType, int errorCode) {
            Toast.makeText(SearchActivity.this, "抱歉哟，我们不能识别空指令" , Toast.LENGTH_LONG).show();
            isRecognitioning = false;
            recognitionClient.stopVoiceRecognition();
        }

        @Override
        public void onNetworkStatusChange(int status, Object obj) {
            // 这里不做任何操作不影响简单识别
        }

        /**
         * 将识别结果更新到UI上，搜索模式结果类型为List<String>,输入模式结果类型为List<List<Candidate>>
         */
        private void updateRecognitionResult(Object result) {
            String recognitionResult = "";
            if (result != null && result instanceof List) {
                List results = (List) result;
                if (results.size() > 0) {
                    if (results.get(0) instanceof List) {
                        List<List<Candidate>> sentences = (List<List<Candidate>>) result;
                        StringBuffer sb = new StringBuffer();
                        for (List<Candidate> candidates : sentences) {
                            if (candidates != null && candidates.size() > 0) {
                                sb.append(candidates.get(0).getWord());
                            }
                        }
                        recognitionResult = sb.toString().replace("。", "");
                    } else {
                        recognitionResult = results.get(0).toString().replace("。", "");
                    }
                }
            }

            /**
             * used for check yuying laucher is successful or not
             * <p>
             * we have two flows:
             * 1 - if text start with "打开"，"启动"，"开启" go to open box app way
             * 2 - else go to switch channel way
             */
            boolean hasResult = false;
            if (!TextUtils.isEmpty(recognitionResult)) {
                /********************************************处理用户说的话********************************************/
            	recognitionWord.setText(recognitionResult);
            	/********************************************结束处理用户说的话******************************************/
            }

//            if (!hasResult) {
//                recognitioningFailedTimes = recognitioningFailedTimes + 1;
//                if (recognitioningFailedTimes == 3) {
//                    recognitioningFailedTimes = 0;
//                } else {
//                    Toast.makeText(SearchActivity.this, "抱歉哟，目前还不支持该指令:" + recognitionResult, Toast.LENGTH_LONG).show();
//                }
//            } else {
//                recognitioningFailedTimes = 0;
//            }
        }
    }
}
