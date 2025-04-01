package gui;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * контроллер робота
 */
public class ControllerRobot {
    /**
     * модель робота
     */
    private final GameMoved model;

    /**
     * конструктор
     * @param model модель для подсчета изменений координат
     */
    ControllerRobot(GameMoved model){
        this.model = model;
        Timer m_timer = initTimer();
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                model.onModelUpdateEvent();
            }
        }, 0, 10);
    }
    /**
     * инициализирует таймер для переодической перерисовки
     *
     * @return таймер
     */
    private static Timer initTimer() {
        return new Timer("events generator", true);
    }

    /**
     * передает модели информацию об изменениях
     * @param point новая точка
     */
    public void setChangesModel(Point point){
        model.setTargetPosition(point);
    }

}
