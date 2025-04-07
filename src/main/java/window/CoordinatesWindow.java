package window;

import log.Logger;
import robot.RobotModel;
import state.PrefixFilteredMap;
import state.StateRestorable;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.HashMap;

/**
 * окно координат
 */
public class CoordinatesWindow extends JInternalFrame implements StateRestorable, PropertyChangeListener {
    /**
     * модель для вычисления координат
     */
    private final RobotModel model;
    /**
     * мапа для сохранения состояния окна
     */
    private final PrefixFilteredMap mapState = new PrefixFilteredMap("coor");
    /**
     * текстовые поля в окне
     */
    private final JLabel robotCoordinatesLabel = new JLabel("Робот: (0, 0)");

    /**
     * создаём окно
     *
     * @param model модель для вычисления координат
     */
    public CoordinatesWindow(RobotModel model) {
        super("Координаты робота", true, true, true, true);
        this.model = model;
        model.addPropertyChangeListener(this);


        createPanel();
    }

    /**
     * создаем панельку внутри окна
     */
    private void createPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                mapState.updateMapSize(getWidth(), getHeight());
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                mapState.updateMapLocation(getX(), getY());
            }
        });

        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameIconified(InternalFrameEvent e) {
                mapState.updateMapIcon(true);
            }

            @Override
            public void internalFrameDeiconified(InternalFrameEvent e) {
                mapState.updateMapIcon(false);
            }
        });
        panel.add(robotCoordinatesLabel);

        this.add(panel);
        pack();


    }

    /**
     * сохранить текущее состояние окна
     */
    public void saveProp() {
        mapState.addToStore();
    }

    /**
     * получить и установить предыдущее состояние окна
     */
    public void getProp() {

        HashMap<String, String> mapStartState = mapState.takeFromStore();

        try {
            this.setIcon(Boolean.parseBoolean(mapStartState.get("isIcon")));
        } catch (PropertyVetoException e) {
            Logger.debug("Не удалось установить IsIcon");
        }


        int x = Integer.parseInt(mapStartState.get("x"));
        int y = Integer.parseInt(mapStartState.get("y"));
        int width = Integer.parseInt(mapStartState.get("width"));
        int height = Integer.parseInt(mapStartState.get("height"));


        this.setBounds(x, y, width, height);

    }

    /**
     * переписываем координаты при их изменении
     *
     * @param evt Объект PropertyChangeEvent, описывающий источник события
     *            * и свойство, которое изменилось.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("robotPosition".equals(evt.getPropertyName())) {

            Point robotPosition = (Point) evt.getNewValue();
            robotCoordinatesLabel.setText("Робот: (" + robotPosition.x + ", " + robotPosition.y + ")");
        }
    }

}
