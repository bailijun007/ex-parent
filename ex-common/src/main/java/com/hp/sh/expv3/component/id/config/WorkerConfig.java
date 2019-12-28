package com.hp.sh.expv3.component.id.config;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

public class WorkerConfig {
	private int dataCenterId;
	
	private int serverId;
	
	private List<SequencConfig> sequencConfigs;
	
	private List<Pair<String, Integer>> seqIdPairs;

	public int getDataCenterId() {
		return dataCenterId;
	}

	public void setDataCenterId(int dataCenterId) {
		this.dataCenterId = dataCenterId;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public List<SequencConfig> getSequencConfigs() {
		return sequencConfigs;
	}

	public void setSequencConfigs(List<SequencConfig> sequencConfigs) {
		this.sequencConfigs = sequencConfigs;
	}

	public List<Pair<String, Integer>> getSeqIdPairs() {
		return seqIdPairs;
	}

	public void setSeqIdPairs(List<Pair<String, Integer>> seqIdPairs) {
		this.seqIdPairs = seqIdPairs;
	}

}
