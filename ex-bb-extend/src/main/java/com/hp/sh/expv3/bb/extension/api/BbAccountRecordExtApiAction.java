package com.hp.sh.expv3.bb.extension.api;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.bb.extension.error.BbExtCommonErrorCode;
import com.hp.sh.expv3.bb.extension.service.BbAccountRecordExtService;
import com.hp.sh.expv3.bb.extension.vo.BbAccountRecordVo;
import com.hp.sh.expv3.commons.exception.ExException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author BaiLiJun  on 2020/2/14
 */
@RestController
public class BbAccountRecordExtApiAction implements BbAccountRecordExtApi {

    @Autowired
    private BbAccountRecordExtService bbAccountRecordExtService;

    @Override
    public PageResult<BbAccountRecordVo> queryHistory(Long userId, String asset, Integer pageSize, Integer pageNo) {
        if ( pageSize==null||pageNo==null) {
            throw new ExException(BbExtCommonErrorCode.PARAM_EMPTY);
        }
        return bbAccountRecordExtService.queryHistory(userId,asset,pageNo,pageSize);
    }


}
