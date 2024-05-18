package com.example.mcvp.services.impl;

import com.example.mcvp.data.LabelData;
import com.example.mcvp.exceptions.Exceptions;
import com.example.mcvp.exceptions.InternalException;
import com.example.mcvp.mappers.LabelMapper;
import com.example.mcvp.models.Label;
import com.example.mcvp.repositories.LabelRepository;
import com.example.mcvp.services.LabelService;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {
  private final LabelRepository labelRepository;

  @Override
  public List<Label> getAll() {
    return StreamSupport.stream(labelRepository.findAll().spliterator(), false).toList();
  }

  @Override
  public Label getById(Long id) {
    return labelRepository
        .findById(id)
        .orElseThrow(() -> new InternalException(Exceptions.LABEL_IS_NOT_FOUND));
  }

  @Override
  public Long create(LabelData label) throws RuntimeException {
    Label savedLabel = labelRepository.save(LabelMapper.dataToEntity(label));
    log.info("Label with id = " + savedLabel.getId() + " was saved");
    return savedLabel.getId();
  }

  @Override
  public void add(LabelData... labelData) throws RuntimeException {
    Arrays.stream(labelData)
        .forEach(this::create);
    log.info("Labels were added. Amount of them = " + labelData.length);
  }

  @Override
  public void update(Long id, LabelData label) throws RuntimeException {
    Label labelToUpdate = labelRepository
        .findById(id)
        .orElseThrow(() -> new InternalException(Exceptions.LABEL_IS_NOT_FOUND));
    labelToUpdate.setName(label.getName());
    labelToUpdate.setCoordinates(label.getCoordinates());
    labelRepository.save(labelToUpdate);
    log.info("Label with id = " + id + " was updated");
  }

  @Override
  public void delete(Long id) {
    Label label = labelRepository
        .findById(id)
        .orElseThrow(() -> new InternalException(Exceptions.LABEL_IS_NOT_FOUND));
    labelRepository.delete(label);
    log.info("Label with id = " + id + " was deleted");
  }

  @Override
  public Optional<Label> getByName(String name) {
    return labelRepository.findByName(name);
  }

  @Override
  public List<String> getNamesThatContain(String name) {
    return StreamSupport.stream(labelRepository.findAll().spliterator(), false)
        .map(Label::getName)
        .filter(l -> l.contains(name))
        .toList();
  }

}
