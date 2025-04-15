package log;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

public class LogList implements Iterable {
    private class LogNode<LogEntry> {
        LogEntry message;
        AtomicReference<LogNode<LogEntry>> next;
        AtomicReference<LogNode<LogEntry>> prev;

        private LogNode(LogEntry value) {
            this.message = value;
            next = new AtomicReference<>(null);
            prev = new AtomicReference<>(null);
        }

        public LogEntry getData() {
            return message;
        }

        public LogNode<LogEntry> getNext() {
            return next.get();
        }
    }

    private final AtomicReference<LogNode<LogEntry>> head;
    private final AtomicReference<LogNode<LogEntry>> tail;
    private final int queueLength;
    private int currentSize = 0;

    public LogList(int queueLength) {
        this.tail = new AtomicReference<>(null);
        this.head = new AtomicReference<>(null);
        this.queueLength = queueLength;

    }

    public void addLog(LogEntry newMessage) {
        LogNode<LogEntry> newLogNode = new LogNode<>(newMessage);

        while (true) {
            LogNode<LogEntry> currentTail = tail.get();

            if (currentTail == null) {
                if (head.compareAndSet(null, newLogNode)) {
                    tail.compareAndSet(null, newLogNode);
                    currentSize++;
                    break;
                }
            } else {

                newLogNode.prev.set(currentTail);
                if (tail.compareAndSet(currentTail, newLogNode)) {
                    currentTail.next.set(newLogNode);
                    currentSize++;
                    break;
                }
            }
        }
        if (currentSize >= queueLength + 1) {
            while (true) {
                LogNode<LogEntry> currentHead = head.get();

                LogNode<LogEntry> nextNodeHead = currentHead.next.get();

                if (head.compareAndSet(currentHead, nextNodeHead)) {
                    nextNodeHead.prev.set(null);
                    currentSize--;
                    return;
                }
            }
        }
    }

    public LogNode<LogEntry> getHead() {
        return head.get();
    }


    public int getSize() {
        return currentSize;
    }

    public Iterator iterator() {

        return new LogListIterator(this);
    }


    class LogListIterator implements Iterator<LogEntry> {
        LogNode<LogEntry> current;


        public LogListIterator(LogList list) {
            current = list.getHead();
        }

        public boolean hasNext() {
            return current != null;
        }


        public LogEntry next() {
            LogEntry data = current.getData();
            current = current.getNext();
            return data;
        }


    }
}
