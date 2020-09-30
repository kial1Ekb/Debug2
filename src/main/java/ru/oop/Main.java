package ru.oop;

import ru.oop.application.EmbeddedApplication;
import ru.oop.application.EmbeddedApplicationCreator;
import ru.oop.service.ApplicationDeployment;
import ru.oop.utils.FxException;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main
{
    private static ApplicationDeployment applicationDeployment =
            new ApplicationDeployment();
    private static EmbeddedApplicationCreator applicationCreator =
            new EmbeddedApplicationCreator();

    /**
     * Принимает аргументы:
     * <ol>
     *     <li>Команда
     *     <ul>
     *         <li>start - запуск приложения</li>
     *         <li>stop - остановка приложения</li>
     *         <li>restart - остановка и запуск приложения</li>
     *     </ul></li>
     *     <li>Пусть до архива приложения</li>
     * </ol>
     * @param args аргументы
     */
    public static void main(String[] args)
    {
        if (args.length < 2)
        {
            System.err.println("Введите 2 аргумента: команда и файл приложения");
            System.exit(1);
        }
        String command = args[0];
        String file = args[1];

        try{
            Path filePath = Paths.get(file);
            String filename = filePath.getFileName().toString()
                    .replace(".zip", "");
            EmbeddedApplication application = applicationCreator.create(
                    filename, filePath);
            switch (command)
            {
            case "start":
                applicationDeployment.startApplication(application);
                break;
            case "stop":
                applicationDeployment.stopApplication(application);
                break;
            case "restart":
                applicationDeployment.stopApplication(application);
                applicationDeployment.startApplication(application);
                break;
            default:
                System.err.println("Неизвестная команда");
            }
        } catch (FxException e) {
            System.err.println("Ошибка при работе приложения");
            e.printStackTrace(System.err);
        }
    }
}
