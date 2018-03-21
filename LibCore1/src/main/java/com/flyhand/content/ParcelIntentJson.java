package com.flyhand.content;

import android.os.Parcel;
import android.os.Parcelable;

import com.flyhand.core.utils.StringUtil;

/**
 * Created by Ryan on 15/6/1.
 */
public class ParcelIntentJson implements IntentJson, Parcelable {
    private transient String key;
    private transient String clazz;
    private transient IntentJsonFetcher fetcher;

    public ParcelIntentJson() {
    }

    public void setIntentJsonFetcher(IntentJsonFetcher fetcher) {
        this.fetcher = fetcher;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (StringUtil.isEmpty(key)) {
            readySend(getIntentJson());
        }
        dest.writeString(key);
        dest.writeString(clazz);
    }

    private IntentJson getIntentJson() {
        if (null != fetcher) {
            return fetcher.get();
        } else {
            return this;
        }
    }


    public static IntentJson readFromParcel(Parcel source) {
        String key = source.readString();
        String clazz = source.readString();
        Class<?> aClass;
        try {
            aClass = Class.forName(clazz);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("the clazz[" + clazz + "] not exist.");
        }
        return IntentJsonUtil.read(key, aClass);
    }

    public static final Creator<ParcelIntentJson> CREATOR
            = new Creator<ParcelIntentJson>() {

        @Override
        public ParcelIntentJson createFromParcel(Parcel source) {
            return (ParcelIntentJson) readFromParcel(source);
        }

        @Override
        public ParcelIntentJson[] newArray(int size) {
            return new ParcelIntentJson[size];
        }
    };

    public void readySend(IntentJson json) {
        this.clazz = json.getClass().getName();
        this.key = IntentJsonUtil.store(json);
    }

    public static interface IntentJsonFetcher {
        IntentJson get();
    }
}
