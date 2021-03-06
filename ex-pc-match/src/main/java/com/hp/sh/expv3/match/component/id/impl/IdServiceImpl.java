/**
 * @author 10086
 * @date 2019/10/11
 */
package com.hp.sh.expv3.match.component.id.impl;

import com.hp.sh.expv3.match.component.id.SnowflakeIdWorker;
import com.hp.sh.expv3.match.component.id.def.IdService;
import com.hp.sh.expv3.match.enums.IdTypeEnum;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class IdServiceImpl implements IdService, ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public int getDataCenter(long id) {
        return idType2IdWorker.get(IdTypeEnum.COMMON).getDataCenter(id);
    }

    @Override
    public int getServer(long id) {
        return idType2IdWorker.get(IdTypeEnum.COMMON).getServer(id);
    }

    @Override
    public int getIdType(long id) {
        return idType2IdWorker.get(IdTypeEnum.COMMON).getIdType(id);
    }

    @Override
    public long getTimeInMs(long id) {
        return idType2IdWorker.get(IdTypeEnum.COMMON).getTime(id);
    }

    @Override
    public int getSequence(long id) {
        return idType2IdWorker.get(IdTypeEnum.COMMON).getServer(id);
    }

    Map<Integer, SnowflakeIdWorker> idType2IdWorker = new HashMap<>(32);

    @PostConstruct
    private void init() {
        Collection<SnowflakeIdWorker> idWorkers = this.applicationContext.getBeansOfType(SnowflakeIdWorker.class, true, true).values();
        if (null != idWorkers) {
            for (SnowflakeIdWorker idWorker : idWorkers) {
                idType2IdWorker.put(Long.valueOf(idWorker.getIdType()).intValue(), idWorker);
            }
        }
    }

    @Override
    public long getId(IdTypeEnum idType) {
        return idType2IdWorker.get(idType.value).nextId();
    }

}