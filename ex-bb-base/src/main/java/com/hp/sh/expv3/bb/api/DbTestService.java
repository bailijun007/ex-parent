package com.hp.sh.expv3.bb.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.bb.module.fail.dao.BBMqMsgDAO;
import com.hp.sh.expv3.bb.module.fail.entity.BBMqMsg;
import com.hp.sh.expv3.component.lock.TxIdService;

@Service
@Transactional(rollbackFor=Exception.class)
public class DbTestService {

	@Autowired
	private BBMqMsgDAO bBMqMsgDAO;
	
    @Autowired
    private JdbcTemplate jdbcTemplate;
	
	@Autowired(required=false)
	private TxIdService txIdService;
	
	@Autowired
	private DbTestService self;
	
	@Autowired
	private DataSource dataSource;
	
	long idseq = 0;

	@Transactional(rollbackFor=Exception.class)
	public void save1(BBMqMsg entity){
		bBMqMsgDAO.save(entity);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public Number save2(BBMqMsg entity){
        String sql = "insert into bb_mq_msg ( id,message_id,`tag`,`key`,body,ex_message,method,user_id,asset,symbol,sort_id,created )values( ?,1,'test','1234567890','{\"accountId\":159971958589489152,\"asset\":\"USDT\",\"makerFlag\":1,\"matchTxId\":185064790090089728,\"number\":0.001,\"opponentOrderId\":185064789783874304,\"orderId\":185064599618325248,\"price\":203.14,\"seqId\":185064790916366336,\"symbol\":\"ETH_USDT\",\"tradeId\":185064790090089943,\"tradeTime\":1590394884222}', null,null,1,'USDT','BTC_USDT',1,1234567890 )";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int resRow = jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql,new String[]{"id"});
                ps.setLong(1, entity.getId());
                return ps;
            }
        },keyHolder);
        return keyHolder.getKey();
	}
	
	@Transactional(rollbackFor=Exception.class)
	public Number save3(BBMqMsg entity){
        String sql = "insert into bb_mq_msg (id,message_id,`tag`,`key`,body,ex_message,method,user_id,asset,symbol,sort_id,created)values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int resRow = jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
            	 PreparedStatement ps = prepareStatement(connection, sql);
                ps.setLong(1, entity.getId());
                ps.setString(2, entity.getMessageId());
                ps.setString(3, entity.getTag());
                ps.setString(4, entity.getKey());
                ps.setString(5, entity.getBody());
                ps.setString(6, entity.getExMessage());
                ps.setString(7, entity.getMethod());
                ps.setLong(8, entity.getUserId());
                ps.setString(9, entity.getAsset());
                ps.setString(10, entity.getSymbol());
                ps.setLong(11, entity.getSortId());
                ps.setLong(12, entity.getCreated());
                return ps;
            }
        },keyHolder);
        return keyHolder.getKey();
	}

	@Transactional(rollbackFor=Exception.class)
	public Number save4(BBMqMsg entity){
        String sql = "insert into bb_mq_msg (id,message_id,`tag`,`key`,body,ex_message,method,user_id,asset,symbol,sort_id,created)values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int resRow = jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
            	PreparedStatement ps = prepareStatement(connection, sql);
                ps.setLong(1, entity.getId());
                ps.setString(2, entity.getMessageId());
                ps.setString(3, entity.getTag());
                ps.setString(4, entity.getKey());
                ps.setString(5, entity.getBody());
                ps.setString(6, entity.getExMessage());
                ps.setString(7, entity.getMethod());
                ps.setLong(8, entity.getUserId());
                ps.setString(9, entity.getAsset());
                ps.setString(10, entity.getSymbol());
                ps.setLong(11, entity.getSortId());
                ps.setLong(12, entity.getCreated());
                return ps;
            }
        },keyHolder);
        return keyHolder.getKey();
	}
	
	private Map map = new ConcurrentHashMap();
	
	@Transactional(rollbackFor=Exception.class)
	public Number save5(BBMqMsg entity){
		map.put(entity.getId(), entity);
		return 1;
	}
	
	private PreparedStatement pstmt;
	
	private PreparedStatement prepareStatement(Connection conn, String sql) throws SQLException{
		if(this.pstmt==null){
			this.pstmt = conn.prepareStatement(sql);
		}
		return pstmt;
	}
	
}
