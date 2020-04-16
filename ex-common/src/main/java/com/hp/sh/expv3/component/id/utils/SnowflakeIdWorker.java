/**
 * @author corleone
 * @date 2018/7/6 0006
 */
package com.hp.sh.expv3.component.id.utils;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

/**
 * Twitter_Snowflake<br>
 * SnowFlake的结构如下(每部分用-分开):<br>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0(毫秒-41) - dataCenterBits - serverBits - idTypeBits - sequenceBits <br>
 * 1位标识，由于long基本类型在Java中是带符号的，最高位是符号位，正数是0，负数是1，所以id一般是正数，最高位是0<br>
 * 41位时间截(毫秒级)，注意，41位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截 - 开始时间截)
 * 得到的值），这里的的开始时间截，一般是我们的id生成器开始使用的时间，由我们程序来指定的（如下下面程序IdWorker类的startTime属性）。41位的时间截，可以使用69年，年T = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69<br>
 * dataCenterBits 位的数据中心位<br>
 * serverBits     位的服务实例位<br>
 * idTypeBits     位的对象Id位<br>
 * sequenceBits   位的序列位，毫秒内的计数<br>
 * dataCenterBits + serverIdBits + objectIdBits + sequenceBits = 22
 * 加起来刚好64位，为一个Long型。<br>
 * SnowFlake的优点是，整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞(由idType和serverId作区分)，并且效率较高。
 */
public class SnowflakeIdWorker {

    public static final int maxBits = 22;

    // ==============================Fields===========================================
    /**
     * 开始时间截 (2019-01-01)
     */
    public static final long twepoch = 1546272000000L;

    // id field
    /**
     * 数据中心Id
     */
    private int dataCenterId;
    /**
     * 服务实例ID
     */
    private int serverId;
    /**
     * id类型
     */
    private int idType;


    // bits field
    /**
     * 数据中心位
     */
    private int dataCenterBits;
    /**
     * 服务实例所占的位数
     */
    private int serverBits;
    /**
     * id类型所占的位数
     */
    private int idTypeBits;
    /**
     * 序列在id中占的位数
     */
    private int sequenceBits;

    // max field
    /**
     * 支持的最大数据中心位 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
     */
    private long maxDataCenterId;
    /**
     * 支持的最大服务实例 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
     */
    private long maxServerId;
    /**
     * 支持的最大Id类型
     */
    private long maxIdTypeId;

    // shift field
    /**
     * 数据中心Id向左移位数
     */
    private int dataCenterIdShift;
    /**
     * 服务实例ID向左移位数
     */
    private int serverIdShift;
    /**
     * ID类型Id向左移位数
     */
    private int IdTypeShift;
    /**
     * 时间截向左移位数
     */
    private int timestampLeftShift;

    /**
     * 生成序列的掩码
     */
    private long sequenceMask;
    /**
     * 毫秒内序列
     */
    private long sequence = 0;
    /**
     * 上次生成ID的时间截
     */
    private long lastTimestamp = -1L;

    public static long getTwepoch() {
        return twepoch;
    }

    public int getDataCenterId() {
        return dataCenterId;
    }

    public int getServerId() {
        return serverId;
    }

    public int getIdType() {
        return idType;
    }

    public int getDataCenterBits() {
        return dataCenterBits;
    }

    public int getServerBits() {
        return serverBits;
    }

    public int getIdTypeBits() {
        return idTypeBits;
    }

    public int getSequenceBits() {
        return sequenceBits;
    }

    public long getMaxDataCenterId() {
        return maxDataCenterId;
    }

    public long getMaxServerId() {
        return maxServerId;
    }

    public long getMaxIdTypeId() {
        return maxIdTypeId;
    }

    public int getDataCenterIdShift() {
        return dataCenterIdShift;
    }

    public int getServerIdShift() {
        return serverIdShift;
    }

    public int getIdTypeShift() {
        return IdTypeShift;
    }

    public int getTimestampLeftShift() {
        return timestampLeftShift;
    }

    public long getLastTimestamp() {
        return lastTimestamp;
    }
//==============================Constructors=====================================

