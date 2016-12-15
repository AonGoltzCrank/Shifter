package alon.com.shifter.base_classes;

import android.util.Log;

import java.util.ArrayDeque;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A class to run tasks similar to {@link Timer}.
 */
public class DelayedTaskExecutor {

    public static final String TAG = "RE-EXECUTABLE-TASK";
    private static DelayedTaskExecutor instance = new DelayedTaskExecutor();
    private Timer mTimer = new Timer();
    private ArrayDeque<DelayedTask> mDelayedTaskQueue = new ArrayDeque<DelayedTask>() {
        @Override
        public boolean offer(final DelayedTask o) {
            boolean added = super.offer(o);
            if (added) {
                Log.i(TAG, "offer: Added new task: " + o);
                if (o.mTimed) {
                    final FinishableTask mFinishableTask = o.mTask_Finishable;
                    final TaskResult mTaskResult = o.mTask_Result;
                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Log.i(TAG, "run: Running task: " + o);
                            if (mFinishableTask != null)
                                mFinishableTask.onFinish();
                            else if (mTaskResult != null)
                                mTaskResult.onSucceed();
                        }
                    }, o.mDuration);
                }
            }
            return added;
        }
    };

    private DelayedTaskExecutor() {
    }

    public static DelayedTaskExecutor getInstance() {
        return instance;
    }

    /**
     * Add a {@link DelayedTask} to the queue, if it does not exist.
     *
     * @param mDelayedTask
     */
    public void addTask(DelayedTask mDelayedTask) {
        if (!mDelayedTaskQueue.contains(mDelayedTask))
            mDelayedTaskQueue.offer(mDelayedTask);
    }


    /**
     * Delete all tasks present in the queue.
     */
    public void purge() {
        Log.i(TAG, "purge: Purging executor.");
        mDelayedTaskQueue.clear();
        mTimer.purge();
    }


    /**
     * The delayed latask class.
     */
    public static class DelayedTask {
        FinishableTask mTask_Finishable;
        TaskResult mTask_Result;

        boolean mTimed;

        long mDuration;

        public DelayedTask(FinishableTask fTask, TaskResult tResult, boolean isTimed, long duration) {
            mTask_Finishable = fTask;
            mTask_Result = tResult;
            mTimed = isTimed;
            mDuration = duration;
        }

        @Override
        public boolean equals(Object mOther) {
            if (mOther instanceof DelayedTask)
                return (((DelayedTask) mOther).mDuration == mDuration && ((DelayedTask) mOther).mTimed == mTimed && ((DelayedTask) mOther).mTask_Result == mTask_Result && ((DelayedTask) mOther).mTask_Finishable == mTask_Finishable);
            else
                return false;
        }
    }
}
