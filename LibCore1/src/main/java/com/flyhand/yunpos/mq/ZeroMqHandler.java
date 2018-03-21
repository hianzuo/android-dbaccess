package com.flyhand.yunpos.mq;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.flyhand.core.app.AbstractCoreApplication;
import com.flyhand.core.utils.StringUtil;
import com.flyhand.core.utils.ThreadFactoryUtil;
import com.google.gson.Gson;

import org.zeromq.ZMQ;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * On 2017/7/10.
 *
 * @author Ryan
 */

public class ZeroMqHandler {
    public static final String TAG = "ZeroMqHandler";
    public static final String ACTION = "com.flyhand.core.mq.ZeroMqHandler";
    public static final IntentFilter FILTER = new IntentFilter(ACTION);
    private static final Gson GSON = new Gson();
    private static final int MQ_PORT = 23500;
    private static ExecutorService mExecutor;
    private static MqInitRunner mMqInitRunner;

    public static synchronized void initSender() {
        shutdownOldSender();
        if (AbstractCoreApplication.inMainThread()) {
            ExecutorService service = ThreadFactoryUtil.createSingle("initSender", false, 1);
            service.submit(new Runnable() {
                @Override
                public void run() {
                    getPublisher();
                }
            });
            service.shutdown();
        } else {
            throw new RuntimeException("can not initSender from child thread.");
        }
    }

    public static synchronized void initReceiver(final String server) {
        Log.d(TAG, "initReceiver " + server);
        if (StringUtil.isEmpty(server)) {
            return;
        }
        if (AbstractCoreApplication.inMainThread()) {
            shutdownOldReceiver();
            mExecutor = ThreadFactoryUtil.createSingle("MQHandlerThread", true, 1);
            mMqInitRunner = new MqInitRunner(server);
            mExecutor.submit(mMqInitRunner);
        } else {
            throw new RuntimeException("can not initReceiver from child thread.");
        }
    }

    private static void shutdownOldSender() {
        if (null != publisher) {
            try {
                publisher.close();
            } catch (Exception ignored) {
            }
            publisher = null;
        }
    }

    private static void shutdownOldReceiver() {
        if (null != mMqInitRunner) {
            mMqInitRunner.shutdown();
            mMqInitRunner = null;
        }
        if (null != mExecutor) {
            mExecutor.shutdown();
            mExecutor = null;
        }
    }

    public static void broadcast(String topic, String message) {
        ZMQ.Socket publisher = getPublisher();
        publisher.send(topic + "#" + message);
    }

    public static void sendBroadcast(Intent intent) {
        MqIntent mqIntent = new MqIntent(intent);
        String topic = AbstractCoreApplication.get().getPackageName();
        String mqContent = GSON.toJson(mqIntent);
        MqMessage mqMessage = new MqMessage(MqMessageType.INTENT, mqContent);
        broadcast(topic, GSON.toJson(mqMessage));
    }

    private static ZMQ.Socket publisher;

    private static synchronized ZMQ.Socket getPublisher() {
        Log.d(TAG, "initSender");
        if (null == publisher) {
            ZMQ.Context context = ZMQ.context(1);
            publisher = context.socket(ZMQ.PUB);
            publisher.bind("tcp://*:" + MQ_PORT);
            publisher.setSendTimeOut(3000);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }
        return publisher;
    }

    private static class MqInitRunner implements Runnable {
        private boolean mShutdown = false;
        private Thread runnerThread;
        private String server;

        public MqInitRunner(String server) {
            this.server = server;
        }

        public void shutdown() {
            mShutdown = true;
            if (null != runnerThread) {
                try {
                    runnerThread.interrupt();
                } catch (Exception ignored) {
                }
            }
        }

        @Override
        public void run() {
            runnerThread = Thread.currentThread();
            ZMQ.Context context = ZMQ.context(1);
            ZMQ.Socket subscriber = context.socket(ZMQ.SUB);
            subscriber.setReceiveTimeOut(10000);
            subscriber.connect("tcp://" + server + ":" + MQ_PORT);
            String subTopic = AbstractCoreApplication.get().getPackageName() + "#";
            subscriber.subscribe(subTopic.getBytes(ZMQ.CHARSET));
            while (!mShutdown) {
                try {
                    String message = subscriber.recvStr();
                    if (null != message) {
                        Log.d(TAG, "onReceiveMqMessage ：message");
                        onReceiveMessage(message.substring(subTopic.length()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            subscriber.close();
            context.term();
        }
    }

    private static void onReceiveMessage(String message) {
        MqMessage mqMessage;
        try {
            mqMessage = GSON.fromJson(message, MqMessage.class);
        } catch (Exception e) {
            Log.e(TAG, "收到不能处理的消息，内容：" + message, e);
            return;
        }
        if (null != mqMessage) {
            onReceiveMqMessage(mqMessage);
        }
    }

    private static void onReceiveMqMessage(MqMessage mqMessage) {
        if (MqMessageType.INTENT == mqMessage.getType()) {
            onReceiveMqMessageIntent(mqMessage.getContent());
        }
    }

    private static void onReceiveMqMessageIntent(String content) {
        MqIntent mqIntent = MqIntent.create(content);
        Intent intent = mqIntent.createIntent();
        AbstractCoreApplication.get().sendBroadcast(intent);
    }

    private static class MqMessage {
        private MqMessageType type;
        private String content;

        public MqMessage(MqMessageType type, String content) {
            this.type = type;
            this.content = content;
        }

        public MqMessageType getType() {
            return type;
        }

        public String getContent() {
            return content;
        }
    }

    private static class MqIntent {
        private String action;
        private HashMap<String, String> dataStr;
        private HashMap<String, Integer> dataInt;
        private HashMap<String, Boolean> dataBool;

        public MqIntent(Intent intent) {
            this.action = intent.getAction();
            this.dataStr = new HashMap<>();
            this.dataInt = new HashMap<>();
            this.dataBool = new HashMap<>();
            Bundle bundle = intent.getExtras();
            if (null != bundle) {
                Set<String> keys = bundle.keySet();
                if (null != keys) {
                    for (String key : keys) {
                        Object o = bundle.get(key);
                        if (null == o) {
                            continue;
                        }
                        if (o instanceof Integer || o.getClass().equals(int.class)) {
                            //noinspection ConstantConditions
                            dataInt.put(key, (Integer) o);
                        } else if (o instanceof Boolean || o.getClass().equals(boolean.class)) {
                            //noinspection ConstantConditions
                            dataBool.put(key, (Boolean) o);
                        } else if (o instanceof String) {
                            dataStr.put(key, (String) o);
                        } else {
                            throw new RuntimeException("不支持类型" + o.getClass().getName());
                        }
                    }
                }
            }
        }

        public String getAction() {
            return action;
        }

        public static MqIntent create(String content) {
            return GSON.fromJson(content, MqIntent.class);
        }

        public Intent createIntent() {
            Intent intent = new Intent(getAction());
            for (String name : dataStr.keySet()) {
                intent.putExtra(name, dataStr.get(name));
            }
            for (String name : dataBool.keySet()) {
                intent.putExtra(name, dataBool.get(name));
            }
            for (String name : dataInt.keySet()) {
                intent.putExtra(name, dataInt.get(name));
            }
            return intent;
        }
    }

    private enum MqMessageType {
        /**
         * android.os.Intent 传输的类型
         * 支持的extra类型有 String;Boolean;Integer 其他类型不支持
         * 传输到客机后，客机再取出转换成 android.os.Intent 进行广播
         */
        INTENT
    }
}
