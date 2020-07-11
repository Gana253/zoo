package com.java.zoo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

/**
 * A Favorite.
 */
@Entity
@Table(name = "favorite")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter @Setter
@EqualsAndHashCode
@ToString
public class Favorite implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "room_id")
    private Long roomId;

    @ManyToOne
    @EqualsAndHashCode.Exclude @ToString.Exclude
    @JsonIgnoreProperties(value = "favorites", allowSetters = true)
    private Animal animal;

}
