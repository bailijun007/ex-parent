/**
 * @author zw
 * @date 2019/8/9
 */
package com.hp.sh.expv3.config.redis.setting;

import com.hp.sh.expv3.bb.extension.constant.BbextendConst;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = BbextendConst.MODULE_NAME + ".rediskey")
public class BbExtendRedisKeySetting {

}
