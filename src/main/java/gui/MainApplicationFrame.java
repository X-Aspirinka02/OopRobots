package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

import javax.swing.*;

import log.Logger;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается.
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 */
public class MainApplicationFrame extends JFrame implements StateRestorable {
    /**
     * что-то типо имитации рабочего стола (панелька)
     */
    private final JDesktopPane desktopPane = new JDesktopPane();
    /**
     * мапа для сохранения состояния
     */
    private final PrefixFilteredMap gen = new PrefixFilteredMap("gen");
   private final GameWindow gameWindow = new GameWindow();
    private final LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());

    /**
     * Создаёт главное окно (в том числе игровое окно и для логов)
     */
    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);
        addWindow(logWindow);
        addWindow(gameWindow);


        //устанавливает меню
        setJMenuBar(new Menu(this).getMenu());


        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                closingProcessing();

            }
        });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);


        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                gen.updateMapSize(getWidth(), getHeight());
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                gen.updateMapLocation(getX(), getY());
            }
        });
        this.addPropertyChangeListener("state", evt -> {
            if (evt.getNewValue().equals(JFrame.MAXIMIZED_BOTH)) {
                gen.updateMapIcon(false);
            } else if (evt.getNewValue().equals(JFrame.NORMAL)) {
                Logger.debug("Окно восстановлено.");
            } else if (evt.getNewValue().equals(JFrame.ICONIFIED)) {
                gen.updateMapIcon(true);
            }
        });
        this.pack();
        getPropStateRestorables(this, logWindow, gameWindow);
    }


    /**
     * добавляет новое окно на панель рабочего стола
     *
     * @param frame окно, которое надо добавить
     */
    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

//    //было закомментированно (не используется)
//    protected JMenuBar createMenuBar() {
//        JMenuBar menuBar = new JMenuBar();
//
//        //Set up the lone menu.
//        JMenu menu = new JMenu("Document");
//        menu.setMnemonic(KeyEvent.VK_D);
//        menuBar.add(menu);
//
//        //Set up the first menu item.
//        JMenuItem menuItem = new JMenuItem("New");
//        menuItem.setMnemonic(KeyEvent.VK_N);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_N, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("new");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
//
//        //Set up the second menu item.
//        menuItem = new JMenuItem("Quit");
//        menuItem.setMnemonic(KeyEvent.VK_Q);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("quit");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
//
//        return menuBar;
//    }
//    //до сюда


    /**
     * обрабатывает закрытие окна
     */
    public void closingProcessing() {
        {

            int result = JOptionPane.showConfirmDialog(
                    null,
                    "Вы действительно хотите выйти из приложения?",
                    "Подтверждение выхода",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (result == JOptionPane.YES_OPTION) {

               setPropStateRestorables(this, gameWindow, logWindow);
                Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
                        new WindowEvent(this, WindowEvent.WINDOW_CLOSING));


                desktopPane.setVisible(false);
                dispose();
                System.exit(0);

            } else {
                Logger.debug("Пользователь решил не выходить.");
            }
        }
    }

    public void saveProp() {
        gen.addToStore();
    }

    public void getProp() {
        HashMap<String, String> mapStartState = gen.takeFromStore();

        int x = Integer.parseInt(mapStartState.get("x"));
        int y = Integer.parseInt(mapStartState.get("y"));
        int width = Integer.parseInt(mapStartState.get("width"));
        int height = Integer.parseInt(mapStartState.get("height"));


        if (mapStartState.get("isIcon").equals("true")) {
            this.setExtendedState(JFrame.ICONIFIED);
        } else if (mapStartState.get("isIcon").equals("false")) {
            this.setExtendedState(JFrame.NORMAL);
            this.setBounds(x, y, width, height);
        } else {
            this.setExtendedState(JFrame.MAXIMIZED_BOTH);


        }

        this.setBounds(x, y, width, height);

    }
    private void getPropStateRestorables(StateRestorable... restorables) {
        for (StateRestorable restorable : restorables) {
            restorable.getProp();
        }
    }
    private void setPropStateRestorables(StateRestorable... restorables) {
        for (StateRestorable restorable : restorables) {
            restorable.saveProp();
        }
    }
}