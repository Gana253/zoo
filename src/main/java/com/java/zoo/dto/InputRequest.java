package com.java.zoo.dto;

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

    private Long animalId;

    private Long roomId;

    private Long toRoomId;

}
