package com.java.zoo.web.controller;

import com.java.zoo.entity.Favorite;
import com.java.zoo.exception.BadRequestAlertException;
import com.java.zoo.repository.FavoriteRepository;
import com.java.zoo.util.HeaderUtil;
import com.java.zoo.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link Favorite}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class FavoriteController {

    private final Logger log = LoggerFactory.getLogger(FavoriteController.class);

    private static final String ENTITY_NAME = "favorite";

    @Value("${spring.application.name}")
    private String applicationName;

    private final FavoriteRepository favoriteRepository;

    public FavoriteController(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    /**
     * {@code POST  /favorites} : Create a new favorite.
     *
     * @param favorite the favorite to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new favorite, or with status {@code 400 (Bad Request)} if the favorite has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/favorites")
    public ResponseEntity<Favorite> createFavorite(@RequestBody Favorite favorite) throws URISyntaxException {
        log.debug("REST request to save Favorite : {}", favorite);
        if (favorite.getId() != null) {
            throw new BadRequestAlertException("A new favorite cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Favorite result = favoriteRepository.save(favorite);
        return ResponseEntity.created(new URI("/api/favorites/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /favorites} : Updates an existing favorite.
     *
     * @param favorite the favorite to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated favorite,
     * or with status {@code 400 (Bad Request)} if the favorite is not valid,
     * or with status {@code 500 (Internal Server Error)} if the favorite couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/favorites")
    public ResponseEntity<Favorite> updateFavorite(@RequestBody Favorite favorite) throws URISyntaxException {
        log.debug("REST request to update Favorite : {}", favorite);
        if (favorite.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Favorite result = favoriteRepository.save(favorite);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, favorite.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /favorites} : get all the favorites.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of favorites in body.
     */
    @GetMapping("/favorites")
    public List<Favorite> getAllFavorites() {
        log.debug("REST request to get all Favorites");
        return favoriteRepository.findAll();
    }

    /**
     * {@code GET  /favorites/:id} : get the "id" favorite.
     *
     * @param id the id of the favorite to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the favorite, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/favorites/{id}")
    public ResponseEntity<Favorite> getFavorite(@PathVariable Long id) {
        log.debug("REST request to get Favorite : {}", id);
        Optional<Favorite> favorite = favoriteRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(favorite);
    }

    /**
     * {@code DELETE  /favorites/:id} : delete the "id" favorite.
     *
     * @param id the id of the favorite to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/favorites/{id}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable Long id) {
        log.debug("REST request to delete Favorite : {}", id);
        favoriteRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
