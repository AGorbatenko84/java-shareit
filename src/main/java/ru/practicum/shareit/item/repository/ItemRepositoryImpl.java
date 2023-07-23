/*package ru.practicum.shareit.item.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Qualifier("memory")
@Component
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> repository;

    private Long itemId;

    public ItemRepositoryImpl() {
        this.repository = new HashMap<>();
        this.itemId = 0L;
    }

    public Map<Long, Item> getAllItems() {
        return repository;
    }

    @Override
    public List<Item> findByUserId(Long userId) {
        return repository.values()
                .stream()
                .filter(item -> userId.equals(item.getUserId()))
                .collect(Collectors.toList());
    }

    @Override
    public Item getItemById(Long itemId) {
        return repository.get(itemId);
    }

    @Override
    public Item save(Long userId, Item item) {
        itemId++;
        item.setId(itemId);
        item.setUserId(userId);
        repository.put(itemId, item);
        return item;
    }

    @Override
    public Item update(Item item) {
        repository.put(item.getId(), item);
        return item;
    }

    @Override
    public void deleteByItemId(Long itemId) {
        repository.remove(itemId);

    }
}
*/