package ex0.algo;

import java.util.PriorityQueue;
import java.util.ArrayList;

import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;
import ex0.CallNode;

public class Algo1 implements ElevatorAlgo {

    private Building building;
    private PriorityQueue<CallNode>[] up_queues;
    private PriorityQueue<CallNode>[] down_queues;
    private int num_of_elevators;

    public Algo1(Building b) {
        building = b; // save the building
        num_of_elevators = building.numberOfElevetors(); // save number of elevators

        up_queues = new PriorityQueue[num_of_elevators]; // initiate priority queue array for up calls
        down_queues = new PriorityQueue[num_of_elevators]; // initiate priority queue array for down calls

        // initiate actual queues to both arrays
        for (int i = 0; i < num_of_elevators; ++i) {
            up_queues[i] = new PriorityQueue<CallNode>();
            down_queues[i] = new PriorityQueue<CallNode>();
        }

    }

    @Override
    public Building getBuilding() {
        return building;
    }

    @Override
    public String algoName() {
        return "Algo1";
    }

    @Override
    public int allocateAnElevator(CallForElevator c) {
        int i;
        boolean free_elevator_found = false;

        // allocate to a free elevator if there is one
        for (i = 0; i < num_of_elevators; i++) {
            if (up_queues[i].isEmpty() && down_queues[i].isEmpty()) {
                free_elevator_found = true;
                break;
            }
        }

        // if no free elevator found, check if the call was made in the way of one of the elevators
        if (!free_elevator_found) {
            for (i = 0; i < num_of_elevators; i++) { // all elevators are busy
                Elevator elevator = building.getElevetor(i);

                if (elevator.getState() == Elevator.UP) { // elevator goes up

                    if (elevator.getPos() <= c.getSrc()) // elevator is below call's source
                        break;
                    else continue;
                }

                if (elevator.getState() == Elevator.DOWN) { // elevator goes down

                    if (elevator.getPos() >= c.getSrc()) // elevator is above call's source
                        break;
                    else continue;
                }
            }
        }

        /*
        here we should add the case where all elevators go to the same direction
        and a call not in their route has been called, so we need to get the one
        who's the shortest path to it when it ends his calls
         */
        if (i >= 10){
            for (i = 0; i < num_of_elevators; ++i){

            }
        }

        // add to the queues according to the call direction
        if (c.getType() == CallForElevator.UP) {
            CallNode call = new CallNode(c, true);
            up_queues[i].add(call);
        } else {
            CallNode call = new CallNode(c, false);
            down_queues[i].add(call);
        }

        return i;
    }

    @Override
    public void cmdElevator(int elev) {
        Elevator elevator = building.getElevetor(elev); // get relevant elevator

        /* get relevant elevator queue for convenience */
        PriorityQueue<CallNode> queue;
        if (elevator.getState() == Elevator.UP) { // if elevator is going up
            queue = up_queues[elev];
        } else if (elevator.getState() == Elevator.DOWN) { // if elevator is going down
            queue = down_queues[elev];
        } else { // if elevator is not moving
            if (up_queues[elev].size() > down_queues[elev].size()) { // decide by the amount of calls in each priority queue
                queue = up_queues[elev];
            } else {
                queue = down_queues[elev];
            }
        }


        /* check which calls to execute */
        if(!queue.isEmpty()){
            // update the first call
            CallNode call = queue.peek();
            call.update();

            // remove first and return it to sort the priority queue
            queue.remove(call);
            queue.add(call);

            // get the first one again (in case the queue changed)
            call = queue.peek();

            // determine what to do with this call
            if (call.getCall().getState() != CallForElevator.DONE){
                if (queue.size() > 1) {
                    elevator.stop(call.getDest_floor());
                }
                else{
                    elevator.goTo(call.getDest_floor());
                }
            } else {
                queue.remove();
            }
        }

    }

    private int findMax(int[] arr) {
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] < min) min = arr[i];
        }
        return min;
    }

    private int[] numOfCalls() {
        int[] nOfCalls = new int[num_of_elevators]; // number of calls for each elevator
        for (int j = 0; j < num_of_elevators; j++) {
            nOfCalls[j] = up_queues[j].size() + down_queues[j].size();
        }
        return nOfCalls;
    }
}
