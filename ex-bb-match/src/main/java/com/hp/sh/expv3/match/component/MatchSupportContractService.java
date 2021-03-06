/**
 * @author 10086
 * @date 2019/12/25
 */
package com.hp.sh.expv3.match.component;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.match.bo.BbSymbolBo;
import com.hp.sh.expv3.match.config.setting.BbmatchRedisKeySetting;
import com.hp.sh.expv3.match.config.setting.BbmatchSetting;
import com.hp.sh.expv3.match.util.BbUtil;
import com.hp.sh.expv3.match.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class MatchSupportContractService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("metadataRedisUtil")
    private RedisUtil metadataRedisUtil;

    @Autowired
    private BbmatchRedisKeySetting bbmatchRedisKeySetting;

    @Autowired
    private BbmatchSetting bbmatchSetting;

    private Set<String> supportAssetSymbol = new HashSet<>();

    public Set<String> getSupportAssetSymbol(boolean useCache) {

        if (useCache) {
            if (supportAssetSymbol.size() > 0) {
                return supportAssetSymbol;
            }
        }

        Map<String, String> symbolName2Symbol = metadataRedisUtil.hgetAll(bbmatchRedisKeySetting.getBbSymbolPattern());
        if (null != symbolName2Symbol || symbolName2Symbol.size() > 0) {
            for (Map.Entry<String, String> kv : symbolName2Symbol.entrySet()) {
                String value = kv.getValue();
                if (null != value) {
                    try {
                        BbSymbolBo bbSymbolBo = JSON.parseObject(value, BbSymbolBo.class);
                        if (null != bbSymbolBo.getBbGroupId() && bbmatchSetting.getMatchGroupId().intValue() == bbSymbolBo.getBbGroupId().intValue()) {
                            supportAssetSymbol.add(BbUtil.concatAssetAndSymbol(null, bbSymbolBo.getAsset(), bbSymbolBo.getSymbol()));
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }
        return supportAssetSymbol;
    }

}