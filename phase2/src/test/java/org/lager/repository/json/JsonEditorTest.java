package org.lager.repository.json;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.lager.OrderFixtures.*;

@DisplayName("JSON Editor")
class JsonEditorTest implements WithAssertions {

    @Test
    @DisplayName("loads non existing JSON File")
    void nonExisting() {
        JsonEditor jsonEditor = new JsonEditor("nonExistingFile.json");

        assertThatThrownBy(jsonEditor::loadFromFile)
                .isInstanceOf(IOException.class);
    }

    @Test
    @DisplayName("writes and loads an empty OrderList")
    void emptyOrderList() throws IOException {
        JsonEditor jsonEditor = new JsonEditor("jsonEditorTest.json");

        jsonEditor.saveToFile(List.of());
        assertThat(jsonEditor.loadFromFile()).isEmpty();
    }

    @Test
    @DisplayName("writes and loads non-empty List")
    void nonEmptyList() throws IOException {
        JsonEditor jsonEditor = new JsonEditor("jsonEditorTest.json");

        jsonEditor.saveToFile(List.of(defaultOrderAsJson(), anotherOrderAsJson()));

        assertThat(jsonEditor.loadFromFile()).containsExactlyInAnyOrder(defaultOrderAsJson(), anotherOrderAsJson());
    }
}