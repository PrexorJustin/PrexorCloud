package me.prexorjustin.prexornetwork.cloud.driver.timer;

import lombok.NoArgsConstructor;
import me.prexorjustin.prexornetwork.cloud.driver.timer.interfaces.ITimerBase;
import me.prexorjustin.prexornetwork.cloud.driver.timer.utils.TimeUtil;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

@NoArgsConstructor
public class TimerBase implements ITimerBase {

    private Timer timer;

    @Override
    public void schedule(TimerTask runnable, Integer time, TimeUtil timeUtil) {
        this.timer = new Timer();

        if (timeUtil == TimeUtil.SECONDS) timer.schedule(runnable, time * 1000);
        else if (timeUtil == TimeUtil.MINUTES) timer.schedule(runnable, time * 60 * 1000);
        else if (timeUtil == TimeUtil.HOURS) timer.schedule(runnable, time * 60 * 60 * 1000);
        else if (timeUtil == TimeUtil.MILLISECONDS) timer.schedule(runnable, time);
    }

    @Override
    public void scheduleAsync(TimerTask runnable, Integer time, TimeUtil timeUtil) {
        CompletableFuture.runAsync(() -> schedule(runnable, time, timeUtil));
    }

    @Override
    public void schedule(TimerTask runnable, Integer time, Integer secondTime, TimeUtil timeUtil) {
        timer = new Timer();
        if (timeUtil == TimeUtil.SECONDS)
            timer.schedule(runnable, time * 1000, secondTime * 1000);
        else if (timeUtil == TimeUtil.MINUTES)
            timer.schedule(runnable, time * 60 * 1000, secondTime * 60 * 1000);
        else if (timeUtil == TimeUtil.HOURS)
            timer.schedule(runnable, time * 60 * 60 * 1000, secondTime * 60 * 60 * 1000);
        else if (timeUtil == TimeUtil.MILLISECONDS)
            timer.schedule(runnable, time, secondTime);
    }

    @Override
    public void scheduleAsync(TimerTask runnable, Integer time, Integer secondTime, TimeUtil timeUtil) {
        CompletableFuture.runAsync(() -> this.schedule(runnable, time, secondTime, timeUtil));
    }

    @Override
    public boolean isCanceled() {
        return this.timer == null;
    }

    @Override
    public void cancel() {
        this.timer.cancel();
        this.timer = null;
    }
}
