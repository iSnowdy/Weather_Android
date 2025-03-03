package com.example.weebther.Utils;

import androidx.recyclerview.widget.DiffUtil;
import com.example.weebther.Database.Local.Entity.City;
import java.util.List;

public class CityDiffCallback extends DiffUtil.Callback {
    private final List<City> oldList;
    private final List<City> newList;

    public CityDiffCallback(List<City> oldList, List<City> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getName().equals(newList.get(newItemPosition).getName());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}
