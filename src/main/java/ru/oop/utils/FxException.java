package ru.oop.utils;

/**
 * Общее исключение, которое перехыватывается в корне приложения.
 * Позволяет сохранить работоспособность приложения во время ошибки.
 *
 * @author vpyzhyanov
 * @since 30.09.2020
 */
public class FxException extends RuntimeException
{
    public FxException(String message)
    {
        super(message);
    }

    public FxException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public FxException(Throwable cause)
    {
        super(cause);
    }
}
