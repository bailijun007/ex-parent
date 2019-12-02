package com.hp.sh.expv3.config.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;

import com.hp.sh.expv3.config.db.shard.DbShardingAlgorithm;

public class FunShardingBuilder{
	
	public static final String DS_PREFIX = "data-source-";
	
    private StandardShardingStrategyConfiguration dbSharding = new StandardShardingStrategyConfiguration("user_id", new DbShardingAlgorithm());
    private List<String> tableList = new ArrayList<String>();
    private List<DataSource> dsList;
    
    public FunShardingBuilder() {
	}

	public FunShardingBuilder setDsList(List<DataSource> dsList) {
		this.dsList = dsList;
		return this;
	}

	public FunShardingBuilder addTable(String name){
		tableList.add(name);
		return this;
    }
	
	public DataSource build() throws SQLException{
	    Map<String, DataSource> dataSourceMap = new HashMap<>();
	    
	    for(int i=0; i<dsList.size(); i++){
	    	DataSource ds = dsList.get(i);
		    dataSourceMap.put(DS_PREFIX + i , ds);
	    }
	    
	    ShardingRuleConfiguration shardingRuleConfig = this.getShardingConfig();
		DataSource dataSource = ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, new Properties());
	    return dataSource;
	}

	private ShardingRuleConfiguration getShardingConfig(){
	    ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
	    
		for(String table : this.tableList){
			TableRuleConfiguration tableRule = new TableRuleConfiguration(table, DS_PREFIX+"${0.."+(dsList.size()-1)+"}."+table);
			tableRule.setDatabaseShardingStrategyConfig(dbSharding);
			shardingRuleConfig.getTableRuleConfigs().add(tableRule);
		}

		return shardingRuleConfig;
	}
	
}
