package com.hp.sh.expv3.component.id.config;

import com.hp.sh.expv3.component.id.utils.IdBitSetting;

public class SequencConfig {

    private int dataCenterBits;
    private int serverBits;
    private int idTypeBits;
    private int sequenceBits;
    
    private int sequencId;
    
    public SequencConfig() {
		this.dataCenterBits = IdBitSetting.dataCenterBits;
		this.serverBits = IdBitSetting.serverBits;
		this.idTypeBits = IdBitSetting.idTypeBits;
		this.sequenceBits = IdBitSetting.sequenceBits;
	}

	public SequencConfig(int sequencId) {
		this();
		this.sequencId = sequencId;
	}

	public int getDataCenterBits() {
        return dataCenterBits;
    }

    public void setDataCenterBits(int dataCenterBits) {
        this.dataCenterBits = dataCenterBits;
    }

    public int getServerBits() {
        return serverBits;
    }

    public void setServerBits(int serverBits) {
        this.serverBits = serverBits;
    }

    public int getIdTypeBits() {
        return idTypeBits;
    }

    public void setIdTypeBits(int idTypeBits) {
        this.idTypeBits = idTypeBits;
    }

    public int getSequenceBits() {
        return sequenceBits;
    }

    public void setSequenceBits(int sequenceBits) {
        this.sequenceBits = sequenceBits;
    }

	public int getSequencId() {
		return sequencId;
	}

	public void setSequencId(int tableId) {
		this.sequencId = tableId;
	}

}
