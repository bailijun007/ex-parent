package com.hp.sh.expv3.fund.c2c.component;

import com.hp.sh.expv3.fund.c2c.entity.NotifyParam;
import org.apache.commons.collections4.MultiValuedMap;
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


    /**
     * 检查重定向请求是否成功
     * @param customerId
     * @param orderNo
     * @param orderCurrency
     * @param orderAmount
     * @param receiveUrl
     * @param pickupUrl
     * @param sign
     * @return 成功返回true 失败返回false
     */
    public Boolean checkSendUrl(String customerId, String orderNo, String orderCurrency, String orderAmount, String receiveUrl, String pickupUrl, String sign) {
        String url = apiHost + "?orderNo=" + orderNo + "&customerId=" + customerId + "&orderCurrency=" + orderCurrency + "&orderAmount=" + orderAmount
                + "&receiveUrl=" + receiveUrl + "&pickupUrl=" + pickupUrl + "&shopNo=" + shopNo + "&signType=" + signType + "&sign=" + sign;

// todo 发送请求到第三方支付，发送之后只要不是系统错误即可
// TODO 老杜要加个接口，见tg dddd:2020.01.02 17:25:17 : 查询某个用户一段时间的提币数量 ,请求参数 用户id 开始时间,结束时间,币种(可选,不填为全部币种),结果返回一个集合
        RestTemplate restTemplate=new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<String>(headers);
         int codeValue = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getStatusCodeValue();
        if(codeValue==200){
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
     * @param customerId
     * @return
     */
    public String getSign(String pickupUrl, String receiveUrl, String orderNo, String orderAmount, String orderCurrency, String customerId) {
        String sign = pickupUrl + receiveUrl + signType + orderNo + orderAmount + orderCurrency + customerId + md5Key;
        String md5 = DigestUtils.md5DigestAsHex(sign.getBytes());
        return md5;
    }


    /**
     * 通知签名校验
     *
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
