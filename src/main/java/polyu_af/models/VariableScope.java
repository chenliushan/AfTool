package polyu_af.models;

/**
 * Created by liushanchen on 16/3/29.
 */
public class VariableScope {
    private int startLine;
    private int endLine;

    public VariableScope(int startLine, int endLine) {
        this.endLine = endLine;
        this.startLine = startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public int getStartLine() {
        return startLine;
    }

    @Override
    public String toString() {
        return "VariableScope{" +
                "startLine=" + startLine +
                ", endLine=" + endLine +
                '}';
    }
}
