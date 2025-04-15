package log;

import java.util.ArrayList;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;

/**
 * Что починить:
 * 1. Этот класс порождает утечку ресурсов (связанные слушатели оказываются
 * удерживаемыми в памяти)
 * 2. Этот класс хранит активные сообщения лога, но в такой реализации он 
 * их лишь накапливает. Надо же, чтобы количество сообщений в логе было ограничено 
 * величиной m_iQueueLength (т.е. реально нужна очередь сообщений 
 * ограниченного размера) 
 */
public class LogWindowSource
{
    /**
     * количество сообщений в логе
     */
    private final int m_iQueueLength;
    /**
     * сообщения
     */
    private final LogList m_messages;
    /**
     * слушатели
     */
    private final ArrayList<WeakReference<LogChangeListener>> m_listeners;
    private volatile LogChangeListener[] m_activeListeners;
    
    public LogWindowSource(int iQueueLength) 
    {
        m_iQueueLength = iQueueLength;
        m_messages = new LogList(iQueueLength);
        m_listeners = new ArrayList<>();
    }

    /**
     * регистрирует нового слушателя
     * @param listener новый слушатель
     */
    public void registerListener(LogChangeListener listener)
    {
        synchronized(m_listeners)
        {
            m_listeners.add(new WeakReference<>(listener));
            m_activeListeners = null;
        }
    }

    /**
     * удаление зарегестрированного слушателя
     * @param listener слушатель
     */
    public void unregisterListener(LogChangeListener listener) {
        synchronized (m_listeners) {
                m_listeners.removeIf(weekRef -> weekRef.get() == null || Objects.equals(weekRef.get(), listener));
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

                    List<LogChangeListener> listeners = new ArrayList<>();
                    for (WeakReference<LogChangeListener> weakRef : m_listeners) {
                        LogChangeListener listener = weakRef.get();
                        if (listener != null) {
                            listeners.add(listener);
                        }
                    }
                    activeListeners = listeners.toArray(new LogChangeListener[0]);
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
//    public Iterable<LogEntry> range(int startFrom, int count)
//    {
//        if (startFrom < 0 || startFrom >= m_messages.size())
//        {
//            return Collections.emptyList();
//        }
//        int indexTo = Math.min(startFrom + count, m_messages.size());
//        return m_messages.subList(startFrom, indexTo);
//    }

    /**
     * возвращает все сообщения в логе
     * @return все сообщения
     */
    public Iterable<LogEntry> all()
    {
        return m_messages;
    }
}
