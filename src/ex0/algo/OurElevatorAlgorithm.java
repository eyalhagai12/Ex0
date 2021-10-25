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
            double total_time = calculate_current_route_time(i, building.getElevetor(i))
                    + calculate_time_to_new_route(i, building.getElevetor(i), c);

            // update variables
            if (total_time < best_time) {
                best_time = total_time;
                best_index = i;
            }
        }

        // push calls to the matching priority queue
        Node n = new Node(c, best_time);
        elevator_queues[best_index].add(n);

        // own log
        my_utils.log("Got call from floor " + c.getSrc() + " to " + c.getDest());
        my_utils.log("Assigned to elevator " + best_index + " at floor "
                + building.getElevetor(best_index).getPos());

        // return the best index
        return best_index;
    }

    @Override
    public void cmdElevator(int elev) {
        // get calls from the array of calls and from the priority queue
        Node current_call = active_calls[elev];
        Node next_call = elevator_queues[elev].peek();

        // check if active call is done
        if (active_calls[elev] != null){
            if (active_calls[elev].getCall().getState() == 3){
                if (!elevator_queues[elev].isEmpty()){
                    active_calls[elev] = elevator_queues[elev].remove();
                }
                else{
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

        if (active_calls[elev] != null) {
            makeCall(building.getElevetor(elev), active_calls[elev].getCall());
        }
    }

    private void makeCall(Elevator elevator, CallForElevator call) {
        if (call.getState() == 0 || call.getState() == 1) {
            elevator.goTo(call.getSrc());
        } else if (call.getState() == 2) {
            elevator.goTo(call.getDest());
        }
    }

    /*
     * Calculate the time from now to the next destination of a call
     *
     */
    private double calculate_current_route_time(int i, Elevator elevator) {
        Node active_call = active_calls[i];

        if (active_call == null) {
            return 0;
        } else {
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

    /*
     * Calculate the time to execute a new call
     *
     */
    private double calculate_time_to_new_route(int i, Elevator elevator, CallForElevator call) {
        return Math.abs(call.getSrc() - call.getDest()) / elevator.getSpeed();
    }
}
