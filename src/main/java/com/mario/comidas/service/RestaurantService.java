package com.mario.comidas.service;

import com.mario.comidas.dto.RestaurantDto;
import com.mario.comidas.model.Restaurant;
import com.mario.comidas.model.User;
import com.mario.comidas.request.CreateRestaurantRequest;

import java.util.List;

public interface RestaurantService {

    public Restaurant createRestaurant(CreateRestaurantRequest req, User user);

    public Restaurant updateRestaurant(Long restaurantId, CreateRestaurantRequest updatedRestaurant) throws Exception;

    public void deleteRestaurant(Long restaurantId) throws Exception;

    //Solo para los admins
    public List<Restaurant> getAllRestaurant();

    public List<Restaurant> searchRestaurant(String keyword);

    public Restaurant findRestaurantById(Long id) throws Exception;

    public Restaurant getRestaurantByUserId(Long userId) throws Exception;

    public RestaurantDto addToFavorites(Long restaurantId, User user)throws Exception;

    public Restaurant updateRestaurantStatus(Long id)throws Exception;


}
