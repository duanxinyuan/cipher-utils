import java.util.Date;

/**
 * @author duanxinyuan
 * 2018/8/6 12:56
 */
public class GameInfo {

    /**
     * name : 英雄联盟
     * level : 铂金1
     * registYear : 2012
     * registTime : 2012-06-06 12:12:12
     */

    private String name;
    private String level;
    private int registYear;
    private Date registTime;

    public String getName() { return name;}

    public void setName(String name) { this.name = name;}

    public String getLevel() { return level;}

    public void setLevel(String level) { this.level = level;}

    public int getRegistYear() { return registYear;}

    public void setRegistYear(int registYear) { this.registYear = registYear;}

    public Date getRegistTime() { return registTime;}

    public void setRegistTime(Date registTime) { this.registTime = registTime;}
}
