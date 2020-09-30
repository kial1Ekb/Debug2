package ru.oop.service;

import org.apache.commons.io.FileUtils;
import ru.oop.application.EmbeddedApplication;
import ru.oop.utils.FxException;
import ru.oop.utils.ZipUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Реализация {@link ApplicationZipService}
 *
 * @author vpyzhyanov
 * @since 30.09.2020
 */
public class ApplicationZipServiceImpl implements ApplicationZipService
{
    private static void closeStreams(Map<String, ExtractedFile> files) throws IOException
    {
        if (files != null)
        {
            for (ExtractedFile extractedFile : files.values())
            {
                extractedFile.getData().close();
            }
        }
    }

    /**
     * Извлечь архив в виде Map отображающий имя файла на InputStream<br>
     * @param stream zip архив
     * @return файлы в виде Map с ключом в виде имени файла
     */
    private static Map<String, ExtractedFile> extractZip(InputStream stream) throws IOException
    {
        final Map<String, ExtractedFile> extractedFiles = new HashMap<>();
        Map<String, ByteArrayInputStream> files = ZipUtils.unzipFiles(stream);

        if (!files.containsKey(EMBEDDED_APP_FILENAME))
        {
            throw new FxException("Архив должен содержать файл index.html");
        }

        files.forEach((fileName, inputStream) -> extractedFiles.put(
                fileName, new ExtractedFile(fileName, inputStream)));
        return extractedFiles;
    }

    @Override
    public void extractFilesAndDoAction(EmbeddedApplication app,
            Consumer<Map<String, ExtractedFile>> action)
    {
        File file = app.getFile();

        if (file == null)
        {
            throw new FxException("Приложение не найдено");
        }

        try (InputStream content = FileUtils.openInputStream(file))
        {
            Map<String, ExtractedFile> files = null;
            try
            {
                files = extractZip(content);
                action.accept(files);
            }
            finally
            {
                closeStreams(files);
            }
        }
        catch (IOException e)
        {
            String msg = "Ошибка чтения файла встроенного приложения";
            throw new FxException(msg, e);
        }
    }
}
