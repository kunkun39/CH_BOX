package com.changhong.common.utils;

import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * Created by Jack Wang
 */
public class WebUtils {

    public static String httpPostRequest(String url) {
        PostMethod postMethod = new PostMethod(url);
        HttpClient client = new HttpClient();
        client.setConnectionTimeout(5000);
        client.setTimeout(8000);
        client.getParams().setContentCharset("UTF-8");

        int status = 0;
        String response = "";
        try {
            status = client.executeMethod(postMethod);
            if (status == HttpStatus.SC_OK) {
                response = postMethod.getResponseBodyAsString();
            } else if (status == HttpStatus.SC_MOVED_TEMPORARILY) {
                response = postMethod.getResponseHeader("Location").getValue();
            } else {
                throw new RuntimeException("can't find update data for this request");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            postMethod.releaseConnection();
        }

        return response;
    }

    public static InputStream httpGetRequest(String url) {
        PostMethod postMethod = new PostMethod(url);
        HttpClient client = new HttpClient();
        client.setConnectionTimeout(5000);
        client.setTimeout(8000);
        client.getParams().setContentCharset("UTF-8");

        int status = 0;
        InputStream response = null;
        try {
            status = client.executeMethod(postMethod);
            if (status == HttpStatus.SC_OK) {
                response = postMethod.getResponseBodyAsStream();
            } else {
                throw new RuntimeException("can't find update data for this request");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return response;
    }

    public static String httpPostRequest(PostMethod postMethod) {
        HttpClient client = new HttpClient();
        client.setConnectionTimeout(5000);
        client.setTimeout(8000);
        client.getParams().setContentCharset("UTF-8");

        int status = 0;
        String response = "";
        try {
            status = client.executeMethod(postMethod);
            if (status == HttpStatus.SC_OK) {
                response = postMethod.getResponseBodyAsString();
            } else if (status == HttpStatus.SC_MOVED_TEMPORARILY) {
                response = postMethod.getResponseHeader("Location").getValue();
            } else {
                throw new RuntimeException("can't find update data for this request");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            postMethod.releaseConnection();
        }

        return response;
    }

    public static String convertLocalFileToHttpURL(String url) {
        if (StringUtils.hasLength(url)) {
            return url.replace("%", "%25").replace(" ", "%20").replace("+", "%2B").replace("#", "%23").replace("&", "%26").replace("=", "%3D").replace("?", "%3F").replace("^", "%5E");
        }
        return url;
    }

    public static String convertHttpURLToLocalFile(String url) {
        if (StringUtils.hasLength(url)) {
            return url.replace("%20", " ").replace("%2B", "+").replace("%23", "#").replace("%26", "&").replace("%3D", "=").replace("%3F", "?").replace("%5E", "^").replace("%25", "%");
        }
        return url;
    }
}
