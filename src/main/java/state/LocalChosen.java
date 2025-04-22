package state;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class LocalChosen implements StateRestorable {
    private final PrefixFilteredMap local = new PrefixFilteredMap("leng");
    private ResourceBundle resourceBundle;
    private Locale currentLocale;
    private final List<LanguageChangeListener> listeners = new ArrayList<>();

    public LocalChosen() {
        this.currentLocale = new Locale("ru");
        reloadBundle();
    }

    public void addLanguageChangeListener(LanguageChangeListener listener) {
        listeners.add(listener);
    }

    public void changeLanguage(Locale newLocale) {
        this.currentLocale = newLocale;
        reloadBundle();
        local.updateMapLanguage(currentLocale.getLanguage());
        for (LanguageChangeListener listener : listeners) {
            listener.onLanguageChanged();
        }
    }

    private void reloadBundle() {
        this.resourceBundle = ResourceBundle.getBundle(
                "localization/language",
                currentLocale
        );
    }

    public String localStr(String str) {
        try {
            return resourceBundle.getString(str);
        } catch (Exception e) {
            return "[" + str + "]";
        }
    }
    public void saveProp(){
        local.addToStore();
    }
    public void getProp(){
        currentLocale = new Locale(local.takeFromStore().get("language"));
        reloadBundle();
    }
}

