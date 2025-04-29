package log;
/**
 * интерфейс для слушателя, подписавшегося на изменение логов
 */
public interface LogChangeListener
{
    /**
     * метод, вызывающийся при изменении логов
     */
    public void onLogChanged(); 
}
