/**
 * @author zw
 * @date 2019/8/14
 */
package com.hp.sh.expv3.match.component.id;

public class IdUtil {

    public static int getDataCenterId(SnowflakeIdWorker idWorker, int dataCenterId, int serverId, int value) {
        assert dataCenterId <= idWorker.getMaxDataCenterId()
                && serverId <= idWorker.getMaxServerId()
                && value <= idWorker.getMaxIdTypeId()
                && serverId >= 0
                && dataCenterId >= 0
                && value >= 0
                ;
        return dataCenterId;
    }

    public static int getServerId(SnowflakeIdWorker idWorker, int dataCenterId, int serverId, int value) {
        assert dataCenterId <= idWorker.getMaxDataCenterId()
                && serverId <= idWorker.getMaxServerId()
                && value <= idWorker.getMaxIdTypeId()
                && serverId >= 0
                && dataCenterId >= 0
                && value >= 0
                ;
        return serverId;
    }

    public static int getIdType(SnowflakeIdWorker idWorker, int dataCenterId, int serverId, int value) {
        assert dataCenterId <= idWorker.getMaxDataCenterId()
                && serverId <= idWorker.getMaxServerId()
                && value <= idWorker.getMaxIdTypeId()
                && serverId >= 0
                && dataCenterId >= 0
                && value >= 0
                ;
        return value;
    }

    public static SnowflakeIdWorker newIdWorker(int dataCenterBits, int serverBits, int idTypeBits, int sequenceBits, Integer dataCenterId, Integer serverId, Integer idType) {
        return new SnowflakeIdWorker(dataCenterBits, serverBits, idTypeBits, sequenceBits).setId(dataCenterId, serverId, idType);
    }

    public static int getDataCenterId(SnowflakeIdWorker idWorker, long id) {
        assert idWorker.getDataCenterId() == idWorker.getDataCenter(id);
        return Long.valueOf(idWorker.getDataCenterId()).intValue();
    }

    public static int getServerId(SnowflakeIdWorker idWorker, long id) {
        assert idWorker.getServer(id) == idWorker.getServerId();
        return Long.valueOf(idWorker.getServerId()).intValue();
    }

    public static int getIdType(SnowflakeIdWorker idWorker, long id) {
        assert idWorker.getIdType(id) == idWorker.getIdType();
        return idWorker.getIdType(id);
    }

    public static int compare(SnowflakeIdWorker idWorker, long id1, long id2) {
        if (id1 == id2) return 0;
        long t1 = idWorker.getTime(id1);
        long t2 = idWorker.getTime(id2);
        if (t1 > t2) return 1;
        else if (t1 < t2) return -1;
        else {
            int seq1 = idWorker.getSequence(id1);
            int seq2 = idWorker.getSequence(id2);
            return seq1 > seq2 ? 1 : (seq1 < seq2 ? -1 : 0);
        }
    }

}