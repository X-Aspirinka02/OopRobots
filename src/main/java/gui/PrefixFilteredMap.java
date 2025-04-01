package gui;

import java.util.HashMap;

/**
 * Отвечает за локальный словарь окна
 */
public class PrefixFilteredMap {
    /**
     * префикс,показывающий принадлежность данных к конкретному окну
     */
    private final String prefix;
    /**
     * объект для работы с хранилищем
     */
    private final StateStore storage = new StateStore();

    /**
     * локальный словарь для окна
     */
    private final HashMap<String, String> windowMap;

    /**
     * создание
     *
     * @param prefix принадлежность данных к конкретному окну
     */
    public PrefixFilteredMap(String prefix) {
        this.prefix = prefix;
        windowMap = new HashMap<>();
    }

    /**
     * обновление данных при изменении состояния окна (размер)
     *
     * @param width  ширина
     * @param height высота
     */
    public void updateMapSize(int width, int height) {

        windowMap.put("width", Integer.toString(width));
        windowMap.put("height", Integer.toString(height));

    }

    /**
     * обновление данных при изменении состояния окна (положение)
     *
     * @param x абцисса верхнего левого угла окна
     * @param y ордината верхнего левого угла окна
     */
    public void updateMapLocation(int x, int y) {
        windowMap.put("x", Integer.toString(x));
        windowMap.put("y", Integer.toString(y));

    }

    /**
     * обновление данных при изменении состояния окна (свернутое состояние)
     *
     * @param isIcon определитель того, что окно является свернутым
     */
    public void updateMapIcon(boolean isIcon) {

        windowMap.put("isIcon", Boolean.toString(isIcon));

    }

    /**
     * добавить состояние окна в хранилище
     */
    public void addToStore() {
        HashMap<String, String> windowMapI = new HashMap<>(windowMap);
        for (HashMap.Entry<String, String> entry : windowMapI.entrySet()) {
            String key = prefix + '.' + entry.getKey();
            windowMap.put(key, entry.getValue());
            windowMap.remove(entry.getKey());
        }

        storage.setInfo(windowMap, prefix);
    }

    /**
     * взять данные состояния из хранилища
     *
     * @return новое состояние окна
     */
    public HashMap<String, String> takeFromStore() {
        HashMap<String, String> windowMapI = storage.getInfo();
        for (HashMap.Entry<String, String> entry : windowMapI.entrySet()) {
            if (entry.getKey().split("\\.")[0].equals(prefix)) {
                String key = entry.getKey().split("\\.")[1];
                windowMap.put(key, entry.getValue());
            }
        }
        return windowMap;
    }

}
