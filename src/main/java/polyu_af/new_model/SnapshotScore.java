package polyu_af.new_model;

/**
 * Created by liushanchen on 16/7/25.
 */
public class SnapshotScore implements Comparable<SnapshotScore> {

    final static double passingScore = 0.4;
    final static double failingScore = 0.8;

    private double score;
    int passingTimes = 0;
    int failingTimes = 0;


    public SnapshotScore() {
    }

    public int getPassingTimes() {
        return passingTimes;
    }

    public void setPassingTimes(int passingTimes) {
        this.passingTimes = passingTimes;
    }

    public int getFailingTimes() {
        return failingTimes;
    }

    public void setFailingTimes(int failingTimes) {
        this.failingTimes = failingTimes;
    }

    public void addOneTime(boolean isPassing) {
        if (isPassing) {
            this.passingTimes++;
        } else {
            this.failingTimes++;
        }
        setScore();
    }

    public double getScore() {
        return score;
    }

    private void setScore() {
        this.score = calculate();
    }

    private double calculate() {
        double passing = 0, failing = 0;
            passing = (passingScore * (1 - Math.pow(passingScore, passingTimes)) / (1 - passingScore));
            failing = (failingScore * (1 - Math.pow(failingScore, failingTimes)) / (1 - failingScore));
        return failing - passing;
    }

    @Override
    public String toString() {
        return "SnapshotScore{" +
                "score=" + score +
                ", passingTimes=" + passingTimes +
                ", failingTimes=" + failingTimes +
                "}\n";
    }

    @Override
    public int compareTo(SnapshotScore o) {
        if ((o.getScore() - score) < 0)
            return -1;
        else if ((o.getScore() - score) > 0)
            return 1;
        else return 0;

    }


}
