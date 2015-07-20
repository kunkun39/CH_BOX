package com.changhong.faq.service;

import android.util.Log;
import com.alibaba.fastjson.JSONObject;
import com.changhong.faq.assember.FaqAssember;
import com.changhong.faq.domain.AppDescription;
import com.changhong.faq.domain.Examination;
import com.changhong.common.utils.WebUtils;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * Created by Jack Wang
 */
public class HttpClientService {

    /**
     * 外网服务器访问地址
     */
//    private static String HOSTS = "http://www.ottserver.com:8081/";

    /**
     * 川网服务器访问地址
     */
    private static String HOSTS = "http://10.102.140.140:8083/";

    /**
     * 本地测试地址
     */
    //private static String HOSTS = "http://10.9.36.105:8080/";

    private static String TAG = "HttpClientService" ;

    private static boolean LOCAL = false;

    private static String Mac = null;

    public static String getWelcomePageResponse() {
        if (LOCAL) {
            return "{\"appDescription\":\"你好，你是那个\",\"categories\":[{\"id\":3,\"title\":\"你是那个\"},{\"id\":2,\"title\":\"关于美女的问卷调查\"},{\"id\":1,\"title\":\"关于节目喜爱程度的调查访问\"}]}";
        }

        String url = HOSTS + "faq/ott/getexaminationlist.html?type=BOTH";
        String response = WebUtils.httpPostRequest(url);
        return response;
    }

    public static String getExaminationById(int examinationId) {
        if (LOCAL) {
            return "{\"id\":1,\"title\":\"关于川网用户问卷调查\",\"description\":\"\",\"questions\":[{\"sequence\":1,\"questionType\":\"SINGLE\",\"title\":\"你是男的还是女的\",\"answers\":[{\"result\":\"A:男\"},{\"result\":\"B:女\"}]},{\"sequence\":2,\"questionType\":\"SINGLE\",\"title\":\"请问你家里有多少人\",\"answers\":[{\"result\":\"A:1\"},{\"result\":\"B:2\"},{\"result\":\"C:3\"},{\"result\":\"D:>3\"}]},{\"sequence\":3,\"questionType\":\"MUTI\",\"title\":\"下面那些节目你比较喜欢\",\"answers\":[{\"result\":\"A:喜剧\"},{\"result\":\"B:科幻\"},{\"result\":\"C:动作\"},{\"result\":\"D:爱情\"}]},{\"sequence\":4,\"questionType\":\"OBJECTIVE\",\"title\":\"请你的你年龄多大了\",\"answers\":[]}]}";
        }

        String url = HOSTS + "faq/ott/getexamination.html?examinationId=" + examinationId;
        String response = WebUtils.httpPostRequest(url);
        return response;
    }

    public static void sendExaminationResult(int examinationId, String result) {
        if (!LOCAL) {
            JSONObject o = new JSONObject();
            Mac = getMac();
            o.put("mac", Mac);
            o.put("examinationId", examinationId);
            o.put("answers", result);

            PostMethod method = new PostMethod(HOSTS + "faq/ott/sendexamination.html");
            method.addParameter("result", o.toString());
            WebUtils.httpPostRequest(method.toString());

            Log.i(TAG, "httpPostRequest------>");
            Log.i(TAG, "result=" + result);
        }
    }

    private static String getMac() {
        String macSerial = null;
        String str = "";

        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return macSerial;
    }

	public static void main(String[] args) throws Exception {
        String response = getWelcomePageResponse();
        AppDescription appDescription = FaqAssember.convertToAppDescription(response);
        System.out.printf("1");

        response = getExaminationById(1);
        Examination examination = FaqAssember.convertToExamination(response);
        System.out.printf("1");
    }
}
