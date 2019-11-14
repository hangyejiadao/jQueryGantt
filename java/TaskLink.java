/**
 * @Author zwh
 * @Date 2019/11/11 11:23
 **/
public class TaskLink {
    private Task from;
    private Task to;
    private int lag;


    public TaskLink(Task from, Task to, int lag) {
        this.from = from;
        this.to = to;
        this.lag = lag;
    }

    public Task getFrom() {
        return from;
    }

    public void setFrom(Task from) {
        this.from = from;
    }

    public Task getTo() {
        return to;
    }

    public void setTo(Task to) {
        this.to = to;
    }

    public int getLag() {
        return lag;
    }

    public void setLag(int lag) {
        this.lag = lag;
    }
}
