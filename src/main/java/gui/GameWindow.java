package gui;

import log.Logger;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.util.AbstractMap;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;

public class GameWindow extends JInternalFrame implements StateRestorable
{
    private final GameVisualizer m_visualizer;
    private final PrefixFilteredMap mapState = new PrefixFilteredMap("game");
    public GameWindow() 
    {

        super("Игровое поле", true, true, true, true);
        //getState();
        m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);



        // доюавление обработки события изменения размера окна
        // обновление локальной мапы
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                mapState.updateMapSize( getWidth(), getHeight());
            }
            @Override
            public void componentMoved(ComponentEvent e){
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
        getContentPane().add(panel);
        pack();

    }

    /**
     * сохранить текущее состояние окна
     */
    public void saveState() {
        mapState.addToStore();
    }
    /**
     * получить предыдущее состояние окна
     */
    public void getState() {

        AbstractMap<String, String> mapStartState = mapState.takeFromStore();

        try {
            this.setIcon(Boolean.parseBoolean(mapStartState.get("isIcon")));
        } catch (PropertyVetoException e) {
            Logger.debug("Не удалось установить IsIcon");
        }

        // Получаем координаты и размеры из mapStartState
        int x = Integer.parseInt(mapStartState.get("x"));
        int y = Integer.parseInt(mapStartState.get("y"));
        int width = Integer.parseInt(mapStartState.get("width"));
        int height = Integer.parseInt(mapStartState.get("height"));

        // Устанавливаем размеры и положение окна
        this.setBounds(x, y, width, height);


    }
}
