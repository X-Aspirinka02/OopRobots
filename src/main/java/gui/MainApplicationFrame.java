package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import log.Logger;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается. 
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame
{
    /**
     * что-то типо имитации рабочего стола (панелька)
     */
    private final JDesktopPane desktopPane = new JDesktopPane();
    /**
    Создаёт главное окно (в том числе игровое окно и для логов)
     */
    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
            screenSize.width  - inset*2,
            screenSize.height - inset*2);

        setContentPane(desktopPane);
        
        
        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400,  400);
        addWindow(gameWindow);
        //устанавливает меню
        setJMenuBar(generateMenuBar());
        //закрытие окна при крестике???
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                closingProcessing();
            }
        });
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    /**
     * Создает окно для логов
     * @return окно для логов
     */
    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    /**
     * добавляет новое окно на панель рабочего стола
     * @param frame окно, которое надо добавить
     */
    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }
    //было закомментированно (не используется)
    protected JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        //Set up the lone menu.
        JMenu menu = new JMenu("Document");
        menu.setMnemonic(KeyEvent.VK_D);
        menuBar.add(menu);

        //Set up the first menu item.
        JMenuItem menuItem = new JMenuItem("New");
        menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("new");
//        menuItem.addActionListener(this);
        menu.add(menuItem);

        //Set up the second menu item.
        menuItem = new JMenuItem("Quit");
        menuItem.setMnemonic(KeyEvent.VK_Q);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("quit");
//        menuItem.addActionListener(this);
        menu.add(menuItem);

        return menuBar;
    }
    //до сюда

    /**
     * создаёт меню (полоса сверху)
     * @return меню
     */
    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu lookAndFeelMenu = createlookAndFeelMenu();
       JMenu testMenu = createTestMenu();
        JMenu endMenu = createEndMenu();

        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        menuBar.add(endMenu);
        return menuBar;
    }

    /**
     * создает подменю режимов отобрадения
     * @return подменю режимов отобрадения
     */
    private JMenu createlookAndFeelMenu(){
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        {
            JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
            systemLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(systemLookAndFeel);
        }

        {
            JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
            crossplatformLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(crossplatformLookAndFeel);
        }
        return lookAndFeelMenu;
    }

    /**
     * создаёт меню для тестов
     * @return меню для тестов
     */
    private JMenu createTestMenu(){
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        {
            JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
            addLogMessageItem.addActionListener((event) -> {
                Logger.debug("Новая строка");
            });
            testMenu.add(addLogMessageItem);
        }
        return testMenu;
    }

    /**
     * создаёт меню для выхода
     * @return меню для выхода
     */
    private JMenu createEndMenu(){

       JMenu endMenu = new JMenu("Выход");
        {
            endMenu.setMnemonic(KeyEvent.VK_I);
            endMenu.getAccessibleContext().setAccessibleDescription(
                    "Выход из приложения");
            JMenuItem exitMenuItem = new JMenuItem("Выйти", KeyEvent.VK_X);
            exitMenuItem.addActionListener((event) -> closingProcessing());
            endMenu.add(exitMenuItem);
        }
        return endMenu;
    }

    /**
     * пока не знаю для чего
     * @param className
     */
    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }

    /**
     * обрабатывает закрытие окна
     */
    private void closingProcessing(){
        {
            int result = JOptionPane.showConfirmDialog(
                    null,
                    "Вы действительно хотите выйти из приложения?",
                    "Подтверждение выхода",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (result == JOptionPane.YES_OPTION) {
                System.exit(0);

            } else {
                Logger.debug("Пользователь решил не выходить.");
            }
        }
    }
}
