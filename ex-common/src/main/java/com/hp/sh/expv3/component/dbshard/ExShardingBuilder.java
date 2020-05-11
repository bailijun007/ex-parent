package com.hp.sh.expv3.component.dbshard;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.ComplexShardingStrategyConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;

import com.hp.sh.expv3.component.dbshard.DbShardingAlgorithm;
import com.hp.sh.expv3.component.dbshard.TableShardingByDateAsset;
import com.hp.sh.expv3.component.dbshard.TableShardingByDateSymbol;

public class ExShardingBuilder{
	
	public static final String DS_PREFIX = "data-source-";
	
    private List<DataSource> dataSourceList;
    private String defaultDataSourceName;
    private List<String> subDBTableNames = new ArrayList<String>();
    private List<String> assetSubTableNames = new ArrayList<String>();
    private List<String> symbolSubTableNames = new ArrayList<String>();
    
    public ExShardingBuilder() {
	}

	public ExShardingBuilder setDataSourceList(List<DataSource> dsList) {
		this.dataSourceList = dsList;
		return this;
	}

	/**
	 * 添加分库表名
	 * @param tableName
	 * @return
	 */
	public ExShardingBuilder addSubDBTableName(String tableName){
		subDBTableNames.add(tableName);
		return this;
    }

	/**
	 * 添加分表表名
	 * @param tableName
	 * @return
	 */
	public ExShardingBuilder addAssetSubTableName(String tableName){
		assetSubTableNames.add(tableName);
		return this;
    }

	/**
	 * 添加symbol分表表名
	 * @param tableName
	 * @return
	 */
	public ExShardingBuilder addSymbolSubTableName(String tableName){
		symbolSubTableNames.add(tableName);
		return this;
    }
	
	public DataSource build() throws SQLException{
	    Map<String, DataSource> dataSourceMap = new HashMap<>();
	    
	    for(int i=0; i<dataSourceList.size(); i++){
	    	DataSource ds = dataSourceList.get(i);
		    dataSourceMap.put(DS_PREFIX + i , ds);
	    }
	    
	    ShardingRuleConfiguration shardingRuleConfig = this.getShardingConfig();
		DataSource dataSource = ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, new Properties());
	    return dataSource;
	}

	private ShardingRuleConfiguration getShardingConfig(){
	    ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
		shardingRuleConfig.setDefaultDataSourceName(defaultDataSourceName);
		
		List<String> tableList = new ArrayList<>();
		tableList.addAll(subDBTableNames);
		tableList.removeAll(assetSubTableNames);
		tableList.addAll(assetSubTableNames);
		
		tableList.removeAll(symbolSubTableNames);
		tableList.addAll(symbolSubTableNames);
		
		Map<String, TableRuleConfiguration> ruleMap = new HashMap<>();
		
		for(String table : tableList){
			TableRuleConfiguration tableRule = new TableRuleConfiguration(table, DS_PREFIX+"${0.."+(dataSourceList.size()-1)+"}."+table);
			shardingRuleConfig.getTableRuleConfigs().add(tableRule);
			ruleMap.put(table, tableRule);
		}
	    
		for(String table : this.subDBTableNames){
			TableRuleConfiguration tableRule = ruleMap.get(table);
			tableRule.setDatabaseShardingStrategyConfig(new StandardShardingStrategyConfiguration("user_id", new DbShardingAlgorithm()));
		}
	    
		for(String table : this.assetSubTableNames){
			TableRuleConfiguration tableRule = ruleMap.get(table);
			ComplexShardingStrategyConfiguration strategyConfig = new ComplexShardingStrategyConfiguration("asset,created,id", new TableShardingByDateAsset());
			tableRule.setTableShardingStrategyConfig(strategyConfig);
		}
	    
		for(String table : this.symbolSubTableNames){
			TableRuleConfiguration tableRule = ruleMap.get(table);
			ComplexShardingStrategyConfiguration strategyConfig = new ComplexShardingStrategyConfiguration("asset,symbol,id,created,trade_time", new TableShardingByDateSymbol());
			tableRule.setTableShardingStrategyConfig(strategyConfig);
		}

		return shardingRuleConfig;
	}
	
}
