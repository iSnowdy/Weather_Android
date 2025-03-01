package com.example.weebther.UI.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.example.weebther.Database.Local.Entity.City;
import com.example.weebther.R;
import com.example.weebther.UI.ViewModels.OnCityClickListener;

/*
CityViewHolder optimizes the RecyclerView we will be using. It saves the references of the views
inside each CardView.
*/


/**
 * Adapter for displaying a list of cities with weather data.
 */

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> {
    private final List<City> cityList;
    private final OnCityClickListener onCityClickListener;
    private final OnCityClickListener onRefreshClickListener;

    public CityAdapter(List<City> cityList, OnCityClickListener onCityClickListener, OnCityClickListener onRefreshClickListener) {
        this.cityList = cityList;
        this.onCityClickListener = onCityClickListener;
        this.onRefreshClickListener = onRefreshClickListener;
    }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city, parent, false);
        return new CityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityViewHolder holder, int position) {
        City city = cityList.get(position);
        holder.cityNameTextView.setText(city.getName());

        holder.itemView.setOnClickListener(v -> onCityClickListener.onCityClick(city));
        holder.refreshButton.setOnClickListener(v -> onRefreshClickListener.onCityClick(city));
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    public void updateCitiesData(List<City> cities) {
        cityList.clear();
        cityList.addAll(cities);
        notifyDataSetChanged();
    }

    /**
     * ViewHolder for city items.
     */
    public static class CityViewHolder extends RecyclerView.ViewHolder {
        TextView cityNameTextView;
        ImageButton refreshButton;

        public CityViewHolder(View itemView) {
            super(itemView);
            cityNameTextView = itemView.findViewById(R.id.cityNameTextView);
            refreshButton = itemView.findViewById(R.id.refreshWeatherButton);
        }
    }
}
