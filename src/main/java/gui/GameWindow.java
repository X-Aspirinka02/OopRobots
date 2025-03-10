package gui;

import log.Logger;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyVetoException;
import java.util.AbstractMap;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

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
                mapState.updateMap(getX(), getY(), getWidth(), getHeight(), isIcon());
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

        setLocation(Integer.parseInt(mapStartState.get("x")), Integer.parseInt(mapStartState.get("y")));
        setSize(Integer.parseInt(mapStartState.get("width")), Integer.parseInt(mapStartState.get("height")));
        try{
            setIcon(Boolean.getBoolean(mapStartState.get("isIcon")));
        }
        catch (PropertyVetoException e){
            Logger.debug("Не удалось установить IsIcon");
        }


    }
}
