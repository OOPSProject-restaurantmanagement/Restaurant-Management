package com.example.oops;

import java.util.HashMap;
import java.util.Map;

public class DishUtils {
    private static final Map<String, Integer> dishImageMap = new HashMap<>();

    static {
        dishImageMap.put("IDLY", R.drawable.idly);
        dishImageMap.put("VADA", R.drawable.vada);
        dishImageMap.put("DOSA", R.drawable.dosa);
        dishImageMap.put("CHOLE BHATURE", R.drawable.cholebature);
        dishImageMap.put("POHA", R.drawable.poha);
        dishImageMap.put("CHOWMEIN", R.drawable.chowmein);
        dishImageMap.put("COFFEE", R.drawable.coffee);
        dishImageMap.put("TEA", R.drawable.tea);
        dishImageMap.put("BIRYANI", R.drawable.biryani);
        dishImageMap.put("TOMATO CURRY", R.drawable.tomatocurry);
    }

    public static int getImageResId(String dishName) {
        Integer imageResId = dishImageMap.get(dishName);
        return imageResId != null ? imageResId : 0; // Return 0 if dish name not found
    }
}