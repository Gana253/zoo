package com.java.zoo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class InputRequest implements Serializable {
    private static final long serialVersionUID = 7217291299864447435L;

    @JsonProperty("animalId")
    @NotNull
    private Long animalId;

    @JsonProperty("toRoomId")
    @NotNull
    private Long toRoomId;

}
