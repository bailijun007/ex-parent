/**
 * @author 10086
 * @date 2019/10/11
 */
package com.hp.sh.expv3.match.component.id.def;

import com.hp.sh.expv3.match.util.IdTypeEnum;

public interface IdService {

    long getId(IdTypeEnum idType);

}