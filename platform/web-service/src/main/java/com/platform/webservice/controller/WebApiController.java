package com.platform.webservice.controller;

import com.platform.webservice.dto.UserRegisteredEvent;
import com.platform.webservice.listener.UserRegisteredEventListener;
import com.platform.webservice.model.Item;
import com.platform.webservice.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class WebApiController {

    private final ItemService itemService;
    private final UserRegisteredEventListener eventListener;

    public WebApiController(ItemService itemService, UserRegisteredEventListener eventListener) {
        this.itemService = itemService;
        this.eventListener = eventListener;
    }

    @GetMapping("/hello")
    public Map<String, Object> hello(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaimAsString("username");
        if (username == null) {
            username = jwt.getSubject();
        }
        return Map.of(
                "message", "Hello, " + username + "!",
                "username", username,
                "claims", jwt.getClaims()
        );
    }

    @GetMapping("/items")
    public List<Item> getItems() {
        return itemService.getAllItems();
    }

    @PostMapping("/items")
    public ResponseEntity<Item> addItem(@RequestBody Item item, @AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaimAsString("username");
        if (username == null) {
            username = jwt.getSubject();
        }
        Item created = itemService.addItem(item, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable String id) {
        boolean removed = itemService.deleteItem(id);
        if (removed) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/events/users")
    public List<UserRegisteredEvent> getUserEvents() {
        return eventListener.getRecentEvents();
    }
}

