package com.yacovets.projectmanagement.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CollectionUtil {
    public <E> List<List<E>> splitCollection(List<E> items, int countList) {
        List<List<E>> result = new ArrayList<>();

        for (int i = 0; i < countList; i++) {
            result.add(new ArrayList<>());
        }

        for (int i = 0; i < items.size(); i++) {
            int indexList = i % 4;
            result.get(indexList).add(items.get(i));
        }

        return result;
    }
}
