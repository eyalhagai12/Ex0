package ex0;

public class CallNode implements Comparable<CallNode> {
    static int counter = 0;
    private CallForElevator call;
    private int dest_floor;
    private boolean up;
    private int id;

    public CallNode(CallForElevator call, boolean up) {
        this.call = call;
        id = counter++;
        update();
        this.up = up;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CallForElevator getCall() {
        return call;
    }

    public void setCall(CallForElevator call) {
        this.call = call;
    }

    public int getDest_floor() {
        return dest_floor;
    }

    public void setDest_floor(int dest_floor) {
        this.dest_floor = dest_floor;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public void update() {
        // set the dest floor according to the call state
        if (call.getState() == 0 || call.getState() == 1) {
            dest_floor = call.getSrc();
        } else if (call.getState() == 2) {
            dest_floor = call.getDest();
        }
    }

    @Override
    public int compareTo(CallNode o) {
        if (up) {
            if (this.dest_floor > o.getDest_floor()) {
                return 1;
            } else {
                return -1;
            }
        } else {
            if (this.dest_floor > o.getDest_floor()) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}
