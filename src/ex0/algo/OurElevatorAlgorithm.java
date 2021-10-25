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

        // our own log
        Call_IDs.add(c);
        my_utils.log("Got call: (call id " + (id_counter++) + ") " + c.getSrc() + " -> " + c.getDest());
        my_utils.log("Assigned to elevator " + best_index + " at floor "
                + building.getElevetor(best_index).getPos());
        my_utils.log("Estimated time: " + best_time + ", Elevator state: " + building.getElevetor(best_index).getState());

        // check something
        if (building.getElevetor(best_index).getState() != 0)
            for (int i = 0; i < num_of_elevators; ++i) {
                if (building.getElevetor(i).getState() == 0) {
                    System.out.println("Try increasing punish!!!");
                }
            }

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

        // get current active call and next call
        Node active_call = active_calls[elev];
        Node next_call = elevator_queues[elev].peek();

        // check if call is done
        if (active_call != null && active_call.getCall().getState() == 3) {
            // log for myself
            my_utils.log("Call " + Call_IDs.indexOf(active_call.getCall()) + " completed");

            if (!elevator_queues[elev].isEmpty()) {
                active_calls[elev] = elevator_queues[elev].remove();
            } else {
                active_calls[elev] = null;
            }
        }

        // separate to cases
        if (active_call != null && elevator_queues[elev].isEmpty()) {
            // make call regularly
            makeCall(elevator, active_call.getCall());
        }
        if (active_call == null && next_call != null) {
            // swap calls
            active_calls[elev] = next_call;
            makeCall(elevator, next_call.getCall());
            elevator_queues[elev].remove();
        }
        if (active_call != null && !elevator_queues[elev].isEmpty()) {
            // calculate actual current time for each node
            double active_call_time = calculate_elevator_time(elev, elevator, active_call.getCall());
            double next_call_time = calculate_elevator_time(elev, elevator, next_call.getCall());

            // update actual current time for each node
            active_call.setTime_for_exec(active_call_time);
            next_call.setTime_for_exec(next_call_time);

            // compare after update
            if (active_call.compareTo(next_call) > 0) {
                // swap calls for more urgent ones
                active_calls[elev] = elevator_queues[elev].remove();

                // push back to the priority queue
                elevator_queues[elev].add(active_call);
            }
            // make the call
            makeCall(elevator, active_calls[elev].getCall());
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
     * @param i The index of the elevator
     * @param elevator The elevator
     * @param c The call
     * @return the estimated time to execute the call
     */
    private double calculate_elevator_time(int i, Elevator elevator, CallForElevator c){
        // calculate the best time for each elevator
        double total_time = calculate_time_for_current_call(i, elevator)
                + calculate_time_for_call(i, elevator, c);


        // if elevator is active, punish a little
        if (elevator.getState() != 0) {
            total_time += 5.5;
        } else {
            total_time /= 2;
        }

        // check more cases of punish and reward
        if (elevator.getState() == 1) { // elevator going up
            if (actual_direction(c) >= elevator.getPos() && active_calls[i] != null && actual_direction(c) <= actual_direction(active_calls[i].getCall())) {
//                    total_time -= 1;
                total_time /= 2;
            } else {
                total_time += 3;
            }
        } else if (elevator.getState() == -1) {
            if (actual_direction(c) <= elevator.getPos() && active_calls[i] != null && active_calls[i] != null && actual_direction(c) <= actual_direction(active_calls[i].getCall())) {
//                    total_time -= 1;
                total_time /= 2;
            } else {
                total_time += 3;
            }
        }

        return total_time;
    }

    public void update_calls(int i){
        if (elevator_queues[i].iterator().hasNext()){

        }
    }
}
