package ch.icclab.sentinel.dao;

public class PingEvent {
    public long eventTime;
    public String status;

    public PingEvent(long time, String stat)
    {
        eventTime = time;
        status = stat;
    }
}
