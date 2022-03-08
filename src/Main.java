import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Main {
    //абсолютный адрес папки сохранений игры
    public static final String PATH = "C:/" +
            "/Users" +
            "/poddu" +
            "/IdeaProjects" +
            "/myHomeworksNetology" +
            "/Java Core" +
            "/Homework3InputOutputStreamsFilesSerialize" +
            "/Homework1.3.1setup" +
            "/Games";
    // лог записи результатов операций
    public static StringBuilder log = new StringBuilder();

    public static void main(String[] args) {
        String zipPath = PATH + "/savegames/zip.zip";//путь к будущему архиву
        GameProgress[] gameProgresses = {
                new GameProgress(96, 6, 23, 425.87),
                new GameProgress(46, 12, 25, 654.56),
                new GameProgress(78, 2, 28, 875.25)
        };
        List<String> pathsSavesList = new ArrayList<>();//список ссылок на файлы сохранения
        String fileSavePath;
//генерация полных имен файлов и сохранение игры с созданным именем
        for (int i = 1; i < 4; i++) {
            fileSavePath = PATH + "/savegames/save" + i + ".dat";
            pathsSavesList.add(fileSavePath);
            saveGame(fileSavePath, gameProgresses[i - 1]);
        }
        zipFile(zipPath, pathsSavesList);
//добавление информации из log в файл temp.txt
        try (FileWriter fileWriter = new FileWriter(PATH + "/temp/temp.txt", true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write(log.toString());
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    //метод создания файлов сохранения игры
    public static void saveGame(String filePath, GameProgress gameProgress) {
        File save = new File(filePath);
//пробуем создать файл с указанным именем и добавим в лог результат операции
        if (!save.exists()) {
            logAppenDate();
            try {
                log.append(save.createNewFile() ? " создание файла " : " неудачная попытка создать файл");
            } catch (IOException exception) {
                System.out.println(exception.getMessage());
                log.append("выброс исключения при создании файла, путь не найден: ");
            }
            logAppendEnd(filePath);
        }
//запишем сериализацию в созданный файл и допишем в лог результат сохранения
        logAppenDate();
        if (save.exists() && save.canWrite()) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(save))) {
                objectOutputStream.writeObject(gameProgress);
                log.append(" игра сохранена в файле ");
            } catch (IOException exception) {
                System.out.println(exception.getMessage());
                log.append(" неудачная попытка сохранить прогресс игры в файл ");
            }
        } else {
            log.append(" попытка сохранения, файл отсутствует или недоступен для записи: ");
        }
        logAppendEnd(filePath);
    }

    //создание zip папки и добавление в нее архивируемых файлов
    public static void zipFile(String pathZip, List<String> savesPaths) {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(pathZip))
        ) {
            for (String savePath : savesPaths) {
                logAppenDate();
                //выделение имени файла из полной строки
                String fileSaveName = savePath.substring(savePath.lastIndexOf('/') + 1);
                zipOutputStream.putNextEntry(new ZipEntry(fileSaveName));
                try (FileInputStream saveInputStream = new FileInputStream(savePath)) {
                    byte[] saveBytes = new byte[saveInputStream.available()];
                    //noinspection ResultOfMethodCallIgnored
                    saveInputStream.read(saveBytes);
                    zipOutputStream.write(saveBytes);
                    log
                            .append(" файл ")
                            .append(fileSaveName)
                            .append(" добавлен в архив ");
                } catch (IOException exception) {
                    System.out.println(exception.getMessage());
                    log
                            .append(" неудачная попытка добавить файл ")
                            .append(fileSaveName)
                            .append(" в архив ");
                }
                zipOutputStream.closeEntry();
                logAppendEnd(pathZip);
            }
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
            logAppenDate();
            log.append(" неудачная попытка создать архив ");
            logAppendEnd(pathZip);
        }
//удаление дублирующих файлов вне архива
        deleteFiles(savesPaths);
    }

    //метод удаления файлов
    public static void deleteFiles(List<String> savesPaths) {
        for (String savePath : savesPaths) {
            File save = new File(savePath);
            if (save.exists() && save.canWrite()) {
                logAppenDate();
                log.append(save.delete() ? " успешно удален файл" : " неудачная попытка удалить файл ");
                logAppendEnd(savePath);
            }
        }
    }

    //добавление в лог даты
    public static void logAppenDate() {
        log.append(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(Calendar.getInstance().getTime()));
    }

    //добавление в лог окончания и перенос строки
    public static void logAppendEnd(String filePath) {
        log
                .append(filePath)
                .append('\n');
    }
}