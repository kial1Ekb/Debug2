package ru.oop.service;

import ru.oop.application.EmbeddedApplication;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Сервис для работы с архивом встроенного приложения.<br>
 * Позволяет выполнять действия над файлами в архиве, изменять и удалять файлы внутри ахрива
 *
 * @author vpyzhyanov
 * @since 05.02.20
 */
public interface ApplicationZipService {

    /**
     * Файл извлечённый из архива встроенного прилоежния.
     */
    class ExtractedFile {
        private final String name;
        private ByteArrayInputStream data;

        public ExtractedFile(String name, ByteArrayInputStream data) {
            this.name = name;
            this.data = data;
        }

        public ByteArrayInputStream getData() {
            return data;
        }

        public String getName() {
            return name;
        }

        public void setData(ByteArrayInputStream data) {
            this.data = data;
        }
    }

    /**
     * Имя файла встроенного приложения
     */
    public static final String EMBEDDED_APP_FILENAME = "index.html";

    /**
     * Извлечь файлы приложения и выполнить для них действие.<br>
     * Если приложение лицензируемое, то расшифрует файлы приложения.
     *
     * @param app    встроенное приложение
     * @param action действие, которое нужно выполнить над файлами
     */
    void extractFilesAndDoAction(EmbeddedApplication app,
                                 Consumer<Map<String, ExtractedFile>> action);
}