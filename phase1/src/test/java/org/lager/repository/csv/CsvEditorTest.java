package org.lager.repository.csv;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

@DisplayName("CSV Editor")
class CsvEditorTest implements WithAssertions {

    @Test
    @DisplayName("loads non existing CSV File")
    void loadNonExisting() {
        CsvEditor csvEditor = new CsvEditor("nonExistingFile.csv", "any");

        assertThatThrownBy(csvEditor::loadFromFile)
                .isInstanceOf(IOException.class);
    }

    @Test
    @DisplayName("writes and loads empty list")
    void emptyList() throws IOException {
        CsvEditor csvEditor = new CsvEditor("csvEditorTest.csv", "number,name");

        csvEditor.saveToFile(List.of());
        assertThat(csvEditor.loadFromFile()).isEmpty();
    }

    @Test
    @DisplayName("writes and loads non-empty list")
    void nonEmptyList() throws IOException {
        CsvEditor csvEditor = new CsvEditor("csvEditorTest.csv", "number,name");

        csvEditor.saveToFile(List.of("100,Object One", "123,Object Two"));
        assertThat(csvEditor.loadFromFile()).containsExactlyInAnyOrder("100,Object One", "123,Object Two");
    }
}