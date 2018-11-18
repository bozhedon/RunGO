package com.myrungo.rungo.utils;

import android.support.annotation.NonNull;

public final class CustomExceptions {

    public static final class UnauthorizedUserExceptions extends Exception {

        @NonNull
        String message;

        public UnauthorizedUserExceptions(@NonNull final String message) {
            super(message);
            this.message = message;
        }

        @NonNull
        @Override
        public String getMessage() {
            return message;
        }

    }

}
