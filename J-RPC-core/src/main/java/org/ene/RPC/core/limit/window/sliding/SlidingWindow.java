package org.ene.RPC.core.limit.window.sliding;

import org.ene.RPC.core.limit.window.AbstractWindow;
import org.ene.RPC.core.limit.window.Window;
import sun.misc.Unsafe;

import java.util.concurrent.atomic.AtomicReferenceArray;


public class SlidingWindow extends AbstractWindow implements Window {

    public static class Stat implements AbstractWindow.Stat {

        // 样本窗口数组
        AtomicReferenceArray<SampleWindow> sampleWindowArray;

        SlidingWindowInfo slidingWindowInfo;

        SampleWindowInfo sampleWindowInfo;

        public Stat(SlidingWindowInfo slidingWindowInfo) {
            long length = slidingWindowInfo.getLength();
            int count = slidingWindowInfo.getCount();
            int size = slidingWindowInfo.getSize();
            this.slidingWindowInfo = slidingWindowInfo;
            this.sampleWindowInfo = SampleWindowInfo.create(length / size, count / size);
            this.sampleWindowArray = new AtomicReferenceArray<>(size);
        }

        @Override
        public boolean allowable() {
            return currentSampleWindow().allowable();
        }

        /**
         * 获取时间id，当前时间除以样本窗口的长度
         */
        private long getTimeId(long timeMillis) {
            return timeMillis / sampleWindowInfo.length;
        }

        /**
         * 根据时间id路由到某一个样本窗口
         * 这里使用的是取模法，可以替换为其他算法
         *
         * @param timeId
         * @return
         */
        private int routeSampleWindowIndex(long timeId) {
            return (int) (timeId % sampleWindowArray.length());
        }

        /**
         * 计算请求打在哪一个样本窗口中
         */
        public SampleWindow currentSampleWindow() {
            // 获取当前时间
            long timeMillis = System.currentTimeMillis();
            // 计算当前时间的时间Id
            long timeId = getTimeId(timeMillis);
            // 通过取模的方式获取该时间Id会落在滑动窗口的哪一个样本窗口上
            int sampleWindowIndex = routeSampleWindowIndex(timeId);

            while (true) {
                // 获取到当前时间所在的样本窗口
                SampleWindow sampleWindow = sampleWindowArray.get(sampleWindowIndex);
                // 若当前时间所在样本窗口为null，说明还不存在，需要创建一个
                if (sampleWindow == null) {
                    // 创建一个样本窗口
                    SampleWindow initSampleWindow = new SampleWindow(sampleWindowInfo, timeId);
                    if (sampleWindowArray.compareAndSet(sampleWindowIndex, null, initSampleWindow)) {
                        // 创建成功返回窗口
                        return initSampleWindow;
                    } else {
                        Thread.yield();
                    }
                }
                // 若当前样本窗口的id与旧的样本窗口的id相同
                // 则说明这两个是同一个样本窗口
                else if (timeId == sampleWindow.getTimeId().get()) {
                    return sampleWindow;
                }
                // 若当前样本窗口的id不等于旧的样本窗口的id
                // 则说明旧的样本窗口已经过时了，需要使用cas的方式将旧的样本窗口替换(更新窗口id，并重置count)
                else if (timeId != sampleWindow.getTimeId().get()) {
                    long oldTimeId = sampleWindow.getTimeId().get();
                    // 替换窗口的时间id，模拟滑动的效果
                    if (oldTimeId != timeId && sampleWindow.updateWindowTimeIdCas(oldTimeId, timeId)) {
                        sampleWindow.resetCount();
                        return sampleWindow;
                    } else {
                        Thread.yield();
                    }
                }
            }
        }

    }

}
