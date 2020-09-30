package ru.oop.application;

import org.apache.commons.collections4.CollectionUtils;
import ru.oop.service.ApplicationZipService;
import ru.oop.utils.FxException;
import ru.oop.utils.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Отвечает за создание встроенного приложения.
 * Делает все необходимые проверки.
 *
 * @author vpyzhyanov
 * @since 30.09.2020
 */
public class EmbeddedApplicationCreator
{
    public EmbeddedApplication create(String code, Path filePath) {
        File file = filePath.toFile();
        if (!file.exists()) {
            throw new FxException(String.format("Файл %s не существует", filePath));
        }
        EmbeddedApplication application = new EmbeddedApplication(code);
        application.setFile(file);
        try
        {
            checkApplicationFormat(application, Files.readAllBytes(filePath));
        }
        catch (IOException e)
        {
            throw new FxException(String.format("Не удалось прочитать файл %s", filePath), e);
        }
        return application;
    }

    private void checkApplicationFormat(final EmbeddedApplication application, byte[] content) throws FxException
    {
        Predicate<Set<String>> hasIndexHtml = fileNames ->
                fileNames.contains(ApplicationZipService.EMBEDDED_APP_FILENAME);
        validateZip(application, content, hasIndexHtml);
    }

    /**
     * Валидация файла встроенного приложения
     * <ol>
     *   <li>Формата файла: файл должен быть формата ZIP и не пустым</li>
     *   <li>Проверка наличия обязательных файлов (настраиваемая)</li>
     * </ol>
     * @param application встроенное приложение
     * @param content содержимое файла встроенного приложения
     * @param requiredFileNamesPredicate условие на обязательные файлы
     * @throws FxException при ошибке валидации
     */
    private void validateZip(EmbeddedApplication application, byte[] content,
            Predicate<Set<String>> requiredFileNamesPredicate) throws FxException
    {
        String fileIsNotZippedMsg = String.format("Недопустимый формат файла встроенного приложения %s. "
                + "Файл должен быть в формате zip и не пустым.", application.getCode());
        Set<String> fileNames;
        try
        {
            fileNames = ZipUtils.getAllFileNamesFromZip(content);
        }
        catch (Exception e)
        {
            throw new FxException(fileIsNotZippedMsg, e);
        }
        if (CollectionUtils.isEmpty(fileNames))
        {
            throw new FxException(fileIsNotZippedMsg);
        }
        if (!requiredFileNamesPredicate.test(fileNames))
        {
            throw new FxException(String.format("В архиве встроенного приложения %s "
                    + "должен присутствовать файл index.html", application.getCode()));
        }
    }
}
