package com.example.ingredientsmapv1;

import android.util.Log;

public class Recipe {
    public Integer ing1;
    public Integer ing2;

    public Recipe(Integer ing1, Integer ing2) {
        this.ing1 = ing1;
        this.ing2 = ing2;
        Log.d("RECIPE", "Recipe() returned: " + ing1 + '+' + ing2);
    }

//    public Recipe() {
//        Log.d("RECIPE", "Recipe() returned: " + "no argument");
//    }

    public Integer getIng1() {
        return ing1;
    }
}
