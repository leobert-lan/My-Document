package com.lht.cloudjob.checkable;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.vsocyy.checkable
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> CheckableJobs
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2017/2/21.
 */

public class CheckableJobs {
    private ArrayList<ICheckableJob> jobs;

    private CheckableJobs() {
        jobs = new ArrayList<>();
    }

    public static CheckableJobs getInstance() {
        return new CheckableJobs();
    }

    public CheckableJobs next(ICheckableJob job) {
        jobs.add(job);
        return this;
    }

    private OnAllCheckLegalListener onAllCheckLegalListener;

    public CheckableJobs onAllCheckLegal(OnAllCheckLegalListener listener) {
        this.onAllCheckLegalListener = listener;
        return this;
    }

    public void start() {
        for (ICheckableJob job : jobs) {
            boolean result = job.check();
            if (result == ICheckableJob.RESULT_CHECK_ILLEGAL) {
                if (job instanceof AbsCheckJob) {
                    ((AbsCheckJob) job).onCheckIllegal();
                }
                return;
            }
        }
        onAllCheckLegalListener.onAllCheckLegal();
    }

    public interface OnAllCheckLegalListener {
        void onAllCheckLegal();
    }

}
