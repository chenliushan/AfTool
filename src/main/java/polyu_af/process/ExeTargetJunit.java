package polyu_af.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import polyu_af.models.TargetProgram;
import polyu_af.utils.ReadFileUtils;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by liushanchen on 16/5/19.
 */
public class ExeTargetJunit extends ExeTarget {
    private Logger logger = LogManager.getLogger();


    public ExeTargetJunit(TargetProgram tp) {
        super(tp);

    }

    public void process() {
        ClassLoader loader = new URLClassLoader(newCpUrl());
        JUnitCore core = new JUnitCore();
        core.addListener(new RingingListener());
        Result result = null;
        try {
            String targetClass=tp.getCurrentTarget().getQualifyFileName();
            result = core.run(loader.loadClass(targetClass+ "Test"));
            logger.info("targetClass:" + loader.loadClass(targetClass + "Test"));

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        logger.info("getFailureCount:" + result.getFailureCount());

        if (!result.wasSuccessful()) {
            logger.info("NotSuccessful:" + result.getRunCount());
        } else {
            logger.info("wasSuccessful:" + result.getRunCount());
        }
    }

    private class RingingListener extends RunListener {
        @Override
        public void testFailure(Failure failure) throws Exception {
            super.testFailure(failure);
            System.out.println("testFailure:" + failure.getMessage());
        }

        @Override
        public void testFinished(Description description) throws Exception {
            super.testFinished(description);
            System.out.println("testFinished:" + description.getMethodName());
        }

        @Override
        public void testStarted(Description description) throws Exception {
            super.testStarted(description);
//            System.out.println("testStarted");
        }


        @Override
        public void testRunFinished(Result result) throws Exception {
            super.testRunFinished(result);
            System.out.println("testRunFinished");
        }

        @Override
        public void testRunStarted(Description description) throws Exception {
            super.testRunStarted(description);
            System.out.println("testRunStarted");
        }


    }


    private URL[] newCpUrl() {
        List<String> cps = new ArrayList(Arrays.asList(tp.getClasspathEntries()));
        List<URL> cpsUrl = new ArrayList<URL>();
        if (!cps.contains(tp.getOutputPath())) {
            cps.add(tp.getOutputPath());
        }
        cps.add(tp.getTestClassPath());
        for (String cp : cps) {
            cpsUrl.add(ReadFileUtils.getURL(cp));
        }
        return cpsUrl.toArray(new URL[cps.size()]);

    }


}
