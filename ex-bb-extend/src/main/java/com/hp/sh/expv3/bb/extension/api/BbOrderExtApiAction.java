package com.hp.sh.expv3.bb.extension.api;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.bb.extension.error.BbExtCommonErrorCode;
import com.hp.sh.expv3.bb.extension.service.BbOrderExtService;
import com.hp.sh.expv3.bb.extension.vo.BbOrderVo;
import com.hp.sh.expv3.commons.exception.ExException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author BaiLiJun  on 2020/2/14
 */
@RestController
public class BbOrderExtApiAction implements BbOrderExtApi {

    @Autowired
    private BbOrderExtService bbOrderExtService;

    @Override
    public PageResult<BbOrderVo> queryAllBbOrederHistory(Long userId, String asset, Integer pageSize, Integer pageNo) {
        if ( pageSize==null||pageNo==null) {
            throw new ExException(BbExtCommonErrorCode.PARAM_EMPTY);
        }
        return bbOrderExtService.queryAllBbOrederHistory(userId,asset,pageNo,pageSize);
    }
}
