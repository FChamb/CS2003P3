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

    /**
     * TimeStamp is taken from the CS2003 P3 practical with some slight changes to
     * make application use simpler. There are now two simple date formats. One for
     * with the date and time and one for just with the date.
     */
    public TimeStamp() {
        this.d = new Date();
        this.dateFormat = "yyyy-MM-dd_HH-mm-ss.SSS";
        this.dateTimeFormat = "yyyy-MM-dd";
        this.simpleDateFormat = new SimpleDateFormat(dateFormat);
        this.simpleDateTimeFormat = new SimpleDateFormat(dateTimeFormat);
        this.date = simpleDateFormat.format(d);
        this.timeDate = simpleDateTimeFormat.format(d);
    }

    /**
     * Getter for simple date format.
     * @return current date
     */
    public String getSimpleDateFormat() {
        return this.date;
    }

    /**
     * Getter for simple time dat format.
     * @return current time date
     */
    public String getSimpleTimeDateFormat() {
        return this.timeDate;
    }
}
