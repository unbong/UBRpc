package io.unbong.ubrpc.core.registry;

/**
 * Description
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-03-17 22:48
 */
public interface ChangedListener {
    void fire(Event event);
}
