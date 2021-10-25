package ex0.algo;

import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;
import ex0.Node;
import ex0.my_utils;

import java.util.PriorityQueue;

public class OurElevatorAlgorithm implements ElevatorAlgo {
    private final Building building; // save the building
    private final int num_of_elevators; // save the number of elevators
    private PriorityQueue<Node>[] elevator_queues; // an array of priority queues for each elevator
    private Node[] active_calls; // save active calls as nodes

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
            // calculate the best time for each elevator
            double total_time = calculate_time_for_current_call(i, building.getElevetor(i))
                    + calculate_time_for_next_call(i, building.getElevetor(i), c);

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
        my_utils.log("Got call: " + c.getSrc() + " -> " + c.getDest());
        my_utils.log("Assigned to elevator " + best_index + " at floor "
                + building.getElevetor(best_index).getPos());

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
    public void cmdElevator(int elev) {
        // get calls from the array of calls and from the priority queue
        Node current_call = active_calls[elev];
        Node next_call = elevator_queues[elev].peek();

        // check if active call is done
        //this block should be changed or maybe used elsewhere
        // update of the active calls should happen more often
        // --------------------------------------------------------------------
        // --------------------------------------------------------------------
        // --------------------------------------------------------------------
        if (active_calls[elev] != null) {
            if (active_calls[elev].getCall().getState() == 3) {
                // log when a call is Done
                my_utils.log("Call " + active_calls[elev].getCall().getSrc() + " -> "
                        + active_calls[elev].getCall().getDest() + " is done! (executed by elevator: "
                        + elev + ")");


                if (!elevator_queues[elev].isEmpty()) {
                    active_calls[elev] = elevator_queues[elev].remove();
                } else {
                    active_calls[elev] = null;
                }
            }
        }

        // compare between them (needs fixing)
        if (current_call == null) {
            active_calls[elev] = next_call;
        } else if (next_call == null) {
            active_calls[elev] = current_call;
        } else {
            if (next_call.compareTo(current_call) < 0) {
                active_calls[elev] = elevator_queues[elev].remove();
                elevator_queues[elev].add(current_call);
            }
        }
        // --------------------------------------------------------------------
        // --------------------------------------------------------------------
        // --------------------------------------------------------------------

        if (active_calls[elev] != null) {
            makeCall(building.getElevetor(elev), active_calls[elev].getCall());
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
            int dest = 0;
            if (active_call.getCall().getState() == 1 || active_call.getCall().getState() == 0) {
                dest = active_call.getCall().getSrc();
            } else if (active_call.getCall().getState() == 2) {
                dest = active_call.getCall().getDest();
            }

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
    private double calculate_time_for_next_call(int i, Elevator elevator, CallForElevator call) {
        // take care of the different cases
        double time = 0;
        if (call.getState() == 2){
            // get active elevator call
            Node active_call = active_calls[i];

            // get the correct destination
            int dest = active_call == null ? elevator.getPos() : call.getDest();

            // calculate the time
            time = Math.abs(dest - call.getDest()) / elevator.getSpeed();
        } else if (call.getState() == 0 || call.getState() == 1){

            time = Math.abs(call.getSrc() - call.getDest()) / elevator.getSpeed();
        }
        else{
            System.out.println("Call state is: DONE");
        }

        return time;
    }
}
