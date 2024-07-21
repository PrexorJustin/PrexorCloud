package me.prexorjustin.prexornetwork.cloud.driver.timer.interfaces;

import me.prexorjustin.prexornetwork.cloud.driver.timer.utils.TimeUtil;

import java.util.TimerTask;

public interface ITimerBase {

    void schedule(TimerTask runnable, Integer time, TimeUtil timeUtil);

    void scheduleAsync(TimerTask runnable, Integer time, TimeUtil timeUtil);

    void schedule(TimerTask runnable, Integer time, Integer secondTime, TimeUtil timeUtil);

    void scheduleAsync(TimerTask runnable, Integer time, Integer secondTime, TimeUtil timeUtil);

    boolean isCanceled();

    void cancel();

}
