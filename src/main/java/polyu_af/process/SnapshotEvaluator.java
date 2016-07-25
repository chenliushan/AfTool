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

    final int passingScore = -1;
    final int failingScore = 10;

    public SnapshotEvaluator() {

    }


    public  Hashtable<SnapshotV3, SnapshotScore> evaluate(Hashtable<TestUnit, List<SnapshotV3>> lineSnapshotTable) {
        Hashtable<SnapshotV3, SnapshotScore> snapshotScoreTable = new Hashtable<>();
        for (Map.Entry<TestUnit, List<SnapshotV3>> testUnitSnapshotE : lineSnapshotTable.entrySet()) {
            int score;
            SnapshotScore sss;
            List<SnapshotV3> ssV3List=testUnitSnapshotE.getValue();
            if (testUnitSnapshotE.getKey().isPassing()) {
                System.out.println("P :"+testUnitSnapshotE.getKey());
                score=passingScore;
            }else{
                System.out.println("F :"+testUnitSnapshotE.getKey());
                score=failingScore;
            }
            for(SnapshotV3 ssV3:ssV3List){
                if(snapshotScoreTable.keySet().contains(ssV3)){
                    sss=snapshotScoreTable.get(ssV3);
                    sss.setScore(sss.getScore()+score);
                }else{
                    sss=new SnapshotScore(score);
                    snapshotScoreTable.put(ssV3,sss);
                }
            }
        }
        return snapshotScoreTable;
    }


}
