package com.sharathp.blabber.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

public abstract class BaseService extends Service {
    private static final String TAG = BaseService.class.getSimpleName();

    private volatile HandlerThread mHandlerThread;
    private ServiceHandler mServiceHandler;

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // An Android handler thread internally operates on a looper.
        mHandlerThread = new HandlerThread(getClass().getSimpleName());
        mHandlerThread.start();
        // An Android service handler is a handler running on a specific background thread.
        mServiceHandler = new ServiceHandler(mHandlerThread.getLooper());
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {

        if (intent != null) {
            final Message message = Message.obtain();
            message.setData(intent.getExtras());
            mServiceHandler.sendMessage(message);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Shutting down: " + getClass().getSimpleName());
        // Cleanup service before destruction
        mHandlerThread.quit();
    }

    final class ServiceHandler extends Handler {
        ServiceHandler(final Looper looper) {
            super(looper);
        }

        // Define how to handle any incoming messages here
        @Override
        public void handleMessage(final Message message) {
            doHandleMessage(message);
        }
    }

    protected abstract void doHandleMessage(final Message message);
}
