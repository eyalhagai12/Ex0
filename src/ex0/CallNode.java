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

    /**
     * Update the dest_floor variable according to the call state
     * (if call state = INIT, GOING2SRC, dest_floor = src, if calls state = GOING2DEST, dest_floor = dest)
     *
     */
    public void update() {
        // set the dest floor according to the call state
        if (call.getState() == 0 || call.getState() == 1) {
            dest_floor = call.getSrc();
        } else if (call.getState() == 2) {
            dest_floor = call.getDest();
        }
    }

    /**
     * Comparison depends on the direction of the call, so that if we want calls
     * that are going down we sort the priority queue by the bigger floor, and if the
     * call is going up we sort it by the lowest floor
     *
     * @param o the CallNode to compare with
     * @return 1 or -1 depending on which is considered "first" in the priority w.r.t the call direction
     */
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
