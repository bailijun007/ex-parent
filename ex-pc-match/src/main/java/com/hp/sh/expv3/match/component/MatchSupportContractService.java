/**
 * @author 10086
 * @date 2019/12/25
 */
package com.hp.sh.expv3.match.component;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.match.bo.PcContractBo;
import com.hp.sh.expv3.match.config.setting.PcmatchRedisKeySetting;
import com.hp.sh.expv3.match.config.setting.PcmatchSetting;
import com.hp.sh.expv3.match.util.PcUtil;
import com.hp.sh.expv3.match.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class MatchSupportContractService {

    @Autowired
    @Qualifier("metadataRedisUtil")
    private RedisUtil metadataRedisUtil;

    @Autowired
    private PcmatchRedisKeySetting pcmatchRedisKeySetting;

    @Autowired
    private PcmatchSetting pcmatchSetting;

    private Set<String> supportAssetSymbol = new HashSet<>();

    public Set<String> getSupportAssetSymbol(boolean useCache) {

        if (useCache) {
            if (supportAssetSymbol.size() > 0) {
                return supportAssetSymbol;
            }
        }

        Map<String, String> contractName2Contract = metadataRedisUtil.hgetAll(pcmatchRedisKeySetting.getPcContractPattern());
        if (null != contractName2Contract || contractName2Contract.size() > 0) {
            for (Map.Entry<String, String> kv : contractName2Contract.entrySet()) {
                String value = kv.getValue();
                if (null != value) {
                    PcContractBo pcContractBo = JSON.parseObject(value, PcContractBo.class);
                    if (pcmatchSetting.getMatchGroupId() == pcContractBo.getContractGroup()) {
                        supportAssetSymbol.add(PcUtil.concatAssetAndSymbol(null, pcContractBo.getAsset(), pcContractBo.getSymbol()));
                    }
                }
            }
        }
        return supportAssetSymbol;
    }

}
