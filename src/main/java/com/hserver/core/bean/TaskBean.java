package com.hserver.core.bean;

import lombok.Data;

import java.util.Timer;
import java.util.TimerTask;

@Data
public class TaskBean {
    private Timer timer;
    private TimerTask timerTask;

    public TaskBean(Timer timer, TimerTask timerTask) {
        this.timer = timer;
        this.timerTask = timerTask;
    }

}
