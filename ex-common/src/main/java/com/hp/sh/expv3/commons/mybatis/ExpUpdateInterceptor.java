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
import com.hp.sh.expv3.base.entity.BaseBizEntity;
import com.hp.sh.expv3.commons.ctx.RequestContext;
import com.hp.sh.expv3.commons.ctx.TxContext;
import com.hp.sh.expv3.component.context.IdGeneratorContext;
import com.hp.sh.expv3.utils.DbDateUtils;

/**
 * 从环境变量取ReqeustId并保存
 * 
 * @author wangjg
 *
 */
@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) })
public class ExpUpdateInterceptor extends AbstractInterceptor {

    private Logger logger = LoggerFactory.getLogger(ExpUpdateInterceptor.class);

    public Object intercept(Invocation invocation) throws Throwable {
    	if(this.isEntity(invocation)){
            Object[] args = invocation.getArgs();
            MappedStatement ms = (MappedStatement) args[0];
            Object entity = args[1];
            
            this.handleEntity(ms, entity);
            
            Object result = invocation.proceed();
            return result;
    	}else{
    		return invocation.proceed();
    	}
    }
    
    void handleEntity(final MappedStatement ms, final Object entity) {
        if (ms.getSqlCommandType() == SqlCommandType.INSERT) {
        	this.insertRequestId(entity);
        	this.setTxId(entity);
        	this.setCreatedTime(entity);
        }
    	
        if (ms.getSqlCommandType() == SqlCommandType.UPDATE) {
        	this.updateRequestId(entity);
        	this.setModifiedTime(entity);
        }
        
    }
    
    private void setCreatedTime(final Object entity){
    	if(entity instanceof BaseBizEntity){
    		BaseBizEntity be = (BaseBizEntity)entity;
    		long time = IdGeneratorContext.getSnowIdTime(entity.getClass().getName(), be.getId());
    		be.setCreated(time);
    		be.setModified(time);
    	}
    }
    
    private void setModifiedTime(final Object entity){
    	if(entity instanceof BaseBizEntity){
			BaseBizEntity be = (BaseBizEntity)entity;
	    	if(be.getModified()==null){
	    		be.setModified(DbDateUtils.now());
	    	}
    	}
    }
    
    private void insertRequestId(final Object entity){
    	Object requestId = RequestContext.getRequestId();
    	if(requestId==null){
    		return;
    	}
    	EntityUtil.AnnoProperty<InsertRequestId> requestIdAnno = EntityUtil.findAnnoProperty(entity.getClass(), InsertRequestId.class);
        if (requestIdAnno!=null) {
        	requestIdAnno.setValue(entity, requestId);
        }
    }
    
    private void updateRequestId(final Object entity){
    	Object requestId = RequestContext.getRequestId();
    	if(requestId==null){
    		return;
    	}
    	EntityUtil.AnnoProperty<UpdateRequestId> requestIdAnno = EntityUtil.findAnnoProperty(entity.getClass(), UpdateRequestId.class);
        if (requestIdAnno!=null) {
        	requestIdAnno.setValue(entity, requestId);
        }
    }
    
    private void setTxId(final Object entity) {
    	Object txId = TxContext.getTxId();
    	if(txId==null){
    		return;
    	}
    	
    	EntityUtil.AnnoProperty<TxId> requestIdAnno = EntityUtil.findAnnoProperty(entity.getClass(), TxId.class);
        if (requestIdAnno!=null) {
        	requestIdAnno.setValue(entity, txId);
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
