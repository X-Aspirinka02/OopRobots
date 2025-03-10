package gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Properties;

/**
 * глобальный словарь для хранения состояния всех окон
 */
public class StateStore {
    String filePath = System.getProperty("user.home") + "\\r\\state.properties";
    /**
     * сохраняет информацию окон в хранилище
     */
    public void setInfo(AbstractMap<String, String> windowMap) {
        Properties properties = new Properties();

        // Заполняем свойства из карты
        for (AbstractMap.Entry<String, String> entry: windowMap.entrySet()) {
            properties.setProperty(entry.getKey(), entry.getValue());
        }

        // Сохраняем свойства в файл
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            properties.store(outputStream, "Properties File");
            System.out.println("Данные успешно сохранены в файл: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * получает информацию окон из хранилища
     */
    public AbstractMap<String, String> getInfo() {

        isFirstStart();

        Properties properties = new Properties();
        AbstractMap<String, String> newWindowMap = new HashMap<>();

        // Загружаем свойства из файла
        try (FileInputStream inputStream = new FileInputStream(filePath)) {
            properties.load(inputStream);
            for (String key : properties.stringPropertyNames()) {
                newWindowMap.put(key, properties.getProperty(key));
            }
            System.out.println("Данные успешно загружены из файла: " + filePath);
        } catch (IOException e) {
            System.out.println("Файл не найден, будет создан новый: " + filePath);
        }

        return newWindowMap;
    }

    private void isFirstStart(){
        // Создаем директорию, если она не существует
        File directory = new File(System.getProperty("user.home") + "\\r");
        if (!directory.exists()) {
            directory.mkdirs(); // Создает директорию и все необходимые родительские директории
        }

        // Создаем карту с значениями по умолчанию
        AbstractMap<String, String> defaultValues = new HashMap<>();
        defaultValues.put("game.width", "400");
        defaultValues.put("game.height", "400");
        defaultValues.put("game.x", "100");
        defaultValues.put("game.y", "200");
        defaultValues.put("game.isIcon", "false");

        // Проверяем, существует ли файл
        File propertiesFile = new File(filePath);
        if (!propertiesFile.exists()) {
            // Если файл не существует, создаем его и заполняем значениями по умолчанию
            setInfo(defaultValues);
        }
    }
    }

