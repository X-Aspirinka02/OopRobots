package state;

import java.util.*;

/**
 * локализация (смена языка)
 */
public class LocalizationState implements StateRestorable {
    /**
     * мапа для сохранения состояния
     */
    private final PrefixFilteredMap local = new PrefixFilteredMap("leng");
    /**
     * пакет ресурсов для обновления языка
     */
    private ResourceBundle resourceBundle;
    /**
     * текущая локаль (текущий язык)
     */
    private Locale currentLocale;
    /**
     * мапа для хранения подписанных слушателей
     */
    private final List<LanguageChangeListener> listeners = new ArrayList<>();

    public LocalizationState() {
        this.currentLocale = new Locale("ru");
        reloadBundle();
    }

    /**
     * регистрируем нового слушателя
     * @param listener слушатель
     */
    public void registerLanguageChangeListener(LanguageChangeListener listener) {
        listeners.add( listener);
    }

    /**
     * изменяет язык
     * @param newLocale новая локаль (новый язык)
     */
    public void changeLanguage(Locale newLocale) {
        this.currentLocale = newLocale;
        reloadBundle();
        local.updateMapLanguage(currentLocale.getLanguage());
        for (LanguageChangeListener listener : listeners) {
            listener.onLanguageChanged();
        }
    }

    /**
     * достать новый пакет ресурсов для обновления языка
     */
    private void reloadBundle() {
        this.resourceBundle = ResourceBundle.getBundle(
                "localization/language",
                currentLocale
        );
    }

    /**
     * взять строку, меняющуюся в зависимости от локализации
     * @param str строка
     * @return локализованная строка
     */
    public String localStr(String str) {
        try {
            return resourceBundle.getString(str);
        } catch (Exception e) {
            return "[" + str + "]";
        }
    }

    /**
     * сохранить текущее состояние локализации
     */
    public void saveProp(){
        local.addToStore();
    }

    /**
     * получает сохраненное состояние локализации
     */
    public void getProp(){
        currentLocale = new Locale(local.takeFromStore().get("language"));
        reloadBundle();
    }
}