    /**
     * 构造函数
     *
     * @param dataCenterBits 数据中心位数
     * @param serverBits     服务实例位数
     * @param idTypeBits     Id类位数
     * @param sequenceBits   同一毫秒下序列位数
     */
    public SnowflakeIdWorker(int dataCenterBits, int serverBits, int idTypeBits, int sequenceBits) {
        if (maxBits != dataCenterBits + serverBits + idTypeBits + sequenceBits) {
            throw new IllegalArgumentException(String.format("dataCenterBits + serverBits + idTypeBits should equals 22,dataCenterBits %d, serverBits %d, idTypeBits %d", dataCenterBits, serverBits, idTypeBits));
        }
        this.dataCenterBits = dataCenterBits;
        this.serverBits = serverBits;
        this.idTypeBits = idTypeBits;
        this.sequenceBits = sequenceBits;

        this.maxDataCenterId = getMask(dataCenterBits);
        this.maxServerId = getMask(serverBits);
        this.maxIdTypeId = getMask(idTypeBits);

        // time - dataCenterBits - serverBits - idTypeBits - sequenceBits
        this.IdTypeShift = sequenceBits;
        this.serverIdShift = idTypeBits + sequenceBits;
        this.dataCenterIdShift = serverBits + idTypeBits + sequenceBits;
        this.timestampLeftShift = dataCenterBits + serverBits + idTypeBits + sequenceBits;

        this.sequenceMask = getMask(sequenceBits);
    }

    public SnowflakeIdWorker setId(int dataCenterId, int serverId, int idType) {

        this.dataCenterId = dataCenterId;
        this.serverId = serverId;
        this.idType = idType;

        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("data center Id can't be greater than %d or less than 0", maxDataCenterId));
        }
        if (serverId > maxServerId || serverId < 0) {
            throw new IllegalArgumentException(String.format("server Id can't be greater than %d or less than 0", maxServerId));
        }
        if (idType > maxIdTypeId || idType < 0) {
            throw new IllegalArgumentException(String.format("id type can't be greater than %d or less than 0", maxIdTypeId));
        }

        return this;
    }

    private long getMask(long num) {
        return -1L ^ (-1L << num);
    }


    // ==============================Methods==========================================

    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            //毫秒内序列溢出
            if (sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        //时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }

        //上次生成ID的时间截
        lastTimestamp = timestamp;

        //移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - twepoch) << timestampLeftShift) //
                | (dataCenterId << dataCenterIdShift) //
                | (serverId << serverIdShift) //
                | (idType << IdTypeShift) //
                | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }


    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }

    // get part long format
    public int getSequence(long id) {
        return (int) (id & sequenceMask);
    }

    public long getTime(long id) {
        return twepoch + (id >> timestampLeftShift);
    }

    public static long getTimeInMs(long id) {
        return twepoch + (id >> maxBits);
    }

    public int getDataCenter(long id) {
        return (int) ((id >> dataCenterIdShift) & maxDataCenterId);
    }

    public int getServer(long id) {
        return (int) ((id >> serverIdShift) & maxServerId);
    }

    public int getIdType(long id) {
        return (int) ((id >> IdTypeShift) & maxIdTypeId);
    }

    // get part 64 bit string format

    public String getId64String(long id) {
        return StringUtils.replace(String.format("%64s", Long.toBinaryString(id)), " ", "0");
    }

    public String getTime64String(long id) {
        return StringUtils.replace(String.format("%64s", Long.toBinaryString((id >> timestampLeftShift) << timestampLeftShift)), " ", "0");
    }

    public String getDataCenter64String(long id) {
        return StringUtils.replace(String.format("%64s", Long.toBinaryString(getDataCenter(id) << dataCenterIdShift)), " ", "0");
    }

    public String getServer64String(long id) {
        return StringUtils.replace(String.format("%64s", Long.toBinaryString(getServer(id) << serverIdShift)), " ", "0");
    }

    public String getIdType64String(long id) {
        return StringUtils.replace(String.format("%64s", Long.toBinaryString(getIdType(id) << IdTypeShift)), " ", "0");
    }

    public String getSequence64String(long id) {
        return StringUtils.replace(String.format("%64s", Long.toBinaryString(getSequence(id))), " ", "0");
    }

    //==============================Test=============================================

    /**
     * 测试
     */
    public static void main(String[] args) {
        long now = System.currentTimeMillis();
        int size = 0;
        Integer x = null;
        SnowflakeIdWorker idWorker = new SnowflakeIdWorker(2, 5, 8, 7).setId(0, 1, 0);
        long time = idWorker.getTimeInMs(170584864708395008L);
        System.out.println(new Date(time).toLocaleString());
        
        System.out.println(idWorker.nextId());
        long sid = idWorker.getServer(170587110762381312L);
        System.out.println(sid);
        

    }
}