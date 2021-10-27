package ex0.algo;

import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;
import ex0.Node;
import ex0.my_utils;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class OurElevatorAlgorithm implements ElevatorAlgo {
    private final Building building; // save the building
    private final int num_of_elevators; // save the number of elevators
    private PriorityQueue<Node>[] elevator_queues; // an array of priority queues for each elevator
    private Node[] active_calls; // save active calls as nodes
    private double[] elevator_times;
    private ArrayList<CallForElevator> Call_IDs; // a list for logging
    private ArrayList<Integer> call_elevators; // for logging
    private int id_counter = 0; // for logging

    /**
     * Constructor
     *
     * @param b the building in which we operate
     */
    public OurElevatorAlgorithm(Building b) {
        my_utils.reset_log(); // reset out own log
        building = b; // assign the building
        num_of_elevators = building.numberOfElevetors(); // assign num of elevators
        elevator_queues = new PriorityQueue[num_of_elevators]; // initiate PQ array
        active_calls = new Node[num_of_elevators]; // initiate to save active calls
        Call_IDs = new ArrayList<CallForElevator>(); // initiate array list
        call_elevators = new ArrayList<Integer>(); // initiate array list
        elevator_times = new double[num_of_elevators]; // initiate array of elevator times

        // initiate PQ's
        for (int i = 0; i < num_of_elevators; ++i) {
            elevator_queues[i] = new PriorityQueue<Node>();
        }

    }

    /**
     * Get the building in which the algorithm operates
     *
     * @return a building object
     */
    @Override
    public Building getBuilding() {
        return building;
    }

    /**
     * Get the name of the algorithm
     *
     * @return a string representing the name
     */
    @Override
    public String algoName() {
        return "Little bits of hope";
    }

    /**
     * Allocate the most fitting elevator by calculating the time for the execution of the call
     * from the current moment of each elevator
     *
     * @param c the call for elevator (src, dest)
     * @return the index of the chosen elevator
     */
    @Override
    public int allocateAnElevator(CallForElevator c) {
        Building building = getBuilding(); // get the building

        // keep track of best time and best index
        double best_time = Double.POSITIVE_INFINITY;
        int best_index = 0;

        // update best time and best index
        for (int i = 0; i < num_of_elevators; ++i) {
            // get elevator
            Elevator elevator = building.getElevetor(i);

            // calculate the best time for each elevator
            double total_time = calculate_elevator_time(i, elevator, c);

            // update variables
            if (total_time < best_time) {
                best_time = total_time;
                best_index = i;
            }
        }

        // push calls to the matching priority queue
        Node n = new Node(c, best_time);
        elevator_queues[best_index].add(n);

        // return the best index
        return best_index;
    }

    /**
     * Command the elevator
     * This function runs each second in the simulator
     *
     * @param elev the current Elevator index on which the operation is performed.
     */
    @Override
    public void cmdElevator(int elev) { // i want to rewrite this function again
        // get elevator to command
        Elevator elevator = building.getElevetor(elev);

        // log
        my_utils.log("Elevator " + elev + " queue size " + elevator_queues[elev].size());

        // update elevator calls
        update_calls(elev, elevator);

        /* here we should add the new stuff and make it better */
        /* Consider the size of the PQ's */
//
//        int max_queue = 0;
//        if (!elevator_queues[elev].isEmpty()){
//            my_utils.log("Elevator " + elev + " queue size is: " + elevator_queues[elev].size());
//            max_queue = elevator_queues[elev].size() > max_queue ? elevator_queues[elev].size() : max_queue;
//        }
//
//        my_utils.log("Max queue size: " + max_queue);

        if (active_calls[elev] != null) {
            makeCall(elevator, active_calls[elev].getCall());
//            my_utils.log("" + active_calls[elev].getCall());
        }
    }

    /**
     * Execute a call in terms of actions (stop, goto)
     *
     * @param elevator The elevator on which to operate
     * @param call     the call to execute
     */
    private void makeCall(Elevator elevator, CallForElevator call) {
        if (call.getState() == 0 || call.getState() == 1) {
            elevator.goTo(call.getSrc());
        } else if (call.getState() == 2) {
            elevator.goTo(call.getDest());
        }
    }

    /**
     * Get the actual direction of the call
     *
     * @param call The call
     * @return The floor to got to
     */
    private int actual_direction(CallForElevator call) {
        if (call.getState() == 0 || call.getState() == 1) {
            return call.getSrc();
        } else if (call.getState() == 2) {
            return call.getDest();
        } else {
            return 0;
        }
    }

    /**
     * Calculate the time from the current position of the elevator to the end of his current route
     *
     * @param i        the inde of the elevator
     * @param elevator the elevator itself
     * @return the time to end his current call
     */
    private double calculate_time_for_current_call(int i, Elevator elevator) {
        Node active_call = active_calls[i]; // Get the current active call

        // Calculate the time
        if (active_call == null) { // If there is no active call return 0
            return 0;
        } else { // If there is an active call, calculate the time

            // get current position
            int current_pos = elevator.getPos();

            // get destination
            int dest = actual_direction(active_call.getCall());

            // return estimated time
            return Math.abs(current_pos - dest) / elevator.getSpeed();
        }
    }

    /**
     * Calculate the time to execute the new call
     * Note: there can be cases where the new call is not in state INIT,
     * and we should consider that
     *
     * @param i        The index of the elevator
     * @param elevator The elev
     * @param call     The call
     * @return The time to execute the new call
     */
    private double calculate_time_for_call(int i, Elevator elevator, CallForElevator call) {
        // take care of the different cases
        double time = 0;

        // split to cases
        if (call.getState() == 0 || call.getState() == 1) {
            return Math.abs(call.getSrc() - call.getDest()) / elevator.getSpeed();
        } else if (call.getState() == 2) {
            return Math.abs(elevator.getPos() - call.getDest()) / elevator.getSpeed();
        } else {
            return 0;
        }
    }

    /**
     * Calculate the time for the elevator to make its call
     *
     * @param i        The index of the elevator
     * @param elevator The elevator
     * @param c        The call
     * @return the estimated time to execute the call
     */
    private double calculate_elevator_time(int i, Elevator elevator, CallForElevator c) {
        // calculate the best time for each elevator
        double total_time = calculate_time_for_current_call(i, elevator)
                + calculate_time_for_call(i, elevator, c);

        /* change punish and reward rules */
        if (elevator.getState() == Elevator.UP) { // check if elevator is going up
            if (actual_direction(c) >= elevator.getPos()) {
                total_time /= 2;
            } else {
                total_time += 2;
            }
        } else if (elevator.getState() == Elevator.DOWN) { // check if elevator is going down
            if (actual_direction(c) <= elevator.getPos()) {
                total_time /= 5;
            } else {
                total_time += 2;
            }
        } else { // check elevator is at rest
            total_time /= 2;
        }

        // add some time to the score according to the amount of calls in the PQ
        total_time += elevator_queues[i].size() * 7.0 / num_of_elevators;

        /* end of punish rules */

        return total_time;
    }

    /**
     * Update all calls for an elevator
     *
     * @param i        The index of the elevator
     * @param elevator The elevator
     */
    private void update_calls(int i, Elevator elevator) {
        if (active_calls[i] != null) {
            // update time
            double time = calculate_elevator_time(i, elevator, active_calls[i].getCall());
            active_calls[i].setTime_for_exec(time);

            // check if call is done
            if (active_calls[i].getCall().getState() == 3) {
                active_calls[i] = null;
            }

        } else if (active_calls[i] == null && !elevator_queues[i].isEmpty()) {
            // move call to the active calls
            active_calls[i] = elevator_queues[i].remove();
        }

        // go over all elements in queue
        if (!elevator_queues[i].isEmpty() && elevator_queues[i].iterator().hasNext()) {
            // get next Node
            Node next = elevator_queues[i].iterator().next();

            // remove the object
            elevator_queues[i].remove(next);

            if (next.getCall().getState() != 3) {
                // update node time
                double time = calculate_elevator_time(i, elevator, next.getCall());
                next.setTime_for_exec(calculate_elevator_time(i, elevator, next.getCall()));

                // add the object back
                elevator_queues[i].add(next);
            }
        }

//        elevator_times[i] = calculate_all_calls_time(i);
    }

    /**
     * Get the estimated time it takes to execute all calls
     *
     * @param i the index of the elevator
     * @return the time to execute all calls
     */
    private double calculate_all_calls_time(int i) {
        double sum = 0; // save the sum

        // iterate over all elements in the PQ
        if (elevator_queues[i].iterator().hasNext()) {
            Node next = elevator_queues[i].iterator().next();
            sum += next.getTime_for_exec();
        }

        return sum;
    }
}
