package cc.xiaoxu.cloud.core.utils.id;

import org.apache.commons.lang3.StringUtils;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * <p>雪花算法</p>
 *
 * @author 小徐
 * @since 2024/4/6 14:07
 */
public class SnowflakeUtils {

    /**
     * 开始时间截 (2020-01-01)
     */
    private final static long START_TIMESTAMP = 1577808000L;

    /**
     * 序列号占用的位数
     */
    private final static long SEQUENCE_BIT = 12L;

    /**
     * 机器标识占用的位数，256个机器
     */
    private final static long MACHINE_BIT = 5L;

    /**
     * 数据中心占用的位数，4个数据中心
     */
    private final static long DATACENTER_BIT = 5L;

    /**
     * 数据中心ID最大值
     */
    private final static long MAX_DATACENTER_NUM = ~(-1L << DATACENTER_BIT);

    /**
     * 机器ID最大值
     */
    private final static long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);

    /**
     * 毫秒内序列的最大值
     */
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);

    /**
     * 机器id左移位数
     */
    private final static long MACHINE_LEFT = SEQUENCE_BIT;

    /**
     * 数据中心id左移位数 17
     */
    private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;

    /**
     * 时间部分向左移动的位数22(雪花算法总长度64,最高位是符号位，正数是0，负数是1，所以id一般是正数，最高位是 1)
     */
    private final static long TIMESTAMP_LEFT_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

    /**
     * 数据中心
     */
    private final long datacenterId;

    /**
     * 机器标识
     */
    private final long machineId;

    /**
     * 序列号
     */
    private long sequence = 0L;

    /**
     * 上一次时间戳
     */
    private long lastTimestamp = -1L;

    public SnowflakeUtils() {
        // 通过当前物理网卡地址获取 datacenterId
        this.datacenterId = getDatacenterId(MAX_DATACENTER_NUM);
        // 物理网卡地址 + jvm 进程 pi 获取 workerId
        this.machineId = getMaxWorkerId(datacenterId, MAX_MACHINE_NUM);
    }

    public SnowflakeUtils(long datacenterId, long machineId) {
        if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
            throw new IllegalArgumentException(
                    "datacenterId can't be greater than " + MAX_DATACENTER_NUM + " or less than 0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException(
                    "machineId can't be greater than " + MAX_MACHINE_NUM + " or less than 0");
        }
        this.datacenterId = datacenterId;
        this.machineId = machineId;
    }

    protected static long getDatacenterId(long maxDatacenterId) {
        long id = 0L;
        try {
            // 获取本机(或者服务器 ip 地址)
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            // 一般不是null会进入else
            if (network == null) {
                id = 1L;
            } else {
                // 获取物理网卡地址
                byte[] mac = network.getHardwareAddress();
                if (null != mac) {
                    id = ((0x000000FF & (long) mac[mac.length - 2])
                            | (0x0000FF00 & (((long) mac[mac.length - 1]) << 8))) >> 6;
                    id = id % (maxDatacenterId + 1);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("获取DatacenterId错误:" + e.getMessage());
        }
        return id;
    }

    /**
     * 获取 maxWorkerId
     */
    protected static long getMaxWorkerId(long datacenterId, long maxWorkerId) {
        StringBuilder pid = new StringBuilder();
        pid.append(datacenterId);
        // 获取 jvm 进程信息
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (StringUtils.isNotBlank(name)) {
            /*
             * 获取进程 PID
             */
            pid.append(name.split("@")[0]);
        }
        /*
         * MAC + PID 的 hashcode 获取 16 个低位
         */
        return (pid.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
    }

    /**
     * 产生下一个ID
     *
     * @return long
     */
    public synchronized long nextId() {
        long currTimestamp = getNewTimestamp();
        if (currTimestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }

        if (currTimestamp == lastTimestamp) {
            // 相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 同一毫秒的序列数已经达到最大
            if (sequence == 0L) {
                // 循环获取多几次，尽可能地避免不可能的可能
                for (int i = 0; i < 100; i++) {
                    currTimestamp = getNextMill();
                    if (currTimestamp != lastTimestamp) {
                        break;
                    }
                }
            }
        } else {
            // 不同毫秒内，序列号置为0
            sequence = 0L;
        }

        lastTimestamp = currTimestamp;

        // 时间戳部分
        return (currTimestamp - START_TIMESTAMP) << TIMESTAMP_LEFT_LEFT
                // 数据中心部分
                | datacenterId << DATACENTER_LEFT
                // 机器标识部分
                | machineId << MACHINE_LEFT
                // 序列号部分
                | sequence;
    }

    private long getNextMill() {
        long mill = getNewTimestamp();
        while (mill <= lastTimestamp) {
            mill = getNewTimestamp();
        }
        return mill;
    }

    private long getNewTimestamp() {
        return System.currentTimeMillis();
    }
}