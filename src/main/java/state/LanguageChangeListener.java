package state;

/**
 * интерфейс для слушателя, подписавшегося на изменение языка
 */
public interface LanguageChangeListener {
    /**
     * метод, вызывающийся при изменении языка
     */
    void onLanguageChanged();
}
