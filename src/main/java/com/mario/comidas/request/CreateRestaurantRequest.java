package com.mario.comidas.request;

import com.mario.comidas.model.Address;
import com.mario.comidas.model.ContactInformation;
import lombok.Data;

import java.util.List;

//La anotacion nos permite tener todos los metodos get y ser
@Data
public class CreateRestaurantRequest {

    private Long id;
    private String name;
    private String description;
    private String cuisineType;
    private Address address;
    private ContactInformation contactInformation;
    private String openingHours;
    private List<String> images;
}
