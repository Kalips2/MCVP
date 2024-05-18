package com.example.mcvp.services;

import com.example.mcvp.data.AlbumData;
import com.example.mcvp.models.Album;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface AlbumService {
  List<Album> getAll();

  Album getById(Long id);

  void create(AlbumData album, MultipartFile file) throws RuntimeException;

  void update(Long id, AlbumData album, MultipartFile file) throws RuntimeException;

  void delete(Long id);

  Long countAlbumsByLabelName(String labelName);
}
