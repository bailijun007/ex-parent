package com.hp.sh.expv3.fund.c2c.component;

import com.hp.sh.expv3.fund.c2c.entity.NotifyParam;
import com.hp.sh.expv3.fund.c2c.service.BuyService;
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


    @Value("${plpay.server.host}")
    private String apiHost;

    @Value("${plpay.server.shopNo}")
    private String shopNo;

    @Value("${plpay.server.signType}")
    private String signType;

    @Value("${plpay.server.md5Key}")
    private String md5Key;

    @Value("${expv3.base.url}")
    private String baseUrl;

    @Value("${plpay.server.customerId}")
    private String customerId;

    /**
     * 检查重定向请求是否成功
     *
     * @param orderNo
     * @param orderCurrency
     * @param orderAmount
     * @param receiveUrl
     * @param pickupUrl
     * @param sign
     * @return 成功返回true 失败返回false
     */
    public Boolean checkSendUrl( String orderNo, String orderCurrency, String orderAmount, String receiveUrl, String pickupUrl, String sign) {
        String url = apiHost + "?orderNo=" + orderNo + "&customerId=" + customerId + "&orderCurrency=" + orderCurrency + "&orderAmount=" + orderAmount
                + "&receiveUrl=" + receiveUrl + "&pickupUrl=" + pickupUrl + "&shopNo=" + shopNo + "&signType=" + signType + "&sign=" + sign;

        System.out.println("重定向请求url为 = " + url);
        logger.info("重定向请求url为:{}", url);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        int codeValue = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getStatusCodeValue();
        if (codeValue == 200) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    /**
     * 获取加密后的签名
     *
     * @param pickupUrl
     * @param receiveUrl
     * @param orderNo
     * @param orderAmount
     * @param orderCurrency
     * @return
     */
    public String getSign(String pickupUrl, String receiveUrl, String orderNo, String orderAmount, String orderCurrency) {
        String sign = pickupUrl + receiveUrl + signType + orderNo + orderAmount + orderCurrency + customerId + md5Key;
        String md5 = DigestUtils.md5DigestAsHex(sign.getBytes());

        System.out.println("获取到加密后的签名 = " + md5);
        logger.info("获取到加密后的签名:{}", md5);

        return md5;
    }


    /**
     * 通知签名校验
     *MD5c2c157827741489168700USDTc2c1578277414891680000successpkriDZoEwB
     * @return
     */
    public boolean getNotifySign(@RequestBody NotifyParam param) {
        String str = param.getSignType() + param.getOrderNo() + param.getOrderAmount() + param.getOrderCurrency() + param.getTransactionId() + param.getStatus() + md5Key;
        String md5 = DigestUtils.md5DigestAsHex(str.getBytes());
        if (param.getSign().equals(md5)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

}
