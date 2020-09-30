package ru.oop.service;

import org.apache.commons.io.FileUtils;
import ru.oop.application.EmbeddedApplication;
import ru.oop.service.ApplicationZipService.ExtractedFile;
import ru.oop.utils.FxException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;

import static ru.oop.service.ApplicationZipService.EMBEDDED_APP_FILENAME;

/**
 * Отвечает за деплой приложения
 *
 * @author vpyzhyanov
 * @since 30.09.2020
 */
public class ApplicationDeployment
{
    private final static String CODE_PREFIX = "application-";
    private static final String XML_SUFFIX = ".xml";

    /**
     * Скопировать файлы в указанную директорию
     * @param files файлы в виде Map: имя файла -> поток данных
     * @param outputFolder директория, в которую будет произведено копирование
     * @throws IOException при ошибке копирования
     */
    private static void copyFilesToDir(Map<String, ExtractedFile> files, String outputFolder)
            throws IOException
    {
        for (Entry<String, ExtractedFile> entry : files.entrySet())
        {
            String fileName = entry.getKey();

            Path newFilePath = Paths.get(outputFolder, fileName);
            InputStream inputStream = entry.getValue().getData();
            FileUtils.copyToFile(inputStream, newFilePath.toFile());
        }
    }

    /**
     * Получить название каталога, в котором будет находиться внутреннее приложение, работающее на стороне клиента
     * @param appCode код приложения
     * @return название каталога
     */
    private static String getAppFolderName(String appCode)
    {
        return CODE_PREFIX + appCode;
    }

    /**
     * Удалить xml файлы из files
     */
    private static void removeXmlFiles(Map<String, ExtractedFile> files)
    {
        for (String fileName : files.keySet())
        {
            if (fileName.endsWith(XML_SUFFIX))
            {
                files.remove(fileName);
            }
        }
    }

    private final ApplicationZipService applicationZipService =
            new ApplicationZipServiceImpl();

    /**
     * Запустить приложение
     */
    public void startApplication(EmbeddedApplication app)
    {
        System.out.println("Запуск " + app.getCode());
        if (app.getFile() == null || !app.getFile().exists())
        {
            throw new FxException("Файл приложения не найден");
        }

        try
        {
            if (!isAppDeployed(app))
            {
                deployApp(app);
            } else {
                System.out.println("Приложение уже запущено");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
            // Если не удалось задеплоить приложение, то нужно удалить созданную директорию,
            // чтобы приложение не считалось запущенным (см. метод isAppDeployed())
            stopApplication(app);
            throw e;
        }
    }

    /**
     * Остановить приложение
     */
    public void stopApplication(EmbeddedApplication app)
    {
        System.out.println("Остановка " + app.getCode());
        try
        {
            FileUtils.deleteDirectory(getAppDirectory(app));
        }
        catch (IOException e)
        {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Деплой приложения в Tomcat
     * <ol>
     *     <li>Извлечь файлы из архива за исключением xml файлов</li>
     * </ol>
     */
    private void deployApp(EmbeddedApplication app)
    {
        applicationZipService.extractFilesAndDoAction(app, files ->
        {
            try
            {
                String outputFolder = getAppPath(app);
                new File(outputFolder).mkdirs();

                removeXmlFiles(files);
                copyFilesToDir(files, outputFolder);
            }
            catch (IOException e)
            {
                String msg = "Ошибка чтения файла встроенного приложения";
                e.printStackTrace(System.err);
                throw new FxException(msg, e);
            }
        });
    }

    private static File getAppDirectory(EmbeddedApplication app)
    {
        return new File(getAppPath(app));
    }

    private static String getAppPath(EmbeddedApplication app)
    {
        return Paths.get("target",
                getAppFolderName(app.getCode())).toString();
    }

    /**
     * Развёрнуто ли приложение<br>
     * Определяется по наличию директории приложения и наличию в нём файла index.html
     */
    private boolean isAppDeployed(EmbeddedApplication app)
    {
        File appFile = getAppDirectory(app);

        return appFile.exists() && appFile.isDirectory()
                && new File(appFile, EMBEDDED_APP_FILENAME).exists();
    }
}
