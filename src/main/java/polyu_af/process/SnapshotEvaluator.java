package polyu_af.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import polyu_af.TestUnit;
import polyu_af.new_model.SnapshotScore;
import polyu_af.new_model.SnapshotV3;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;


/**
 * Created by liushanchen on 16/6/13.
 */
public class SnapshotEvaluator {
    private static Logger logger = LogManager.getLogger(SnapshotEvaluator.class.getName());



    public SnapshotEvaluator() {

    }


    public  Hashtable<SnapshotV3, SnapshotScore> evaluate(Hashtable<TestUnit, List<SnapshotV3>> lineSnapshotTable) {
        Hashtable<SnapshotV3, SnapshotScore> snapshotScoreTable = new Hashtable<>();
        for (Map.Entry<TestUnit, List<SnapshotV3>> testUnitSnapshotE : lineSnapshotTable.entrySet()) {
            SnapshotScore sss;
            List<SnapshotV3> ssV3List=testUnitSnapshotE.getValue();
            for(SnapshotV3 ssV3:ssV3List){
                if(snapshotScoreTable.keySet().contains(ssV3)){
                    sss=snapshotScoreTable.get(ssV3);
                    sss.addOneTime(testUnitSnapshotE.getKey().isPassing());
                }else{
                    sss=new SnapshotScore();
                    sss.addOneTime(testUnitSnapshotE.getKey().isPassing());
                    snapshotScoreTable.put(ssV3,sss);
                }
            }
        }
        return snapshotScoreTable;
    }


}
