package com.platform.webservice.service;

import com.platform.webservice.model.Item;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ItemService {

    private final List<Item> items = new CopyOnWriteArrayList<>();

    public ItemService() {
        items.add(new Item(UUID.randomUUID().toString(), "Initial Item 1", "Default sample item #1", "system"));
        items.add(new Item(UUID.randomUUID().toString(), "Initial Item 2", "Default sample item #2", "system"));
        items.add(new Item(UUID.randomUUID().toString(), "Initial Item 3", "Default sample item #3", "system"));
    }

    public List<Item> getAllItems() {
        return new ArrayList<>(items);
    }

    public Item addItem(Item item, String username) {
        String id = (item.getId() == null || item.getId().isBlank()) ? UUID.randomUUID().toString() : item.getId();
        Item newItem = new Item(id, item.getName(), item.getDescription(), username);
        items.add(newItem);
        return newItem;
    }

    public boolean deleteItem(String id) {
        return items.removeIf(item -> item.getId().equals(id));
    }
}

