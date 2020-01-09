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
import com.hp.sh.expv3.commons.ctx.TxIdContext;

/**
 * 从环境变量取ReqeustId并保存
 * 
 * @author wangjg
 *
 */
@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) })
public class TxIdInterceptor extends AbstractInterceptor {

    private Logger logger = LoggerFactory.getLogger(TxIdInterceptor.class);

    public Object intercept(Invocation invocation) throws Throwable {
    	if(this.isEntity(invocation)){
            Object[] args = invocation.getArgs();
            MappedStatement ms = (MappedStatement) args[0];
            Object entity = args[1];
            
            this.setTxId(ms, entity);
            
            Object result = invocation.proceed();
            return result;
    	}else{
    		return invocation.proceed();
    	}
    }
    
    private void setTxId(final MappedStatement ms, final Object entity) {
    	Object txId = TxIdContext.getTxId();
    	if(txId==null){
    		return;
    	}
    	
        if (ms.getSqlCommandType() == SqlCommandType.INSERT) {
        	EntityUtil.AnnoProperty<TxId> requestIdAnno = EntityUtil.findAnnoProperty(entity.getClass(), TxId.class);
            if (requestIdAnno!=null) {
            	requestIdAnno.setValue(entity, txId);
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
