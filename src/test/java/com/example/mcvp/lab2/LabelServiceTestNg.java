package com.example.mcvp.lab2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.example.mcvp.data.LabelData;
import com.example.mcvp.exceptions.Exceptions;
import com.example.mcvp.exceptions.InternalException;
import com.example.mcvp.models.CoordinateDto;
import com.example.mcvp.models.Label;
import com.example.mcvp.repositories.impl.LabelRepositoryImpl;
import com.example.mcvp.services.impl.LabelServiceImpl;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class LabelServiceTestNg {
  static LabelRepositoryImpl labelRepository;

  static List<Label> testLabels;

  static LabelServiceImpl labelService;

  @BeforeClass(groups = {"get", "delete", "count", "create-update"})
  public static void setupAll() {
    labelRepository = new LabelRepositoryImpl();
    labelService = new LabelServiceImpl(labelRepository);
  }

  @BeforeMethod(groups = {"get", "delete", "count", "create-update"})
  public void setupTest() {
    resetElements();

    labelRepository.saveAll(testLabels);
  }

  private void resetElements() {
    labelRepository.deleteAll();

    testLabels = Arrays.asList(
        new Label(null, "Label 1", new CoordinateDto("40.0, 40.0")),
        new Label(null, "Label 2", new CoordinateDto("50.0, 50.0")),
        new Label(null, "Label 3", new CoordinateDto("60.0, 60.0")),
        new Label(null, "Label 4", new CoordinateDto("70.0, 70.0")),
        new Label(null, "Label 5", new CoordinateDto("80.0, 80.0")),
        new Label(null, "Label 6", new CoordinateDto("90.0, 90.0"))
    );
  }

  @Test(groups = "get")
  public void testGetAll() {
    List<Label> result = labelService.getAll();

    assumeTrue(!result.isEmpty());

    assertAll(
        () -> assertThat(result, hasSize(6)),
        () -> assertThat(result, containsInAnyOrder(
            hasProperty("name", is("Label 1")),
            hasProperty("name", is("Label 2")),
            hasProperty("name", is("Label 3")),
            hasProperty("name", is("Label 4")),
            hasProperty("name", is("Label 5")),
            hasProperty("name", is("Label 6"))
        ))
    );
  }

  @Test(groups = "get")
  public void testGetById() {
    Label label = labelService.getById(testLabels.get(0).getId());

    assertAll(
        () -> assertNotNull(label),
        () -> assertThat(label.getName(), is("Label 1")),
        () -> assertThat(label.getCoordinates(),  allOf(
            hasProperty("lat", is(40.0)),
            hasProperty("lon", is(40.0))
        ))
    );
  }

  @Test(groups = "get")
  public void testGetByIdThrowException() {
    Exception exception = assertThrows(InternalException.class, () -> labelService.getById(-1L));

    assertThat(exception.getMessage(), is(Exceptions.LABEL_IS_NOT_FOUND.getMessage()));
  }

  @Test(groups = "create-update")
  public void testCreate() {
    LabelData newLabelData = new LabelData("New Label", new CoordinateDto("100.0, 100.0"));
    Long labelId = labelService.create(newLabelData);

    List<Label> result = labelService.getAll();
    assertThat(result, hasSize(7));

    Label createdLabel = result.stream()
        .filter(l -> l.getId().equals(labelId))
        .findFirst()
        .orElse(null);
    assertAll(
        () -> assertNotNull(createdLabel),
        () -> assertThat(createdLabel.getName(), is("New Label")),
        () -> assertThat(createdLabel.getCoordinates(), allOf(
            hasProperty("lat", is(100.0)),
            hasProperty("lon", is(100.0))
        ))
    );
  }

  @Test(groups = "create-update")
  public void testUpdate() {
    Long labelId = testLabels.get(0).getId();
    LabelData updatedLabelData = new LabelData("Updated Label", new CoordinateDto("110.0, 110.0"));

    labelService.update(labelId, updatedLabelData);

    Label updatedLabel = labelService.getById(labelId);
    assertAll(
        () -> assertThat(updatedLabel, both(
            hasProperty("name", is("Updated Label"))
        ).and(
            hasProperty("coordinates", allOf(
                    hasProperty("lat", is(110.0)),
                    hasProperty("lon", is(110.0))
            ))
        ))
    );
  }

  @Test(groups = "create-update")
  public void testUpdateThrowLabelIsNotFoundException() {
    LabelData labelData = new LabelData("Non-existent Label", new CoordinateDto("120.0, 120.0"));
    Exception exception = assertThrows(InternalException.class, () -> labelService.update(-1L, labelData));

    assertThat(exception.getMessage(), is(Exceptions.LABEL_IS_NOT_FOUND.getMessage()));
  }

  @Test(groups = "delete")
  public void testDelete() {
    Long labelIdToDelete = testLabels.get(0).getId();
    labelService.delete(labelIdToDelete);

    List<Label> labels = labelService.getAll();
    assertThat(labels, hasSize(5));

    Optional<Label> deletedLabel = labelRepository.findById(labelIdToDelete);
    assertSame(deletedLabel, Optional.empty());
  }

  @Test(groups = "delete")
  public void testDeleteThrowLabelIsNotFoundException() {
    Exception exception = assertThrows(InternalException.class, () -> labelService.delete(-1L));

    assertThat(exception.getMessage(), is(Exceptions.LABEL_IS_NOT_FOUND.getMessage()));
  }

  @Test(groups = "count", dataProvider = "provideNamesToSearch")
  public void testGetByName(String name, boolean expectedExists) {
    Optional<Label> label = labelService.getByName(name);
    assertEquals(expectedExists, label.isPresent());
  }

  @DataProvider(name = "provideNamesToSearch")
  public static Object[][] provideNamesToSearch() {
    return new Object[][] {
        {"Label 1", true},
        {"Non-existent Label", false}
    };
  }
}
