package com.hp.sh.expv3.component.dbshard;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.ComplexShardingStrategyConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;

public class ExShardingBuilder{
	
	public static final String DS_PREFIX = "data-source-";
	
    private List<DataSource> dataSourceList;
    private String defaultDataSourceName;
    private List<String> dbShardTableNames = new ArrayList<String>();
    
    private Map<String,ExBaseShardingAlgorithm> algorithms = new HashMap<String,ExBaseShardingAlgorithm>();
    
    private TableInfoCache tableInfoCache = new TableInfoCache(){};
    
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
	public ExShardingBuilder addDbShard(String tableName){
		dbShardTableNames.add(tableName);
		return this;
    }

	/**
	 * 添加分表表名
	 * @param tableName
	 * @return
	 */
	public ExShardingBuilder addAssetSubTable(String tableName, String idColumn, String dateColumn){
		TableShardingByDateAsset ts = new TableShardingByDateAsset(idColumn, dateColumn);
		ts.setTableInfoCache(tableInfoCache);
		algorithms.put(tableName, ts);
		return this;
    }

	/**
	 * 添加symbol分表表名
	 * @param tableName
	 * @return
	 */
	public ExShardingBuilder addSymbolSubTable(String tableName, String idColumn, String dateColumn){
		TableShardingByDateSymbol ts = new TableShardingByDateSymbol(idColumn, dateColumn);
		ts.setTableInfoCache(tableInfoCache);
		algorithms.put(tableName, ts);
		return this;
    }

	/**
	 * 添加symbol分表表名
	 * @param tableName
	 * @return
	 */
	public ExShardingBuilder addSymbolSubTable(String tableName, String idColumn, String dateColumn, IdDateShard idDateShard){
		TableShardingByDateSymbol ts = new TableShardingByDateSymbol(idColumn, dateColumn, idDateShard);
		ts.setTableInfoCache(tableInfoCache);
		algorithms.put(tableName, ts);
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
		
		List<String> _tableList = new ArrayList<>();
		_tableList.addAll(dbShardTableNames);
		_tableList.removeAll(this.algorithms.keySet());
		_tableList.addAll(this.algorithms.keySet());
		
		Map<String, TableRuleConfiguration> ruleMap = new HashMap<>();
		
		for(String table : _tableList){
			TableRuleConfiguration tableRule = new TableRuleConfiguration(table, DS_PREFIX+"${0.."+(dataSourceList.size()-1)+"}."+table);
			shardingRuleConfig.getTableRuleConfigs().add(tableRule);
			ruleMap.put(table, tableRule);
		}
	    
		//分库
		for(String table : this.dbShardTableNames){
			TableRuleConfiguration tableRule = ruleMap.get(table);
			tableRule.setDatabaseShardingStrategyConfig(new StandardShardingStrategyConfiguration("user_id", new DbShardingAlgorithm()));
		}
	    
		//分表
		Set<Entry<String, ExBaseShardingAlgorithm>> entrySet = this.algorithms.entrySet();
		for(Entry<String, ExBaseShardingAlgorithm> entry : entrySet){
			String tableName = entry.getKey();
			ExBaseShardingAlgorithm algo = entry.getValue();
			TableRuleConfiguration tableRule = ruleMap.get(tableName);
			tableRule.setTableShardingStrategyConfig(new ComplexShardingStrategyConfiguration(algo.getShardingColumns(), algo));
		}

		return shardingRuleConfig;
	}

	public void setTableInfoCache(TableInfoCache tableInfoCache) {
		this.tableInfoCache = tableInfoCache;
	}
	
}
