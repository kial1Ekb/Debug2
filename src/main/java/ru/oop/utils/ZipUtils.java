package ru.oop.utils;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Утилитарный класс для работы с архивами
 *
 * @author vpyzhyanov
 * @since 30.09.2020
 */
public class ZipUtils
{
    private static final Charset[] SUPPORTED_CHARSETS = { StandardCharsets.UTF_8,
            Charset.forName("windows-1251"), Charset.forName("MacCyrillic"),
            StandardCharsets.ISO_8859_1 };

    /**
     * Прочитать zip архив и извлечь имена всех файлов (Entries)<br>
     * Поддерживает несколько кодировок для zip архива.
     */
    public static Set<String> getAllFileNamesFromZip(byte[] content)
    {
        int charsetIndex = 0;
        while (charsetIndex < SUPPORTED_CHARSETS.length)
        {
            try
            {
                Set<String> fileNames = new HashSet<>();
                try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(content),
                        SUPPORTED_CHARSETS[charsetIndex]))
                {
                    ZipEntry entry;
                    while ((entry = zis.getNextEntry()) != null)
                    {
                        fileNames.add(entry.getName());
                    }
                }
                catch (IOException e)
                {
                    throw new FxException(e);
                }
                return fileNames;
            }
            catch (IllegalArgumentException e)
            {
                if (e.getCause() != null && e.getCause().getClass().equals(MalformedInputException.class))
                {
                    charsetIndex++;
                }
                else
                {
                    throw e;
                }
            }
        }
        throw new FxException("Unsupported encoding of zip file");
    }

    /**
     * Извлечь файлы из zip архива, поместив их в мапку по именам файлов
     * @param bytes zip архив
     * @return Map, где ключ - имя файла, значение - поток данных этого файла
     * @see #unzipFiles(InputStream)
     */
    public static Map<String, ByteArrayInputStream> unzipFiles(byte[] bytes) throws IOException
    {
        return unzipFiles(new ByteArrayInputStream(bytes));
    }

    /**
     * Извлечь файлы из zip архива, поместив их в мапку по именам файлов.
     * Данные файлов возвращаются в виде потока с поддержкой операции {@linkplain ByteArrayInputStream#reset()},
     * что даёт возможность <b>читать файл несколько раз</b>!<br>
     * Поддерживает несколько кодировок для zip архива.
     * @param inputStream zip архив
     * @return Map, где ключ - имя файла, значение - поток данных этого файла
     * @see #unzipFiles(byte[])
     */
    public static Map<String, ByteArrayInputStream> unzipFiles(InputStream inputStream) throws IOException
    {
        // Переводим входной поток в ByteArrayInputStream с целью возможности делать reset
        ByteArrayInputStream byteArrayInputStream = toByteArrayInputStream(inputStream);
        int charsetIndex = 0;
        while (charsetIndex < SUPPORTED_CHARSETS.length)
        {
            try
            {
                Map<String, ByteArrayInputStream> result = new HashMap<>();
                try (ZipInputStream zin = new ZipInputStream(byteArrayInputStream,
                        SUPPORTED_CHARSETS[charsetIndex]))
                {
                    ZipEntry entry;
                    while ((entry = zin.getNextEntry()) != null)
                    {
                        String name = entry.getName();
                        ByteArrayInputStream is = toByteArrayInputStream(zin);
                        result.put(name, is);
                    }
                }
                return result;
            }
            catch (IllegalArgumentException e)
            {
                if (e.getCause() != null && e.getCause().getClass().equals(MalformedInputException.class))
                {
                    charsetIndex++;
                    byteArrayInputStream.reset();
                }
                else
                {
                    throw e;
                }
            }
        }
        throw new FxException("Unsupported encoding of zip file");
    }

    private static ByteArrayInputStream toByteArrayInputStream(InputStream inputStream) throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtils.copy(inputStream, out);
        return new ByteArrayInputStream(out.toByteArray());
    }
}
