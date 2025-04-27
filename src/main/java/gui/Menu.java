package gui;

import log.Logger;
import state.LocalizationState;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.Locale;

/**
 * меню (полоса сверху)
 */
public class Menu {
    private final JMenuBar menuBar = new JMenuBar();
    private final LocalizationState language;

    public Menu(MainApplicationFrame frame, LocalizationState language) {
        this.language = language;
        JMenu lookAndFeelMenu = createlookAndFeelMenu(frame);
        JMenu testMenu = createTestMenu();
        JMenu languageMenu = createLanguageMenu();
        JMenuItem endMenu = createExitMenu(frame);


        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        menuBar.add(languageMenu);
        menuBar.add(endMenu);
    }

    /**
     * создаёт подменю режимов отображения
     *
     * @param frame главное окно
     * @return подменю
     */
    private JMenu createlookAndFeelMenu(MainApplicationFrame frame) {
        JMenu lookAndFeelMenu = new JMenu(language.localStr("menu_lf"));
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        {
            JMenuItem systemLookAndFeel = new JMenuItem(language.localStr("menu_lf_ss"), KeyEvent.VK_S);
            systemLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getSystemLookAndFeelClassName(), frame);
                frame.invalidate();
            });
            lookAndFeelMenu.add(systemLookAndFeel);
        }

        {
            JMenuItem crossplatformLookAndFeel = new JMenuItem(language.localStr("menu_lf_us"), KeyEvent.VK_S);
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

        JMenu testMenu = new JMenu(language.localStr("menu_test"));
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        {
            JMenuItem addLogMessageItem1 = new JMenuItem(language.localStr("menu_test1"), KeyEvent.VK_S);
            addLogMessageItem1.addActionListener((event) -> Logger.debug(language.localStr("menu_log_newstr1")));
            JMenuItem addLogMessageItem2 = new JMenuItem(language.localStr("menu_test2"), KeyEvent.VK_S);
            addLogMessageItem2.addActionListener((event) -> Logger.debug(language.localStr("menu_log_newstr2")));
            testMenu.add(addLogMessageItem1);
            testMenu.add(addLogMessageItem2);
        }
        return testMenu;
    }
    /**
     * создает меню для выбора языка
     */
    private JMenu createLanguageMenu(){
        JMenu languageMenu = new JMenu(language.localStr("menu_leng"));
        languageMenu.setMnemonic(KeyEvent.VK_L);

        languageMenu.getAccessibleContext().setAccessibleDescription(language.localStr("menu_leng_s"));
        {
            JMenuItem addLogMessageItem1 = new JMenuItem("Русский");
            addLogMessageItem1.addActionListener((event) -> language.changeLanguage(new Locale("ru")));
            JMenuItem addLogMessageItem2 = new JMenuItem("English");
            addLogMessageItem2.addActionListener((event) -> language.changeLanguage(new Locale("en")));
            languageMenu.add(addLogMessageItem1);
            languageMenu.add(addLogMessageItem2);
        }
        return languageMenu;

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

        JMenu exitMenu = new JMenu(language.localStr("menu_ex"));
        exitMenu.setMnemonic(KeyEvent.VK_T);
        exitMenu.getAccessibleContext().setAccessibleDescription(
                "Выход из приложения");
        JMenuItem exitMenuItem = new JMenuItem(language.localStr("menu_ex_item"), KeyEvent.VK_X);
        exitMenuItem.addActionListener((event) -> frame.closingProcessing());
        exitMenu.add(exitMenuItem);


        return exitMenu;
    }

    public JMenuBar getMenu() {
        return menuBar;
    }


}
