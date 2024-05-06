package com.codewiz.loopers.auto;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.codewiz.loopers.PREF;

import org.jetbrains.annotations.Contract;

import java.util.concurrent.TimeUnit;

/**
 * The AutoLooper class provides a convenient way to run a looping execution of a AutoLooperRunnable
 * based on the lifecycle events of a LifecycleOwner.
 */
public class AutoLooper implements LifecycleEventObserver {

    private static String name;
    // Constants
    private final String TAG = "LOOPER";
    int time;
    // HandlerThread and Handler for background execution
    private HandlerThread handlerThread;
    private Handler handler;
    // Runnable responsible for the looping execution
    private LoopRunnable loopRunnable;

    /**
     * Constructor for the AutoLooper class.
     *
     * @param name The name of the AutoLooper instance.
     */
    public AutoLooper(String name, Lifecycle lifecycleOwner) {
        AutoLooper.name = name;
        // Create a HandlerThread for background execution.
        handlerThread = new HandlerThread(name);
        handlerThread.start();

        // Create a Handler associated with the HandlerThread's looper.
        handler = new Handler(handlerThread.getLooper());

        // Initialize the LoopRunnable.
        loopRunnable = new LoopRunnable();
        time = PREF.getPref().getTime(name);

        // Log instance creation.
        Log.i(TAG, "Instance Created: " + handlerThread.getName());
        lifecycleOwner.addObserver(this);
    }

    /**
     * Factory method to create a AutoLooper instance.
     *
     * @param s The name of the AutoLooper instance.
     * @return A new AutoLooper instance.
     */
    @NonNull
    @Contract(value = "_ -> new")
    public static AutoLooper getInstance(String s, Lifecycle lifecycle) {
        return new AutoLooper(s, lifecycle);
    }

    /**
     * Posts a AutoLooperRunnable to be executed in a loop.
     *
     * @param runnable The AutoLooperRunnable to execute.
     */
    public void post(AutoLooperRunnable runnable) {
        if (loopRunnable != null) {
            // If a LoopRunnable already exists, set the new runnable and log the action.
            loopRunnable.setRunnable(runnable);
            Log.i(TAG, "Posted");
        } else {
            // If no LoopRunnable exists, create a new one and set the runnable.
            loopRunnable = new LoopRunnable();
            loopRunnable.setRunnable(runnable);
            Log.i(TAG, "Instance Created LoopRunnable");
            Log.i(TAG, "Posted");
        }
    }

    /**
     * Set the time interval between executions of the AutoLooperRunnable.
     *
     * @param time The time interval in milliseconds.
     * @return
     */
    public AutoLooper setTime(int time) {
        loopRunnable.setPeriod(time);
        PREF.getPref().setTime(name, time);
        Log.i(TAG, "setTime: time set to " + time + "ms");
        return this;
    }

    /**
     * Set the time interval between executions of the AutoLooperRunnable.
     *
     * @param time     The time interval value.
     * @param timeUnit The time unit for the interval (e.g., seconds, minutes).
     * @return This AutoLooper instance.
     */
    public AutoLooper setTime(long time, TimeUnit timeUnit) {
        long millis;
        switch (timeUnit) {
            case SECONDS:
                millis = TimeUnit.SECONDS.toMillis(time);
                break;
            case MINUTES:
                millis = TimeUnit.MINUTES.toMillis(time);
                break;
            case MILLISECONDS:
                millis = time;
                break;
            case MICROSECONDS:
                millis = TimeUnit.MICROSECONDS.toMillis(time);
                break;
            default:
                throw new IllegalArgumentException("Unsupported time unit: " + timeUnit);
        }
        loopRunnable.setPeriod((int) millis);
        PREF.getPref().setTime(name, (int) millis);
        Log.i(TAG, "setTime: time set to " + millis + "ms");
        return this;
    }


    /**
     * Start the looping execution.
     */
    private void start() {
        if (!loopRunnable.loop) {
            handler.post(loopRunnable);
            loopRunnable.startLoop();
        }
    }

