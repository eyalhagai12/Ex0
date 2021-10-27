package ex0;

public class Node implements Comparable<Node>{
    private CallForElevator call;
    private double time_for_exec;

    public Node(CallForElevator call, double time){
        this.call = call;
        this.time_for_exec = time;
    }

    public CallForElevator getCall() {
        return call;
    }

    public void setCall(CallForElevator call) {
        this.call = call;
    }

    public double getTime_for_exec() {
        return time_for_exec;
    }

    public void setTime_for_exec(double time_for_exec) {
        this.time_for_exec = time_for_exec;
    }

    @Override
    public int compareTo(Node o) {
        if (o.getTime_for_exec() > time_for_exec){
            return 1;
        }
        else if(o.time_for_exec < time_for_exec){
            return -1;
        }

        return 0;
    }
}
