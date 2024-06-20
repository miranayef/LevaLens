package com.example.levalens;

import java.util.Arrays;
import java.util.Optional;

public enum BanknoteType {
    FIVE_LEV("5_lev_back", "5", "5_lev.m4a"),
    FIVE_LEV_FRONT("5_lev_front", "5", "5_lev.m4a"),
    TEN_LEV("10_lev_back", "10", "10_lev.m4a"),
    TEN_LEV_FRONT("10_lev_front", "10", "10_lev.m4a"),
    TWENTY_LEV("20_lev_back", "20", "20_lev.m4a"),
    TWENTY_LEV_FRONT("20_lev_front", "20", "20_lev.m4a"),
    FIFTY_LEV("50_lev_back", "50", "50_lev.m4a"),
    FIFTY_LEV_FRONT("50_lev_front", "50", "50_lev.m4a"),
    HUNDRED_LEV("100_lev_back", "100", "100_lev.m4a"),
    HUNDRED_LEV_FRONT("100_lev_front", "100", "100_lev.m4a");

    private final String key;
    private final String value;
    private final String audioFilename;

    BanknoteType(String key, String value, String audioFilename) {
        this.key = key;
        this.value = value;
        this.audioFilename = audioFilename;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getAudioFilename() {
        return audioFilename;
    }

    public static Optional<String> getValueByKey(String key) {
        return Arrays.stream(values())
                .filter(type -> type.key.equals(key))
                .map(BanknoteType::getValue)
                .findFirst();
    }

    public static Optional<BanknoteType> getByKey(String key) {
        return Arrays.stream(values())
                .filter(type -> type.key.equals(key))
                .findFirst();
    }
}