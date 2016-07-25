package polyu_af.new_model;

/**
 * Created by liushanchen on 16/7/25.
 */
public class SnapshotScore {
    private int score;


    public SnapshotScore() {
    }

    public SnapshotScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "score=" + score;
    }
}
