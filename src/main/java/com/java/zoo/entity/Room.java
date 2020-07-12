package com.java.zoo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A Room.
 */
@Entity
@Table(name = "room")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "title")
    private String title;

    @Min(value = 0L)
    @Column(name = "size")
    private Long size;

    @Column(name = "created")
    private Instant created = Instant.now();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = "animals", allowSetters = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Animal> animals = new HashSet<>();


}
