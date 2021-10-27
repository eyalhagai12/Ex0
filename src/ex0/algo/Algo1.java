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
    private static int counter = 0;

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
        int best_index = -1;

        /* allocate the closest free elevator if there is one */
        int dist = building.maxFloor() - building.minFloor();
        for (int i = 0; i < num_of_elevators; i++) {
            // get relevant elevator
            Elevator elevator = building.getElevetor(i);

            // calculate distance to call
            if ((up_queues[i].isEmpty() || down_queues[i].isEmpty()) && elevator.getState() == Elevator.LEVEL) {
                if (Math.abs(elevator.getPos() - c.getSrc()) <= dist) {
                    dist = Math.abs(elevator.getPos() - c.getSrc());
                    best_index = i;
                }
            }
        }


        /* if no elevator is free, use one that is on the way to the call */
        if (best_index == -1) {
            /* check if there is an elevator in the direction of the call, and the call is a pickup call (in the route of the elevator) */
            for (int i = 0; i < num_of_elevators; i++) { // if all elevators are busy
                Elevator elevator = building.getElevetor(i);

                if (elevator.getState() == Elevator.UP) { // elevator goes up
                    if (elevator.getPos() < c.getSrc()) { // elevator is below call's source
                        best_index = i;
                        break;
                    } else continue;
                }

                if (elevator.getState() == Elevator.DOWN) { // elevator goes down
                    if (elevator.getPos() > c.getSrc()) { // elevator is above call's source
                        best_index = i;
                        break;
                    } else continue;
                }
            }
        }

        
        /* add to the elevator with the least amount of calls */
        if (best_index == -1) {
            int min_size = Integer.MAX_VALUE;

            for (int i = 0; i < num_of_elevators; ++i) {
                if (c.getType() == CallForElevator.UP) {
                    if (up_queues[i].size() < min_size) {
                        min_size = up_queues[i].size();
                        best_index = i;
                    } else {
                        if (down_queues[i].size() < min_size) {
                            min_size = down_queues[i].size();
                            best_index = i;
                        }
                    }
                }
            }
        }

        /* add to the queues according to the call direction */
        if (c.getType() == CallForElevator.UP) {
            CallNode call = new CallNode(c, true);
            up_queues[best_index].add(call);
        } else {
            CallNode call = new CallNode(c, false);
            down_queues[best_index].add(call);
        }

        return best_index;
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
        if (!queue.isEmpty()) {
            // update the first call
            CallNode call = queue.peek();
            call.update();

            // remove first and return it to sort the priority queue
            queue.remove(call);
            queue.add(call);

            // get the first one again (in case the queue changed)
            call = queue.peek();
            call.update();

            // determine what to do with this call
            if (call.getCall().getState() != CallForElevator.DONE) {
                if (!queue.isEmpty()) {
                    elevator.stop(call.getDest_floor()); // stop at the next intermediate level
                }
                // keep the elevator going
                elevator.goTo(call.getDest_floor());
            } else {
                // remove if call is done
                queue.remove();
            }
        }

        counter++;

    }
}
