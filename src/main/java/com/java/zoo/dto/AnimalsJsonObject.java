package com.java.zoo.dto;

import java.time.Instant;

public interface AnimalsJsonObject {
    String getTitle();

    void setTitle(String title);

    Instant getLocated();

    void setLocated(Instant located);

}
