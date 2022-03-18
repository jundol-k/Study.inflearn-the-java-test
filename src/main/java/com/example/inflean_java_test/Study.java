package com.example.inflean_java_test;

public class Study {

    private StudyStatus status;

    private int limit;

    public Study() {
    }

    public Study(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("limit은 0보다 커야한다.");
        }
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }

    public StudyStatus getStatus() {
        return status;
    }
}
