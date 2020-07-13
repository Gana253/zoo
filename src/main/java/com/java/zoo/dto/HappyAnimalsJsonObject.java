package com.java.zoo.dto;


import org.springframework.beans.factory.annotation.Value;

public interface HappyAnimalsJsonObject {
    @Value("#{target.Roomtitle}")
    String getRoomtitle();
    @Value("#{target.HappyAnimals}")
    Long getHappyAnimals();
}