    /**
     * Resume the looping execution.
     */
    private void resume() {
        handler.post(loopRunnable);
        loopRunnable.startLoop();
    }

    /**
     * Pause the looping execution.
     */
    private void pause() {
        if (loopRunnable.loop) {
            loopRunnable.stopLoop();
            handler.removeCallbacks(loopRunnable);
        }
    }

    /**
     * Stop the looping execution.
     */
    private void stop() {
        if (loopRunnable.loop) {
            loopRunnable.stopLoop();
            handler.removeCallbacks(loopRunnable);
        }
    }

    /**
     * Destroy the AutoLooper instance.
     */
    private void destroy() {
        if (handlerThread.isAlive()) {
            // Quit the HandlerThread and cleanup resources.
            Log.i(TAG, "Instance Destroyed: " + handlerThread.getName());
            handlerThread.quitSafely();
            handler = null;
            handlerThread = null;
            loopRunnable = null;
        } else {
            Log.i(TAG, "AutoLooper: Instance Already Destroyed");
        }
        Log.i(TAG, "--------------------------------------------------------------------------------------------");
        PREF.getPref().getEdit().clear().commit();
    }

    // LifecycleEventObserver Interface Implementation
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        // Extract the class name from the LifecycleOwner's toString() output.
        String substring = source.toString().substring(source.toString().lastIndexOf(".") + 1, source.toString().indexOf("@")).toUpperCase();
        // Respond to different lifecycle events.
        switch (event) {
            case ON_CREATE:
                start();
                Log.d(TAG, "AutoLooper Thread: " + handlerThread.getName() + " onCreate: called on " + substring + " Class");
                Log.d(TAG, "--------------------------------------------------------------------------------------------");
                break;
            case ON_START:
                start();
                Log.d(TAG, "AutoLooper Thread: " + handlerThread.getName() + " onStart: called on " + substring + " Class");
                Log.d(TAG, "--------------------------------------------------------------------------------------------");
                break;
            case ON_RESUME:
                resume();
                Log.d(TAG, "AutoLooper Thread: " + handlerThread.getName() + " onResume:  called on " + substring + " Class");
                Log.d(TAG, "--------------------------------------------------------------------------------------------");
                break;
            case ON_PAUSE:
                pause();
                Log.d(TAG, "AutoLooper Thread: " + handlerThread.getName() + " onPause:  called on " + substring + " Class");
                Log.d(TAG, "--------------------------------------------------------------------------------------------");
                break;
            case ON_STOP:
                stop();
                Log.d(TAG, "AutoLooper Thread: " + handlerThread.getName() + " onStop:  called on " + substring + " Class");
                Log.d(TAG, "--------------------------------------------------------------------------------------------");
                break;
            case ON_DESTROY:
                Log.d(TAG, "AutoLooper Thread: " + handlerThread.getName() + " onDestroy:  called on " + substring + " Class");
                destroy();
                Log.d(TAG, "--------------------------------------------------------------------------------------------");
                break;
        }
    }

    // Inner class for the looping execution
    private static class LoopRunnable implements Runnable {
        private AutoLooperRunnable runnable;
        private boolean loop;
        private int period = PREF.pref.getTime(name);

        /**
         * Set the AutoLooperRunnable to execute in a loop.
         *
         * @param runnable The AutoLooperRunnable to execute.
         */
        public void setRunnable(final AutoLooperRunnable runnable) {
            this.runnable = runnable;
            loop = false;
        }

        /**
         * Start the loop execution.
         */
        public void startLoop() {
            loop = true;
        }

        /**
         * Stop the loop execution.
         */
        public void stopLoop() {
            loop = false;
        }

        /**
         * Set the time interval between executions.
         *
         * @param period The time interval in milliseconds.
         */
        public void setPeriod(int period) {
            this.period = period;
        }

        @Override
        public void run() {
            while (loop) {
                runnable.run();
                SystemClock.sleep(period);
            }
        }
    }
}
