package com.dxy.library.util.common.snowflake;

import javax.xml.bind.DatatypeConverter;
import java.lang.management.ManagementFactory;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

/**
 * Twitter_Snowflake
 * 雪花Id生成器
 * @author Twitter
 */
public class SnowflakeId {
    private static final long DEF_WORKER_ID;
    private static final long DEF_DATACENTER_ID;
    private static List<String> hardwareAddresses;
    private static final Random RANDOM = new Random(System.currentTimeMillis());
    private static final SnowflakeIdWorker idWorker;

    static {
        int macPiece = RANDOM.nextInt();
        int processPiece = RANDOM.nextInt();
        try {
            List<String> addresses = getHardwareAddresses();
            StringBuilder buffer = new StringBuilder();
            for (String addr : addresses) {
                buffer.append(addr);
                buffer.append("&");
            }
            macPiece = System.identityHashCode(buffer.toString());
        } catch (Exception ignore) {
        }
        try {
            String process = ManagementFactory.getRuntimeMXBean().getName();
            processPiece = System.identityHashCode(process);
        } catch (Throwable ignore) {
        }
        DEF_WORKER_ID = (long) (processPiece & 31);
        DEF_DATACENTER_ID = (long) (macPiece & 31);
        idWorker = new SnowflakeIdWorker(DEF_WORKER_ID, DEF_DATACENTER_ID);
    }

    private static List<String> getHardwareAddresses() {
        if (hardwareAddresses != null) {
            return hardwareAddresses;
        }
        hardwareAddresses = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();
            if (ifs != null) {
                while (ifs.hasMoreElements()) {
                    NetworkInterface iface = ifs.nextElement();
                    byte[] hardware = iface.getHardwareAddress();
                    if ((hardware != null) && (hardware.length == 6) && (hardware[1] != -1)) {
                        String hardwareAddr = DatatypeConverter.printHexBinary(hardware);
                        hardwareAddresses.add(hardwareAddr);
                    }
                }
            }
        } catch (SocketException ignore) {
        }
        return hardwareAddresses;
    }

    /**
     * 生成雪花Id（18位）
     */
    public static long generate() {
        return idWorker.nextId();
    }
    
}
