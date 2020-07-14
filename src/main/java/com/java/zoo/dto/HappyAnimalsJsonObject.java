package com.java.zoo.dto;


import org.springframework.beans.factory.annotation.Value;

public interface HappyAnimalsJsonObject {
    @Value("#{target.Roomtitle}")
    String getRoomtitle();

    void setRoomtitle(String title);

    @Value("#{target.HappyAnimals}")
    Long getHappyAnimals();

    void setHappyAnimals(Long number);
}
