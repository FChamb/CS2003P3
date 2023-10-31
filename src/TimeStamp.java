/**
  * Simple example of a timestamp.
  *
  * Saleem Bhatti, https://saleem.host.cs.st-andrews.ac.uk/
  * 28 Aug 2019
  *
  */
import java.util.*;
import java.text.*;

public class TimeStamp {
    private Date d;
    private String dateFormat;
    private String dateTimeFormat;
    private SimpleDateFormat simpleDateFormat;
    private SimpleDateFormat simpleDateTimeFormat;
    private String date;
    private String timeDate;

    public TimeStamp() {
        this.d = new Date();
        this.dateFormat = "yyyy-MM-dd_HH-mm-ss.SSS";
        this.dateTimeFormat = "yyyy-MM-dd";
        this.simpleDateFormat = new SimpleDateFormat(dateFormat);
        this.simpleDateTimeFormat = new SimpleDateFormat(dateTimeFormat);
        this.date = simpleDateFormat.format(d);
        this.timeDate = simpleDateTimeFormat.format(d);
    }

    public String getSimpleDateFormat() {
        return this.date;
    }

    public String getTimeDateFormat() {
        return this.timeDate;
    }
}
