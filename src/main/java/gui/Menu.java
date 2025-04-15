package gui;

import log.Logger;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * меню (полоса сверху)
 */
public class Menu {
    private final JMenuBar menuBar = new JMenuBar();

    public Menu(MainApplicationFrame frame) {
        JMenu lookAndFeelMenu = createlookAndFeelMenu(frame);
        JMenu testMenu = createTestMenu();
        JMenuItem endMenu = createExitMenu(frame);


        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        menuBar.add(endMenu);
    }

    /**
     * создаёт подменю режимов отображения
     *
     * @param frame главное окно
     * @return подменю
     */
    private JMenu createlookAndFeelMenu(MainApplicationFrame frame) {
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        {
            JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
            systemLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getSystemLookAndFeelClassName(), frame);
                frame.invalidate();
            });
            lookAndFeelMenu.add(systemLookAndFeel);
        }

        {
            JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
            crossplatformLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName(), frame);
                frame.invalidate();
            });
            lookAndFeelMenu.add(crossplatformLookAndFeel);
        }
        return lookAndFeelMenu;
    }


    /**
     * создаёт меню для тестов
     *
     * @return меню для тестов
     */
    private JMenu createTestMenu() {

        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        {
            JMenuItem addLogMessageItem1 = new JMenuItem("Сообщение в лог 1", KeyEvent.VK_S);
            addLogMessageItem1.addActionListener((event) -> Logger.debug("Новая строка 1"));
            JMenuItem addLogMessageItem2 = new JMenuItem("Сообщение в лог 2", KeyEvent.VK_S);
            addLogMessageItem2.addActionListener((event) -> Logger.debug("Новая строка 2"));
            testMenu.add(addLogMessageItem1);
            testMenu.add(addLogMessageItem2);
        }
        return testMenu;
    }

    /**
     * пока не знаю для чего
     */
    private void setLookAndFeel(String className, MainApplicationFrame frame) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(frame);
        } catch (ClassNotFoundException | InstantiationException
                 | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // just ignore
        }
    }

    /**
     * создаёт меню для выхода
     *
     * @return меню для выхода
     */
    private JMenuItem createExitMenu(MainApplicationFrame frame) {

        JMenu exitMenu = new JMenu("Выход");
        exitMenu.setMnemonic(KeyEvent.VK_T);
        exitMenu.getAccessibleContext().setAccessibleDescription(
                "Выход из приложения");
        JMenuItem exitMenuItem = new JMenuItem("Выйти", KeyEvent.VK_X);
        exitMenuItem.addActionListener((event) -> frame.closingProcessing());
        exitMenu.add(exitMenuItem);


        return exitMenu;
    }

    public JMenuBar getMenu() {
        return menuBar;
    }


}
