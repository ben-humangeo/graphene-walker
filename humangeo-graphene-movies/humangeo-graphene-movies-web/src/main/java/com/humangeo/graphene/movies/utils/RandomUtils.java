package com.humangeo.graphene.movies.utils;

/**
 * Created by bparrish on 12/24/14.
 */
public class RandomUtils {

    public static int getRandomNonZeroOddNumber(int min, int max) {
        if (max % 2 == 0) --max;

        if (min % 2 == 0) ++min;

        return min + 2*(int)(Math.random()*((max-min)/2+1));
    }

}
