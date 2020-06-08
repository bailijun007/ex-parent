package com.hp.sh.expv3.fund.c2c.component;

import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.c2c.entity.NotifyParam;
import com.hp.sh.expv3.fund.c2c.service.BuyService;
import com.hp.sh.expv3.fund.extension.error.ExFundError;
import org.apache.commons.collections4.MultiValuedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

@Component
public class PLpayClient {
    private static final Logger logger = LoggerFactory.getLogger(PLpayClient.class);

    //    @Value("${cnyPlpay.server.host}")
//    private String apiHost;
//
//    @Value("${cnyPlpay.server.shopNo}")
//    private String shopNo;
//
//    @Value("${cnyPlpay.server.signType}")
//    private String signType;
//
//    @Value("${cnyPlpay.server.md5Key}")
//    private String md5Key;
//
//    @Value("${cnyPlpay.server.customerId}")
//    private String customerId;
    @Value("${usdtPlpay.server.host}")
    private String apiHost;

    @Value("${usdtPlpay.server.pid}")
    private int pid;

    @Value("${usdtPlpay.server.name}")
    private String name;

    @Value("${usdtPlpay.server.md5Key}")
    private String md5Key;

    @Value("${usdtPlpay.server.sign_type}")
    private String signType;

    /**
     * 转发请求到第三方支付，并返回支付路径
     *
     * @param orderNo
     * @param orderCurrency
     * @param orderAmount
     * @param receiveUrl
     * @param pickupUrl
     * @param sign
     * @return 成功返回true 失败返回false
     */
    public String sendRequestUrl(String orderNo, String orderCurrency, String orderAmount, String receiveUrl, String pickupUrl, String sign) {
        String url = apiHost + "?pid=" + pid + "&trade_no=" + orderNo + "&notify_url=" + receiveUrl + "&return_url=" + pickupUrl
                + "&name=" + name + "&money=" + orderAmount + "&sign=" + sign + "&sign_type=" + signType;

        logger.info("重定向请求url为:{}", url);
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            HttpEntity<String> entity = new HttpEntity<String>(headers);
            int codeValue = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getStatusCodeValue();
            if (codeValue != 200) {
                throw new ExException(ExFundError.SEND_REQUEST_TO_C2C_SERVICE_FAIL);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * 获取加密后的签名
     *
     * @param return_url    页面跳转通知地址
     * @param notify_url    服务器异步通知地址
     * @param out_trade_no  商户订单号
     * @param money         金额
     * @param orderCurrency
     * @return
     */
    public String getSign(String return_url, String notify_url, String out_trade_no, String money, String orderCurrency) {
//        String sign = pickupUrl + receiveUrl + signType + orderNo + orderAmount + orderCurrency + customerId + md5Key;
//        String sign = pid + out_trade_no + notify_url + return_url + name + money + md5Key + signType;
        String sign = "money=" + money + "&name=" + name + "&notify_url=" + notify_url + "&pid=" + pid + "&return_url=" + return_url + "&trade_no=" + out_trade_no + md5Key;
        String md5 = DigestUtils.md5DigestAsHex(sign.getBytes());
        logger.info("获取到加密后的签名:{}", md5);
        return md5;
    }


    /**
     * 通知签名校验
     * MD5c2c157827741489168700USDTc2c1578277414891680000successpkriDZoEwB
     *
     * @return
     */
    public boolean getNotifySign(int pid, String trade_no, String type, String name, String money, String usdt, String trade_status, String sign, String sign_type) {
//        String str = param.getSignType() + param.getOrderNo() + param.getOrderAmount() + param.getOrderCurrency() + param.getTransactionId() + param.getStatus() + md5Key;
 //money,name,orderid,pid,sign,sign_type,trade_no,trade_status,type,usdt
        String str = "money="+money+"&name="+name+"&pid="+pid+"&trade_no="+trade_no+"&trade_status="+trade_status+"&type="+type+"&usdt="+usdt+md5Key;
        String md5 = DigestUtils.md5DigestAsHex(str.getBytes());
        if (sign.equals(md5)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

}
