package gui;

/**
 * интерфейс для окон, сохраняющих свое положение на экране
 */
public interface StateRestorable {
    /**
     * сохранение состояния окна
     */
    void saveState();
    /**
     * получение состояния окна для восстановления
     */
    void getState();
}
