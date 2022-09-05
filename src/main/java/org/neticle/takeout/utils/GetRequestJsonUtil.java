package org.neticle.takeout.utils;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;

/**
 * @author Faruku123
 * @version 1.0
 */
public class GetRequestJsonUtil {
    // 解析request中的参数
    public static JSONObject getPostRequestJsonString(HttpServletRequest request) {
        BufferedReader br;
        StringBuilder sb = null;
        JSONObject jsonObject = null;
        try {
            br = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String line = null;
            sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            jsonObject = JSONObject.parseObject(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
