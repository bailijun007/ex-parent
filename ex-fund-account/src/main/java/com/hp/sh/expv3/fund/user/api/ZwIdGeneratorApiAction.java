package com.hp.sh.expv3.fund.user.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.hupadev.commons.id.IdGenerator;
import com.hp.sh.expv3.config.db.WorkerConfigBuilder;

/**
 * 用户Api
 *
 * @author BaiLiJun  on 2019/12/14
 */
@RestController
public class ZwIdGeneratorApiAction implements ZwIdGeneratorApi {

    @Autowired
    private IdGenerator idGenerator;

	@Override
	public Long getNextId() {
		Long id = idGenerator.nextId(WorkerConfigBuilder.USER);
		return id;
	}

}
