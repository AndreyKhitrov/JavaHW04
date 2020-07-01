package ru.mts;

import com.sun.management.GarbageCollectionNotificationInfo;

import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

public class MyApp {
    public static void main(String... args) throws Exception {
        System.out.println("Starting pid: " + ManagementFactory.getRuntimeMXBean().getName());
        switchOnMonitoring();
        int ind = 1;
        int loopCounter = 10000;
        long beginTime = System.currentTimeMillis();
        System.out.println(Integer.toString(ind));
        ArrayList<Object> spisok = new ArrayList<>();
        try {
            while (ind < 100000) {
                for (int i = 1; i < loopCounter; i++) {
                    spisok.add(new Object());
                    if (i%2==0){
                        spisok.remove(spisok.size()-1);
                    }
                }
                System.out.println(spisok.size());
                Thread.sleep(1); //90
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e);;
        }
        finally {
            System.out.println("time:" + (System.currentTimeMillis() - beginTime) / 1000);
        }
    }

    private static void switchOnMonitoring() {
        List<GarbageCollectorMXBean> gcbeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean gcbean : gcbeans) {
            System.out.println("GC name:" + gcbean.getName());
            NotificationEmitter emitter = (NotificationEmitter) gcbean;
            NotificationListener listener = (notification, handback) -> {
                if (notification.getType().equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {
                    GarbageCollectionNotificationInfo info = GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());
                    String gcName = info.getGcName();
                    String gcAction = info.getGcAction();
                    String gcCause = info.getGcCause();

                    long startTime = info.getGcInfo().getStartTime();
                    long duration = info.getGcInfo().getDuration();

                    System.out.println("start:" + startTime + " Name:" + gcName + ", action:" + gcAction + ", gcCause:" + gcCause + "(" + duration + " ms)");
                }
            };
            emitter.addNotificationListener(listener, null, null);
        }
    }

}

