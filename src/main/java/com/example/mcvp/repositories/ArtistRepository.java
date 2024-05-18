package com.example.mcvp.repositories;

import com.example.mcvp.models.Album;
import com.example.mcvp.models.Artist;
import org.springframework.data.repository.CrudRepository;

public interface ArtistRepository extends CrudRepository<Artist, Long> {
}
