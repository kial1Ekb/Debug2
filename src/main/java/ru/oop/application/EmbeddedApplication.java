package ru.oop.application;

import java.io.File;

/**
 * Встроенное приложение
 *
 * @author vpyzhyanov
 * @since 30.09.2020
 */
public class EmbeddedApplication {
    private String code;
    private File file;

    EmbeddedApplication(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    void setCode(String code) {
        this.code = code;
    }

    public File getFile() {
        return file;
    }

    void setFile(File file) {
        this.file = file;
    }
}
