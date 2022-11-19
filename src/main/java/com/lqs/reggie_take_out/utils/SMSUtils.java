package com.lqs.reggie_take_out.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;

public class SMSUtils {

    public static void sendMessage(String signName, String templateCode,String phoneNumbers,String param) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-beijing", "", "");
        /** use STS Token
         DefaultProfile profile = DefaultProfile.getProfile(
         "<your-region-id>",           // The region ID
         "<your-access-key-id>",       // The AccessKey ID of the RAM account <LTAI5tPxpTpA5D5jk67PCoN4>
         "<your-access-key-secret>",   // The AccessKey Secret of the RAM account <jNf1tIUIszqWFLLkunwNLT2L2XTHVc>
         "<your-sts-token>");          // STS Token
         **/
        IAcsClient client = new DefaultAcsClient(profile);

        SendSmsRequest request = new SendSmsRequest();
        request.setPhoneNumbers(phoneNumbers);//接收短信的手机号码
        request.setSignName(signName);//短信签名名称
        request.setTemplateCode(templateCode);//短信模板CODE  SMS_154950909
        request.setTemplateParam("{\"code\":\""+param+"\"}");//短信模板变量对应的实际值

        try {
            SendSmsResponse response = client.getAcsResponse(request);
            System.out.println(new Gson().toJson(response));
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
        }

    }
}
