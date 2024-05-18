package com.example.mcvp.lab3;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.mcvp.data.LabelData;
import com.example.mcvp.exceptions.Exceptions;
import com.example.mcvp.exceptions.InternalException;
import com.example.mcvp.models.CoordinateDto;
import com.example.mcvp.models.Label;
import com.example.mcvp.repositories.LabelRepository;
import com.example.mcvp.services.impl.LabelServiceImpl;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class LabelServiceTest {

  @Mock
  private LabelRepository labelRepository;

  @Spy
  @InjectMocks
  private LabelServiceImpl labelService;

  private List<Label> testLabels;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);

    testLabels = Arrays.asList(
        new Label(1L, "Label 1 2", new CoordinateDto("40.0, 40.0")),
        new Label(2L, "Label 2 3", new CoordinateDto("50.0, 50.0")),
        new Label(3L, "Label 3 4", new CoordinateDto("60.0, 60.0")),
        new Label(4L, "Label 4 1", new CoordinateDto("70.0, 70.0")),
        new Label(5L, "Label 5 8", new CoordinateDto("80.0, 80.0")),
        new Label(6L, "Label 6 1", new CoordinateDto("90.0, 90.0"))
    );
  }

  @Test
  public void testGetAll() {
    when(labelRepository.findAll()).thenReturn(testLabels);

    List<Label> result = labelService.getAll();

    assertAll(
        () -> assertThat(result, hasSize(6)),
        () -> assertThat(result, containsInAnyOrder(
            hasProperty("name", is("Label 1 2")),
            hasProperty("name", is("Label 2 3")),
            hasProperty("name", is("Label 3 4")),
            hasProperty("name", is("Label 4 1")),
            hasProperty("name", is("Label 5 8")),
            hasProperty("name", is("Label 6 1"))
        ))
    );
    verify(labelRepository, times(1)).findAll();
  }

  @Test
  public void testGetById() {
    when(labelRepository.findById(1L)).thenReturn(Optional.of(testLabels.get(0)));

    Label label = labelService.getById(1L);

    assertNotNull(label);
    assertEquals("Label 1 2", label.getName());
    assertEquals(new CoordinateDto("40.0, 40.0"), label.getCoordinates());
    verify(labelRepository, times(1)).findById(1L);
  }

  @Test
  public void testGetByIdNotFound() {
    when(labelRepository.findById(1L)).thenReturn(Optional.empty());

    Exception exception = assertThrows(InternalException.class, () -> labelService.getById(1L));

    assertEquals(Exceptions.LABEL_IS_NOT_FOUND.getMessage(), exception.getMessage());
    verify(labelRepository, times(1)).findById(1L);
  }

  @Test
  public void testCreate() {
    when(labelRepository.save(any(Label.class))).thenReturn(testLabels.get(0));

    LabelData labelData = new LabelData("Test Label", new CoordinateDto("40.0, 40.0"));
    Long labelId = labelService.create(labelData);

    assertNotNull(labelId);
    verify(labelRepository, times(1)).save(any(Label.class));
  }

  @Test
  public void testUpdate() {
    when(labelRepository.findById(1L)).thenReturn(Optional.of(testLabels.get(0)));
    LabelData labelData = new LabelData("Updated Label", new CoordinateDto("50.0, 50.0"));

    labelService.update(1L, labelData);

    verify(labelRepository, times(1)).findById(1L);
    verify(labelRepository, times(1)).save(any(Label.class));
  }

  @Test
  public void testUpdateLabelNotFound() {
    when(labelRepository.findById(1L)).thenReturn(Optional.empty());
    LabelData labelData = new LabelData("Updated Label", new CoordinateDto("50.0, 50.0"));

    Exception exception = assertThrows(InternalException.class, () -> labelService.update(1L, labelData));
    assertEquals(Exceptions.LABEL_IS_NOT_FOUND.getMessage(), exception.getMessage());

    verify(labelRepository, times(1)).findById(1L);
    verify(labelRepository, never()).save(any(Label.class));
  }

  @Test
  public void testDelete() {
    when(labelRepository.findById(1L)).thenReturn(Optional.of(testLabels.get(0)));

    labelService.delete(1L);

    verify(labelRepository, times(1)).findById(1L);
    verify(labelRepository, times(1)).delete(any(Label.class));
  }

  @Test
  public void testDeleteLabelNotFound() {
    when(labelRepository.findById(1L)).thenReturn(Optional.empty());

    Exception exception = assertThrows(InternalException.class, () -> labelService.delete(1L));

    assertEquals(Exceptions.LABEL_IS_NOT_FOUND.getMessage(), exception.getMessage());
    verify(labelRepository, times(1)).findById(1L);
    verify(labelRepository, never()).delete(any(Label.class));
  }

  @Test
  public void testGetByName() {
    when(labelRepository.findByName("Label 1 2")).thenReturn(Optional.of(testLabels.get(0)));

    Optional<Label> result = labelService.getByName("Label 1 2");

    assertTrue(result.isPresent());
    assertEquals("Label 1 2", result.get().getName());
    verify(labelRepository, times(1)).findByName("Label 1 2");
  }

  @Test
  public void testGetByNameNotFound() {
    when(labelRepository.findByName("Test Label")).thenReturn(Optional.empty());

    Optional<Label> result = labelService.getByName("Test Label");

    assertFalse(result.isPresent());
    verify(labelRepository, times(1)).findByName("Test Label");
  }

  @Test
  public void testGetNamesThatContain() {
    when(labelRepository.findAll()).thenReturn(testLabels);

    List<String> result = labelService.getNamesThatContain("1");

    assertEquals(3, result.size());
    assertTrue(result.contains("Label 1 2"));
    assertTrue(result.contains("Label 4 1"));
    assertTrue(result.contains("Label 6 1"));
    verify(labelRepository, times(1)).findAll();
  }

  @Test
  public void testGetNamesThatContainNone() {
    when(labelRepository.findAll()).thenReturn(testLabels);

    List<String> result = labelService.getNamesThatContain("Non-existent");

    assertEquals(0, result.size());
    verify(labelRepository, times(1)).findAll();
  }
}
