package log;


import java.util.Collections;
import java.util.WeakHashMap;

/**
 * Что починить:
 * 1. Этот класс порождает утечку ресурсов (связанные слушатели оказываются
 * удерживаемыми в памяти)
 * 2. Этот класс хранит активные сообщения лога, но в такой реализации он 
 * их лишь накапливает. Надо же, чтобы количество сообщений в логе было ограничено 
 * величиной m_iQueueLength (т.е. реально нужна очередь сообщений 
 * ограниченного размера)
 * СДЕЛАНО
 */
public class LogWindowSource
{
    /**
     * сообщения
     */
    private final LogList m_messages;
    /**
     * слушатели
     */
    private final WeakHashMap<String, LogChangeListener> m_listeners;
    private volatile LogChangeListener[] m_activeListeners;
    
    public LogWindowSource(int iQueueLength) 
    {

        m_messages = new LogList(iQueueLength);
        m_listeners = new WeakHashMap<>();
    }

    /**
     * регистрирует нового слушателя
     * @param listener новый слушатель
     */
    public void registerListener(LogChangeListener listener)
    {
        synchronized(m_listeners)
        {
            m_listeners.put(listener.toString(), listener);
            m_activeListeners = null;
        }
    }

    /**
     * удаление зарегестрированного слушателя
     * @param listener слушатель
     */
    public void unregisterListener(LogChangeListener listener) {
        synchronized (m_listeners) {
                m_listeners.remove(listener.toString());
                m_activeListeners = null;
            }
    }

    /**
     * добавляет сообщение в лог
     * @param logLevel тип лога
     * @param strMessage текст лога
     */

    public void append(LogLevel logLevel, String strMessage)
    {
        LogEntry entry = new LogEntry(logLevel, strMessage);
        m_messages.addLog(entry);


        LogChangeListener[] activeListeners = m_activeListeners;
        if (activeListeners == null) {
            synchronized (m_listeners) {
                if (m_activeListeners == null) {


                    activeListeners = m_listeners.values().toArray(new LogChangeListener[0]);
                    m_activeListeners = activeListeners;
                }
            }
        }
        if (activeListeners != null) {
            for (LogChangeListener listener : activeListeners) {
                listener.onLogChanged();
            }
        }
    }

    /**
     * возвращает текущее количество сообщений в логе
     * @return количество сообщений
     */
    public int size()
    {
        return m_messages.getSize();
    }

    /**
     * возвращает подсписок сообшений
     * @param startFrom начало
     * @param count количество символов
     * @return подсписок сообщений лога
     */
    public Iterable<LogEntry> range(int startFrom, int count)
    {
        if (startFrom < 0 || startFrom >= size())
        {
            return Collections.emptyList();
        }
        int indexTo = Math.min(startFrom + count, size());
        return m_messages.subList(startFrom, indexTo);
    }

    /**
     * возвращает все сообщения в логе
     * @return все сообщения
     */
    public Iterable<LogEntry> all()
    {
        return m_messages;
    }
}
