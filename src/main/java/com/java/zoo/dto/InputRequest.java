package com.java.zoo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class InputRequest implements Serializable {
    private static final long serialVersionUID = 7217291299864447435L;

    @JsonProperty("animalId")
    @NotNull
    private Long animalId;

    @JsonProperty("roomId")
    @NotNull
    private Long roomId;

}
