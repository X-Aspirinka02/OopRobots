package state;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Properties;

/**
 * глобальный словарь для хранения состояния всех окон
 */
public class StateStore {
    private final String filePath = System.getProperty("user.home") + File.separator + "rushkova" + File.separator + "state.properties";
    private final Properties properties = new Properties();

    /**
     * сохраняет информацию окон в хранилище
     *
     * @param windowMap состояние окна
     * @param prefix    префикс,показывающий принадлежность данных к конкретному окну
     */
    public void setInfo(HashMap<String, String> windowMap, String prefix) {
        if (!Objects.equals(prefix, "")) {

            HashMap<String, String> gen = getInfo();
            for (HashMap.Entry<String, String> entry : gen.entrySet()) {
                if (!Objects.equals(entry.getKey().split("//.")[0], prefix)) {
                    properties.setProperty(entry.getKey(), entry.getValue());
                }
            }
        }


        for (HashMap.Entry<String, String> entry : windowMap.entrySet()) {
            properties.setProperty(entry.getKey(), entry.getValue());
        }


        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            properties.store(outputStream, "Properties File State Windows");
            System.out.println("Данные успешно сохранены в файл: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * получает информацию окон из хранилища
     *
     * @return мапа состояний для всех окон
     */
    public HashMap<String, String> getInfo() {

        isFirstStart();


        HashMap<String, String> newWindowMap = new HashMap<>();


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

    /**
     * заполнение дефолтными состояниями, если это 1 запуск
     */
    private void isFirstStart() {

        File directory = new File(System.getProperty("user.home") + "\\rushkova");
        if (!directory.exists()) {
            directory.mkdirs();
        }


        HashMap<String, String> defaultValues = new HashMap<>();
        defaultValues.put("game.width", "860");
        defaultValues.put("game.height", "554");
        defaultValues.put("game.x", "432");
        defaultValues.put("game.y", "130");
        defaultValues.put("game.isIcon", "false");
        defaultValues.put("log.width", "206");
        defaultValues.put("log.height", "452");
        defaultValues.put("log.x", "10");
        defaultValues.put("log.y", "10");
        defaultValues.put("log.isIcon", "false");
        defaultValues.put("gen.width", "1550");
        defaultValues.put("gen.height", "926");
        defaultValues.put("gen.x", "-7");
        defaultValues.put("gen.y", "-7");
        defaultValues.put("gen.isIcon", "false");
        defaultValues.put("coor.width", "214");
        defaultValues.put("coor.height", "90");
        defaultValues.put("coor.x", "234");
        defaultValues.put("coor.y", "32");
        defaultValues.put("coor.isIcon", "false");


        File propertiesFile = new File(filePath);
        if (!propertiesFile.exists()) {

            setInfo(defaultValues, "");
        }
    }
}

