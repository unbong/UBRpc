package io.unbong.ubrpc.core.registry;

import io.unbong.ubrpc.core.meta.InstanceMeta;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Description
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-03-17 22:54
 */
@Data
@AllArgsConstructor
public class Event {
    List<InstanceMeta> data;
}
