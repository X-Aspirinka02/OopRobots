package gui;

import java.awt.Color;

import robot.ControllerRobot;
import robot.RobotModel;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeListener;

import java.beans.PropertyChangeEvent;

import javax.swing.JPanel;

/**
 * отвечает за рисование робота и цели
 */
public class GameVisualizer extends JPanel implements PropertyChangeListener {
    private final RobotModel model;
    private final ControllerRobot controller;


    /**
     * определяем
     *
     * @param model модель для подсчета изменений координат
     */
    public GameVisualizer(RobotModel model) {
        this.model = model;
        controller = new ControllerRobot(model);
        model.addPropertyChangeListener(this);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                controller.setChangesModel(e.getPoint());
            }
        });

        setDoubleBuffered(true);
    }

    /**
     * слушатель для изменения состояния модели
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *            and the property that has changed.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("targetPosition".equals(evt.getPropertyName()) || "robotPosition".equals(evt.getPropertyName())) {
            repaint();
        }
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

    /**
     * Переопределенный метод для отрисовки компонента.
     *
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        drawRobot(g2d, model.getRobotDirection());
        drawTarget(g2d, model.getTargetPositionX(), model.getTargetPositionY());
    }

    /**
     * Рисует закрашенный овал с центром в указанных координатах.
     */
    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    /**
     * Рисует контур овала с центром в указанных координатах.
     */
    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    /**
     * Отрисовывает робота на экране.
     */
    private void drawRobot(Graphics2D g, double direction) {
        int robotCenterX = round(model.getRobotPositionX());
        int robotCenterY = round(model.getRobotPositionY());
        AffineTransform t = AffineTransform.getRotateInstance(direction, robotCenterX, robotCenterY);
        g.setTransform(t);
        g.setColor(Color.MAGENTA);
        fillOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, robotCenterX + 10, robotCenterY, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX + 10, robotCenterY, 5, 5);
    }

    //оставить
    private void drawTarget(Graphics2D g, int x, int y) {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(t);
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }
}
