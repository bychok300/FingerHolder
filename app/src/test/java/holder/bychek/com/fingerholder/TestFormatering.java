package holder.bychek.com.fingerholder;

import org.junit.Test;

import holder.bychek.com.fingerholder.Utils.TimeUtils;

public class TestFormatering {

    @Test
    public void secToStr() {
        System.out.println(TimeUtils.secondsToTime(-1));
        System.out.println(TimeUtils.secondsToTime(1));
        System.out.println(TimeUtils.secondsToTime(0));
        System.out.println(TimeUtils.secondsToTime(61));
        System.out.println(TimeUtils.secondsToTime(555));
        System.out.println(TimeUtils.secondsToTime(666));
        System.out.println(TimeUtils.secondsToTime(777));
        System.out.println(TimeUtils.secondsToTime(888));
        System.out.println(TimeUtils.secondsToTime(999));
        System.out.println(TimeUtils.secondsToTime(111));
        System.out.println(TimeUtils.secondsToTime(5555));
        System.out.println(TimeUtils.secondsToTime(10000));
        System.out.println(TimeUtils.secondsToTime(100000));
        System.out.println(TimeUtils.secondsToTime(10000000));

    }
    @Test
    public void strToSec(){

        System.out.println(TimeUtils.timeToSeconds("00:00:00"));
        System.out.println(TimeUtils.timeToSeconds("00:00:01"));
        System.out.println(TimeUtils.timeToSeconds("00:00:00"));
        System.out.println(TimeUtils.timeToSeconds("00:01:01"));
        System.out.println(TimeUtils.timeToSeconds("00:09:15"));
        System.out.println(TimeUtils.timeToSeconds("00:11:06"));
        System.out.println(TimeUtils.timeToSeconds("00:12:57"));
        System.out.println(TimeUtils.timeToSeconds("00:14:48"));
        System.out.println(TimeUtils.timeToSeconds("00:16:39"));
        System.out.println(TimeUtils.timeToSeconds("00:01:51"));
        System.out.println(TimeUtils.timeToSeconds("01:32:35"));
        System.out.println(TimeUtils.timeToSeconds("02:46:40"));
        System.out.println(TimeUtils.timeToSeconds("27:46:40"));
        System.out.println(TimeUtils.timeToSeconds("2777:46:40"));
    }

    @Test
    public void sumOfTwoDates(){
        String rs = TimeUtils.sumOfTwoTime("00:01:01","01:10:01");
        System.out.println(rs);
    }

    @Test
    public void calculateFiveMinutes(){
        long rs = TimeUtils.calculateHomMuchFiveMinutesInTime(TimeUtils.timeToSeconds("01:13:00"));
        System.out.println(rs);
    }

}
