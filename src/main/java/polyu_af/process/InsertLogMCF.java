package polyu_af.process;

import polyu_af.models.AccessibleVars;

/**
 * Created by liushanchen on 16/5/4.
 */
public class InsertLogMCF implements ModClassFile {
    AccessibleVars vars=null;

    public InsertLogMCF(AccessibleVars vars) {
        this.vars = vars;
    }
    public boolean modify(){


        return true;
    }
}
