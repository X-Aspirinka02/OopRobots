package log;


import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * коллекция для хранения сообщений
 * + имеет ограниченный размер
 * + потокобезопасна, т к используется synchronized
 * + есть доступ к части данных (метод subList)
 * + итератор потокобезопасный (возвращает итератор от копии)
 * + добавление данных за O(1)
 * не соответствует 4 требованию
 * - subList() имеет сложность O(n)
 * - для взятия итератора создаётся копия (опять O(n))
 */
public class LogList implements Iterable<LogEntry> {
    private final ArrayDeque<LogEntry> messages = new ArrayDeque<>();
    private final int queueLength;

    public LogList(int queueLength) {
        this.queueLength = queueLength;
    }

    /**
     * добавление сообщения
     * @param message сообщение
     */
    public void addLog(LogEntry message) {
        synchronized (messages) {
            messages.addLast(message);
            if (messages.size() > queueLength) {
                messages.removeFirst();
            }
        }
    }

    /**
     * получение подмассива сообщений
     * @param startFrom начало массива
     * @param indexTo конец массива
     * @return подмассив
     */
    public ArrayDeque<LogEntry> subList(int startFrom, int indexTo){
        synchronized (messages){
            if (startFrom < 0 || indexTo > getSize() || startFrom > indexTo) {
                throw new IllegalArgumentException("Не удалось взять подсписок сообщений. Некорректный индекс.");
            }

            ArrayDeque<LogEntry> subDeque = new ArrayDeque<>();
            Iterator<LogEntry> iterator = iterator();

            int currentIndex = 0;
            while (iterator.hasNext() && currentIndex < indexTo) {
                LogEntry element = iterator.next();

                subDeque.add(element);

                currentIndex++;
            }

            return subDeque;
        }
    }

    /**
     * получение размера массива
     * @return размер массива
     */
    public int getSize(){
        return messages.size();
    }

    /**
     * получение итератора массива
     * @return  итератор
     */
    public synchronized Iterator<LogEntry> iterator() {
        return new Iterator<>() {
            final ArrayDeque<LogEntry> copyListeners = messages.clone();

            private final Iterator<LogEntry> iterator = copyListeners.iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public LogEntry next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return iterator.next();
            }
        };
    }

}
