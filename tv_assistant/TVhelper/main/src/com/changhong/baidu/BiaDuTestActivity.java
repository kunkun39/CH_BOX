package com.changhong.baidu;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.baidu.voicerecognition.android.Candidate;
import com.baidu.voicerecognition.android.DataUploader;
import com.baidu.voicerecognition.android.VoiceRecognitionClient;
import com.baidu.voicerecognition.android.VoiceRecognitionConfig;
import com.changhong.tvhelper.R;

import java.util.List;

/**
 * Created by Jack Wang
 */
public class BiaDuTestActivity extends Activity {

    /**
     * 正在识别中
     */
    private boolean isRecognitioning = false;

    /**
     * 百度语音识别客户端
     */
    private VoiceRecognitionClient recognitionClient;

    /**
     * 识别回调接口
     */
    private BaiDuVoiceRecogListener recogListener = new BaiDuVoiceRecogListener();

    /**
     * 结果展示
     */
    private EditText resultText = null;

    /**
     * 调用语音识别的操作按钮，长按表示开始并识别，弹起表示结束
     */
    private Button start = null;

    private Button confirm = null;

    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidu_test);
        /**
         * 初始化识别的客户端
         */
        recognitionClient = VoiceRecognitionClient.getInstance(this);
        recognitionClient.setTokenApis(BaiDuVoiceConfiguration.API_KEY, BaiDuVoiceConfiguration.SECRET_KEY);
        uploadContacts();

        /**
         * 加载View
         */
        resultText = (EditText) findViewById(R.id.recognition_text);
        start = (Button) findViewById(R.id.control_start);
        confirm = (Button) findViewById(R.id.control_confirm);

        /**
         * 设置按钮事件
         */
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultText.setText(null);
                /**
                 * 语音的配置
                 */
                VoiceRecognitionConfig config = new VoiceRecognitionConfig();
                config.setProp(BaiDuVoiceConfiguration.CURRENT_PROP);
                config.setLanguage(BaiDuVoiceConfiguration.CURRENT_LANGUAGE);
                config.enableContacts(); // 启用通讯录
                config.enableVoicePower(BaiDuVoiceConfiguration.SHOW_VOL); // 音量反馈。
                if (BaiDuVoiceConfiguration.PLAY_START_SOUND) {
                    config.enableBeginSoundEffect(R.raw.bdspeech_recognition_start); // 设置识别开始提示音
                }
                if (BaiDuVoiceConfiguration.PLAY_END_SOUND) {
                    config.enableEndSoundEffect(R.raw.bdspeech_speech_end); // 设置识别结束提示音
                }
                config.setSampleRate(VoiceRecognitionConfig.SAMPLE_RATE_8K); // 设置采样率,需要与外部音频一致

                // 下面发起识别
                int code = recognitionClient.startVoiceRecognition(recogListener, config);
                if (code != VoiceRecognitionClient.START_WORK_RESULT_WORKING) {
                    resultText.setText(getString(R.string.error_start, code));
                }

                //开始录音
                boolean start = code == VoiceRecognitionClient.START_WORK_RESULT_WORKING;
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recognitionClient.speakFinish();
            }
        });

//        start.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                /**
//                 * 语音的配置
//                 */
//                VoiceRecognitionConfig config = new VoiceRecognitionConfig();
//                config.setProp(BaiDuProperties.CURRENT_PROP);
//                config.setLanguage(BaiDuProperties.CURRENT_LANGUAGE);
//                config.enableContacts(); // 启用通讯录
//                config.enableVoicePower(BaiDuProperties.SHOW_VOL); // 音量反馈。
//                if (BaiDuProperties.PLAY_START_SOUND) {
//                    config.enableBeginSoundEffect(R.raw.move); // 设置识别开始提示音
//                }
//                if (BaiDuProperties.PLAY_END_SOUND) {
//                    config.enableEndSoundEffect(R.raw.bakmove); // 设置识别结束提示音
//                }
//                config.setSampleRate(VoiceRecognitionConfig.SAMPLE_RATE_8K); // 设置采样率,需要与外部音频一致
//
//                // 下面发起识别
//                int code = recognitionClient.startVoiceRecognition(recogListener, config);
//                if (code != VoiceRecognitionClient.START_WORK_RESULT_WORKING) {
//                    Toast.makeText(BiaDuTestActivity.this, "请设置你的网络!", Toast.LENGTH_SHORT).show();
//                    resultText.setText(getString(R.string.error_start, code));
//                }
//
//                //开始录音
//                boolean start = code == VoiceRecognitionClient.START_WORK_RESULT_WORKING;
//                return start;
//            }
//        });

//        confirm.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_UP:
//                        recognitionClient.speakFinish();
//                        Toast.makeText(BiaDuTestActivity.this, "抬起", Toast.LENGTH_SHORT).show();
//                        break;
//                    default:
//                        break;
//                }
//                return false;
//            }
//        });
    }

    /**
     * 重写用于处理语音识别回调的监听器
     */
    class BaiDuVoiceRecogListener implements VoiceRecognitionClient.VoiceClientStatusChangeListener {

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
                    updateRecognitionResult(obj);
                    break;
                // 用户取消
                case VoiceRecognitionClient.CLIENT_STATUS_USER_CANCELED:
                    isRecognitioning = false;
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onError(int errorType, int errorCode) {
            isRecognitioning = false;
            Toast.makeText(BiaDuTestActivity.this, "语音识别有误，请重新尝试!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNetworkStatusChange(int status, Object obj) {
            // 这里不做任何操作不影响简单识别
        }
    }

    /**
     * 将识别结果更新到UI上，搜索模式结果类型为List<String>,输入模式结果类型为List<List<Candidate>>
     */
    private void updateRecognitionResult(Object result) {
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
                    resultText.setText(sb.toString().replace("。", ""));
                } else {
                    resultText.setText(results.get(0).toString().replace("。", ""));
                }
            }

        }
    }

    private void uploadContacts(){
        DataUploader dataUploader = new DataUploader(BiaDuTestActivity.this);
        dataUploader.setApiKey(BaiDuVoiceConfiguration.API_KEY, BaiDuVoiceConfiguration.SECRET_KEY);

        String jsonString = "[{\"name\":\"兆维\", \"frequency\":1}, {\"name\":\"林新汝\", \"frequency\":2}]";
        try{
            dataUploader.uploadContactsData(jsonString.getBytes("utf-8"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
