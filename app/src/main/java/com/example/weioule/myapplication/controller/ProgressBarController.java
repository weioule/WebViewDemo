package com.example.weioule.myapplication.controller;

/**
 * @author weioule
 * @Create on 2018.6.7
 */

public class ProgressBarController {

    private WcfTime loadingProgressBarTimer;
    private ControllerListener mListener;
    private StateMachine stateMachine;
    private static int PRELOADING = 0;
    private int mPreloadingValue = 0;
    private static int LOADING = 1;
    private int stepProgress = 1;
    private static int STOP = 2;
    private int targetValue = 0;
    private int currentProgress;

    public ProgressBarController(ControllerListener listener) {
        mListener = listener;
        init();
    }

    void init() {
        stateMachine = new StateMachine();
        stateMachine.setListener(new StateChangedListener() {

            @Override
            public void stateChanged(int status) {
                if (status == STOP) {
                    onStop();
                } else if (status == PRELOADING) {
                    onPreloading();
                } else {
                    onLoading();
                }

            }
        });

        loadingProgressBarTimer = new WcfTime(new WcfTime.OnListener() {

            @Override
            public void onAlarmClock() {
                if (stateMachine.getCurrentState() == PRELOADING) {
                    if (currentProgress >= mPreloadingValue) {
                        loadingProgressBarTimer.stopTime();
                        return;
                    }
                    currentProgress += stepProgress;
                    mListener.setProgress(currentProgress);
                    return;
                }

                if (currentProgress >= 100) {
                    stateMachine.setCurrentState(STOP);
                    return;
                }
                if (currentProgress >= targetValue) {
                    loadingProgressBarTimer.stopTime();
                    return;
                }

                currentProgress += stepProgress;
                mListener.setProgress(currentProgress);

            }
        }, 5);
    }

    public void setCurrentValue(int value) {
        if (stateMachine.getCurrentState() == STOP || value == 0) {
            return;
        }
        targetValue = value;
        if (stateMachine.getCurrentState() == PRELOADING) {
            stateMachine.setCurrentState(LOADING);
        } else {
            onLoading();
        }

    }

    public void preloading() {
        if (stateMachine.getCurrentState() == STOP) {
            stateMachine.setCurrentState(PRELOADING);
        }
    }

    private void onPreloading() {
        mPreloadingValue = (int) (Math.random() * 50 + 20);
        currentProgress = 0;
        mListener.start();
        //因为onProgressChanged()返回的进度是从10开始，所以这里就先显示加载1-10的部分
        loadingProgressBarTimer.restartTime(10);
    }

    private void onLoading() {
        int distance = Math.abs(targetValue - currentProgress);
        int interval = (50 - distance) / 10 + 8; //区间3~13
        if (interval < 3 || interval > 13) {
            interval = 8;
        }

        loadingProgressBarTimer.restartTime(interval);

    }

    private void onStop() {
        loadingProgressBarTimer.stopTime();
        mListener.stop();
    }

    private class StateMachine {
        private int currentState = STOP;
        private StateChangedListener listener;

        public int getCurrentState() {
            return currentState;
        }

        public void setCurrentState(int state) {
            if (currentState == state) {
                return;
            }
            this.currentState = state;
            if (listener != null) {
                listener.stateChanged(state);
            }
        }

        public void setListener(StateChangedListener listener) {
            this.listener = listener;
        }
    }

    private interface StateChangedListener {
        void stateChanged(int status);
    }

    public interface ControllerListener {
        void setProgress(int progress);

        void stop();

        void start();
    }

}
