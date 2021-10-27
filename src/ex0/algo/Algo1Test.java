package ex0.algo;

import ex0.Building;
import ex0.simulator.Builging_A;
import ex0.simulator.ElevetorCallList;
import ex0.simulator.Simulator_A;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Algo1Test {
    static ElevatorAlgo algo;

    @BeforeEach
    void setUp(){
        // building for that stage
        Building building;


        Simulator_A.initData(7, null);

        // initiate algorithm
        algo = new Algo1(Simulator_A.getBuilding());
        Simulator_A.initAlgo(algo);
    }

    @Test
    void allocateAnElevator() {

    }

    @Test
    void cmdElevator() {

    }
}