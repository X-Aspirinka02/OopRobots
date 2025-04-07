package robot;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * модель для вычисления координат
 */
public class RobotModel {
    /**
     * координата робота X
     */
    private volatile double m_robotPositionX = 100;
    /**
     * координата робота Y
     */
    private volatile double m_robotPositionY = 100;
    /**
     * направление робота
     */
    private volatile double m_robotDirection = 0;
    /**
     * координата Цели X
     */
    private volatile int m_targetPositionX = 150;
    /**
     * координата Цели Y
     */
    private volatile int m_targetPositionY = 100;
    /**
     * скорость робота
     */
    private static final double maxVelocity = 0.1;
    /**
     * угловая скорость робота
     */
    private static final double maxAngularVelocity = 0.001;
    /**
     * слепая зона движения робота (там, где он ходит плохо)
     */
    private static final double radius_blind = maxVelocity / maxAngularVelocity;
    /**
     * Объект, используемый для управления слушателями изменений свойств.
     * Позволяет добавлять, удалять и уведомлять слушателей о изменениях свойств объекта.
     */
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);


    /**
     * метод для добавления слушателя
     *
     * @param listener слушатель изменения модели
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    /**
     * Уведомление слушателей об изменении позиции робота
     */
    private void fireRobotPositionChanged() {
        support.firePropertyChange("robotPosition", null, new Point(round(m_robotPositionX), round(m_robotPositionY)));
    }

    /**
     * Уведомление слушателей об изменении позиции цели
     */
    private void fireTargetPositionChanged() {
        support.firePropertyChange("targetPosition", null, new Point(m_targetPositionX, m_targetPositionY));
    }

    /**
     * Установка позиции цели с уведомлением слушателей
     *
     * @param p точка с новыми координатами
     */
    public void setTargetPosition(Point p) {
        int oldX = m_targetPositionX;
        int oldY = m_targetPositionY;
        m_targetPositionX = p.x;
        m_targetPositionY = p.y;
        if (oldX != m_targetPositionX || oldY != m_targetPositionY) {
            fireTargetPositionChanged();
        }
    }

    /**
     * Вычисляет евклидово расстояние между двумя точками на плоскости.
     *
     * @param x1 Координата X первой точки.
     * @param y1 Координата Y первой точки.
     * @param x2 Координата X второй точки.
     * @param y2 Координата Y второй точки.
     * @return Расстояние между точками.
     */
    private static double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    /**
     * Обрабатывает событие обновления модели.
     * Вычисляет расстояние до цели, определяет направление и скорость движения робота,
     * а затем обновляет позицию и направление робота.
     * Если робот находится достаточно близко к цели, движение прекращается.
     */
    protected void onModelUpdateEvent() {
        double distance = distance(m_targetPositionX, m_targetPositionY,
                m_robotPositionX, m_robotPositionY);
        if (distance < 0.5) {
            return;
        }
        double velocity = maxVelocity;
        double angleToTarget = angleTo(m_robotPositionX, m_robotPositionY, m_targetPositionX, m_targetPositionY);
        double angularVelocity = 0;
        if (angleToTarget > m_robotDirection) {
            angularVelocity = maxAngularVelocity;
        }
        if (angleToTarget < m_robotDirection) {
            angularVelocity = -maxAngularVelocity;
        }

        moveRobot(velocity, angularVelocity, 10);
        fireRobotPositionChanged();
    }

    /**
     * Вычисляет угол между текущей позицией робота и позицией цели.
     *
     * @param fromX Координата X текущей позиции робота.
     * @param fromY Координата Y текущей позиции робота.
     * @param toX   Координата X целевой позиции.
     * @param toY   Координата Y целевой позиции.
     * @return Угол в радианах, нормализованный в диапазоне [0, 2π).
     */
    private static double angleTo(double fromX, double fromY, double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;

        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    /**
     * Нормализует угол в радианах, приводя его к диапазону [0, 2π).
     *
     * @param angle Угол в радианах.
     * @return Нормализованный угол.
     */
    static double asNormalizedRadians(double angle) {
        while (angle < 0) {
            angle += 2 * Math.PI;
        }
        while (angle >= 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    /**
     * Ограничивает значение заданными минимальным и максимальным пределами.
     *
     * @param value Значение для ограничения.
     * @param min   Минимальное допустимое значение.
     * @param max   Максимальное допустимое значение.
     * @return Значение, ограниченное пределами [min, max].
     */
    private static double applyLimits(double value, double min, double max) {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    /**
     * Обновляет позицию и направление робота на основе заданной скорости и угловой скорости.
     * Учитывает ограничения на скорость и угловую скорость.
     *
     * @param velocity        Линейная скорость робота.
     * @param angularVelocity Угловая скорость робота.
     * @param duration        Время движения.
     */
    private void moveRobot(double velocity, double angularVelocity, double duration) {
        velocity = applyLimits(velocity, 0, maxVelocity);
        angularVelocity = applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);


        final double directionSin = Math.sin(m_robotDirection);
        final double directionCos = Math.cos(m_robotDirection);


        final double blindZone1CenterX = m_robotPositionX - radius_blind * directionSin;
        final double blindZone1CenterY = m_robotPositionY + radius_blind * directionCos;
        final double blindZone2CenterX = m_robotPositionX + radius_blind * directionSin;
        final double blindZone2CenterY = m_robotPositionY - radius_blind * directionCos;


        double distance1 = distance(m_targetPositionX, m_targetPositionY, blindZone1CenterX, blindZone1CenterY);
        double distance2 = distance(m_targetPositionX, m_targetPositionY, blindZone2CenterX, blindZone2CenterY);
        if (distance1 < radius_blind || distance2 < radius_blind) {

            angularVelocity *= -1;
        }


        double newX = m_robotPositionX + velocity / angularVelocity *
                (Math.sin(m_robotDirection + angularVelocity * duration) -
                        Math.sin(m_robotDirection));
        if (!Double.isFinite(newX)) {
            newX = m_robotPositionX + velocity * duration * Math.cos(m_robotDirection);
        }
        double newY = m_robotPositionY - velocity / angularVelocity *
                (Math.cos(m_robotDirection + angularVelocity * duration) -
                        Math.cos(m_robotDirection));
        if (!Double.isFinite(newY)) {
            newY = m_robotPositionY + velocity * duration * Math.sin(m_robotDirection);
        }
        m_robotPositionX = newX;
        m_robotPositionY = newY;
        double newDirection = asNormalizedRadians(m_robotDirection + angularVelocity * duration);
        m_robotDirection = newDirection;
    }

    /**
     * Округляет значение до ближайшего целого числа.
     *
     * @param value Значение для округления.
     * @return Округленное целое число.
     */
    private static int round(double value) {
        return (int) (value + 0.5);
    }

    public double getRobotPositionX() {
        return m_robotPositionX;
    }

    public double getRobotPositionY() {
        return m_robotPositionY;
    }

    public double getRobotDirection() {
        return m_robotDirection;
    }

    public int getTargetPositionX() {
        return m_targetPositionX;
    }

    public int getTargetPositionY() {
        return m_targetPositionY;

    }
}


