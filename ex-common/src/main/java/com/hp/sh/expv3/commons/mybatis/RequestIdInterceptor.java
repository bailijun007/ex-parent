package com.hp.sh.expv3.commons.mybatis;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gitee.hupadev.commons.mybatis.AbstractInterceptor;
import com.gitee.hupadev.commons.mybatis.util.EntityUtil;
import com.hp.sh.expv3.commons.web.RequestContext;

/**
 * 从环境变量取ReqeustId并保存
 * 
 * @author wangjg
 *
 */
@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) })
public class RequestIdInterceptor extends AbstractInterceptor {

    private Logger logger = LoggerFactory.getLogger(RequestIdInterceptor.class);

    public Object intercept(Invocation invocation) throws Throwable {
    	if(this.isEntity(invocation)){
            Object[] args = invocation.getArgs();
            MappedStatement ms = (MappedStatement) args[0];
            Object entity = args[1];
            
            this.setRequestId(ms, entity);
            
            Object result = invocation.proceed();
            return result;
    	}else{
    		return invocation.proceed();
    	}
    }
    
    private void setRequestId(final MappedStatement ms, final Object entity) {
    	Object requestId = RequestContext.getRequestId();
    	if(requestId==null){
    		return;
    	}
    	
        if (ms.getSqlCommandType() == SqlCommandType.INSERT) {
        	EntityUtil.AnnoProperty<InsertRequestId> requestIdAnno = EntityUtil.findAnnoProperty(entity.getClass(), InsertRequestId.class);
            if (requestIdAnno!=null) {
            	requestIdAnno.setValue(entity, requestId);
            }
        }
    	
        if (ms.getSqlCommandType() == SqlCommandType.UPDATE) {
        	EntityUtil.AnnoProperty<UpdateRequestId> requestIdAnno = EntityUtil.findAnnoProperty(entity.getClass(), UpdateRequestId.class);
            if (requestIdAnno!=null) {
            	requestIdAnno.setValue(entity, requestId);
            }
        }
        
    }
    
    private boolean isEntity(Invocation invocation){
    	Object[] args = invocation.getArgs();
    	if(args.length!=2){
    		return false;
    	}
    	Object entity = args[1];
    	if(entity==null || entity.getClass().isPrimitive()){
    		return false;
    	}
    	return true;
    }

}
