package jp.jaxa.iss.kibo.rpc.jebediah;

import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;
import jp.jaxa.iss.kibo.rpc.jebediah.KiboConstants.*;

/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {
    @Override
    protected void runPlan1() {
        // write here your plan 1
        api.startMission();
        api.moveTo(KiboConstants.POINT_1_POS, KiboConstants.POINT_1_ROT, true);

        // send mission completion
        api.reportMissionCompletion();
    }

    @Override
    protected void runPlan2() {
        // write here your plan 2
    }

    @Override
    protected void runPlan3() {
        // write here your plan 3
    }

}

