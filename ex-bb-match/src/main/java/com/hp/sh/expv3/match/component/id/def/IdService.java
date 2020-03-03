/**
 * @author 10086
 * @date 2019/10/11
 */
package com.hp.sh.expv3.match.component.id.def;

import com.hp.sh.expv3.enums.IdTypeEnum;

public interface IdService {

    long getId(IdTypeEnum idType);

    int getDataCenter(long id);

    int getServer(long id);

    int getIdType(long id);

    long getTimeInMs(long id);

    int getSequence(long id);

}