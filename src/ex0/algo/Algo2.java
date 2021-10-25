package ex0.algo;

import ex0.Building;
import ex0.CallForElevator;

public class Algo2 implements ElevatorAlgo{
    private Building building;


    public Algo2(Building b){
        building = b;
    }

    @Override
    public Building getBuilding() {
        return null;
    }

    @Override
    public String algoName() {
        return null;
    }

    @Override
    public int allocateAnElevator(CallForElevator c) {
        return 0;
    }

    @Override
    public void cmdElevator(int elev) {

    }
}
