package com.customermanagement.controller;

import com.customermanagement.domain.ItemResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ItemController {

    @GetMapping("/item/{id}")
    public ItemResponse getItem(@PathVariable("id") int id){
        ItemResponse build = ItemResponse.builder().itemId(1).itemName("Tacos").itemDesc("Tacos contains roll and stuffed vegetables").build();
        ItemResponse build1 = ItemResponse.builder().itemId(2).itemName("Burger").itemDesc("Burger contains cheese and stuffed potato").build();
        Map<Integer, ItemResponse> itemId = Map.of(build.getItemId(), build, build1.getItemId(), build1);
        return itemId.get(id);
    }
}
