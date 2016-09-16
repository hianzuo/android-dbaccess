package com.hianzuo.dbaccess.throwable;

import com.hianzuo.dbaccess.Dto;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;

/**
 * User: Ryan
 * Date: 14-3-21
 * Time: 下午4:51
 */
public class DBDataException extends RuntimeException {
    private Dto dto;

    public DBDataException(Dto dto, Throwable throwable) {
        this("", throwable, dto);
    }

    public DBDataException(String detailMessage, Dto dto) {
        super(getDetailMessage(detailMessage, dto, null));
        this.dto = dto;
    }

    public DBDataException(String detailMessage, Throwable throwable, Dto dto) {
        super(getDetailMessage(detailMessage, dto, throwable), throwable);
        this.dto = dto;
    }

    private static String getDetailMessage(String detailMessage, Dto dto, Throwable throwable) {
        String dtoData = "";
        if (null != dto) {
            try {
                dtoData = gson.toJson(dto);
            } catch (Exception e) {
                dtoData = uploadDtoGson.toJson(dto);
            }
        }
        if (null == detailMessage) detailMessage = "";
        if (null != throwable) {
            String message = throwable.getMessage();
            if (null == message) message = throwable.getClass().getSimpleName();
            detailMessage = detailMessage + "#" + message;
        }
        return "\nDetailMessage:" + detailMessage + "\n\nDtoData:\n[" + dtoData + "]";
    }

    public Dto getDto() {
        return dto;
    }

    public static Gson uploadDtoGson = new com.google.gson.GsonBuilder()
            .setExclusionStrategies(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    return "id".equals(f.getName()) && Integer.class.equals(f.getDeclaredType());
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
            }).create();

    public static Gson gson = new Gson();
}
