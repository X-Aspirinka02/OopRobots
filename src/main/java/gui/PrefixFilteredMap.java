package gui;


import java.util.AbstractMap;
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
    private StateStore storage = new StateStore();

    /**
     * локальный словарь для окна
     */
    private AbstractMap<String, String> windowMap;

    /**
     * создание
     * @param prefix принадлежность данных к конкретному окну
     */
    public PrefixFilteredMap(String prefix) {
        this.prefix = prefix;
        windowMap = new HashMap<>();
    }

    /**
     * обновление данных при изменении состояния окна
     *
     * @param x      абцисса верхнего левого угла окна
     * @param y      ордината верхнего левого угла окна
     * @param width  ширина
     * @param height высота
     * @param isIcon определитель того, что окно является свернутым
     */
    public void updateMap(int x, int y, int width, int height, boolean isIcon) {
        windowMap.put("x", Integer.toString(x));
        windowMap.put("y", Integer.toString(y));
        windowMap.put("width", Integer.toString(width));
        windowMap.put("height", Integer.toString(height));
        windowMap.put("isIcon", Boolean.toString(isIcon));

    }

    /**
     * добавить состояние окна в хранилище
     */
    public void addToStore() {
        AbstractMap<String, String> windowMapI = new HashMap<>(windowMap);
        for (AbstractMap.Entry<String, String> entry: windowMapI.entrySet()) {
            String key = prefix + '.' + entry.getKey();
            windowMap.put(key, entry.getValue());
            windowMap.remove(entry.getKey());
        }
        storage.setInfo(windowMap);
    }

    /**
     * взять данные состояния из хранилища
     * @return новое состояние окна
     */
     public AbstractMap<String, String> takeFromStore() {
            AbstractMap<String, String> windowMapI = storage.getInfo();
         for (AbstractMap.Entry<String, String> entry: windowMapI.entrySet()) {
             String key = entry.getKey().split("\\.")[1];
             windowMap.put(key, entry.getValue());
         }
         return windowMap;
    }

}
