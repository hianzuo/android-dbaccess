package com.flyhand.core.utils;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Ryan
 * Date: 14-3-26
 * Time: 下午12:53
 */
public class TypeToken {
    public static Type get(Type rawType, Type genericClz) {
        Type[] actualTypeArguments = new Type[]{genericClz};
        return new ParameterizedTypeImpl(actualTypeArguments, null, rawType);
    }

    public static Type get(Type rawType, Type genericClz, Type genericClz2) {
        Type[] actualTypeArguments = new Type[]{genericClz, genericClz2};
        return new ParameterizedTypeImpl(actualTypeArguments, null, rawType);
    }

    public static Type getListType(Type clz) {
        return get(List.class, clz);
    }

    public static Type getMapType(Type genericClz, Type genericClz2) {
        return get(HashMap.class, genericClz, genericClz2);
    }

    static class ParameterizedTypeImpl implements ParameterizedType {
        private final Type[] actualTypeArguments;
        private final Type ownerType;
        private final Type rawType;
        private int hash;

        public ParameterizedTypeImpl(Type[] actualTypeArguments, Type ownerType,
                                     Type rawType) {
            super();
            if (actualTypeArguments == null || actualTypeArguments.length == 0
                    || rawType == null) {
                throw new IllegalArgumentException();
            }
            this.actualTypeArguments = actualTypeArguments;
            this.ownerType = ownerType;
            this.rawType = rawType;

            hash = 31 + (ownerType != null ? ownerType.hashCode() : 0);
            hash = 31 * hash + rawType.hashCode();
            hash = 31 * hash + Arrays.hashCode(actualTypeArguments);
        }

        @Override
        public Type[] getActualTypeArguments() {
            return actualTypeArguments;
        }

        @Override
        public Type getOwnerType() {
            return ownerType;
        }

        @Override
        public Type getRawType() {
            return rawType;
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof ParameterizedType)) {
                return false;
            }
            ParameterizedType other = (ParameterizedType) obj;
            if (!Arrays.equals(actualTypeArguments, other.getActualTypeArguments())) {
                return false;
            }
            if (ownerType == null) {
                if (other.getOwnerType() != null) {
                    return false;
                }
            } else if (!ownerType.equals(other.getOwnerType())) {
                return false;
            }
            if (rawType == null) {
                if (other.getRawType() != null) {
                    return false;
                }
            } else if (!rawType.equals(other.getRawType())) {
                return false;
            }
            return true;
        }
    }
}
