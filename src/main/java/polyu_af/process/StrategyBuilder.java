package polyu_af.process;

/**
 * Created by liushanchen on 16/8/3.
 */
public class StrategyBuilder {
    private static final String SNIPPET = "${snippet}";
    private static final String OLD_STMT = "${old_stmt}";
    private static final String FAIL = "${fail}";
    private static final String STMT_ENDING = ";";

    private static final String SCHEMA_A =
            "{ " + SNIPPET + OLD_STMT + "}";
    private static final String SCHEMA_B =
            "{ if(" + FAIL + "){" + SNIPPET + "}" + OLD_STMT + "}";
    private static final String SCHEMA_C =
            "{ if(!" + FAIL + "){" + OLD_STMT + "}}";
    private static final String SCHEMA_D =
            "{ if(" + FAIL + "){" + SNIPPET + "}else{" + OLD_STMT + "}}";

    public String buildA(String snippet, String old_stmt) {
        if (snippet != null && old_stmt != null
                && snippet.length() > 0 && old_stmt.length() > 0) {
            snippet = checkStmt(snippet);
            old_stmt = checkStmt(old_stmt);
            return SCHEMA_A.replace(SNIPPET, snippet).replace(OLD_STMT, old_stmt);
        }
        return null;
    }

    public String buildB(String fail, String snippet, String old_stmt) {
        if (fail != null && snippet != null && old_stmt != null
                && fail.length() > 0 && snippet.length() > 0 && old_stmt.length() > 0) {
            snippet = checkStmt(snippet);
            old_stmt = checkStmt(old_stmt);
            return SCHEMA_B.replace(SNIPPET, snippet).replace(OLD_STMT, old_stmt).replace(FAIL, fail);
        }
        return null;
    }

    public String buildC(String fail, String snippet, String old_stmt) {
        if (fail != null && snippet != null && old_stmt != null
                && fail.length() > 0 && snippet.length() > 0 && old_stmt.length() > 0) {
            snippet = checkStmt(snippet);
            old_stmt = checkStmt(old_stmt);
            return SCHEMA_C.replace(SNIPPET, snippet).replace(OLD_STMT, old_stmt).replace(FAIL, fail);
        }
        return null;
    }

    public String buildD(String fail, String snippet, String old_stmt) {
        if (fail != null && snippet != null && old_stmt != null
                && fail.length() > 0 && snippet.length() > 0 && old_stmt.length() > 0) {
            snippet = checkStmt(snippet);
            old_stmt = checkStmt(old_stmt);
            return SCHEMA_D.replace(SNIPPET, snippet).replace(OLD_STMT, old_stmt).replace(FAIL, fail);
        }
        return null;
    }

    private String checkStmt(String stmt) {
        if (stmt.endsWith(STMT_ENDING)) {
            return stmt;
        } else {
            return stmt + STMT_ENDING;
        }
    }
}
