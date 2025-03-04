package com.example.weebther.UI.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.example.weebther.Database.Local.Entity.City;
import com.example.weebther.R;
import com.example.weebther.UI.ViewModels.OnCityClickListener;
import com.example.weebther.UI.ViewModels.WeatherViewModel;
import com.example.weebther.Database.Remote.RemoteModels.WeatherResponse;
import com.example.weebther.Utils.CityDiffCallback;
import com.example.weebther.Utils.WeatherFormatter;

/**
 * Adapter for displaying a list of cities with weather data.
 */
public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> {
    private final WeatherViewModel weatherViewModel;
    private final List<City> cityList;
    private final OnCityClickListener onCityClickListener;
    private final OnCityClickListener onRefreshClickListener;
    private final LifecycleOwner lifecycleOwner;

    private String originalIconName;

    public CityAdapter(WeatherViewModel weatherViewModel, List<City> cityList, OnCityClickListener onCityClickListener, OnCityClickListener onRefreshClickListener, LifecycleOwner lifecycleOwner) {
        this.weatherViewModel = weatherViewModel;
        this.cityList = cityList;
        this.onCityClickListener = onCityClickListener;
        this.onRefreshClickListener = onRefreshClickListener;
        this.lifecycleOwner = lifecycleOwner;
    }

    private void showCityOptionsDialog(@NonNull CityViewHolder holder, City city) {
        // TODO: Link this with an XML (if it possible)
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
        builder.setTitle("Options for " + city.getName());

        CharSequence[] options = {"Add to favourites", "Refresh Weather", "Delete City"};
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    weatherViewModel.toggleCityFavorite(city.getName(), true);
                    Toast.makeText(holder.itemView.getContext(), city.getName() + " added to favorites!", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    onRefreshClickListener.onCityClick(city);
                    break;
                case 2:
                    showDeleteConfirmationDialog(holder, city);
            }
        });
        builder.show();
    }

    private void showDeleteConfirmationDialog(@NonNull CityViewHolder holder, City city) {
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
        builder.setTitle("Delete " + city.getName() + "?");
        builder.setMessage("This will remove all weather data for this city");
        builder.setPositiveButton("Delete", (dialog, which) -> {
            deleteCity(city);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
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
        String fullName = city.getName() + ", " + city.getCountry();
        holder.cityNameTextView.setText(fullName);

        weatherViewModel.getWeatherLiveData(city.getName()).observe(lifecycleOwner, weatherResponse -> {
            if (weatherResponse != null) {
                Log.d("CityAdapter", "Updating weather for " + city.getName());
                // Temperature, weather description and the corresponding icon
                holder.temperatureTextView.setText(WeatherFormatter.formatUnit(
                        holder.itemView.getContext(), weatherResponse.getTemperature(), WeatherFormatter.UnitType.TEMPERATURE));
                holder.descriptionTextView.setText(weatherResponse.getWeatherDescription());


                int iconRes = getWeatherIconResource(holder.itemView.getContext(), weatherResponse.getWeatherIcon(), weatherResponse.getWeatherDescription());
                holder.weatherIconImageView.setImageResource(iconRes);
            } else {
                holder.temperatureTextView.setText("--°C");
                holder.descriptionTextView.setText("No data");
            }
        });

        // Refresh
        //holder.refreshButton.setOnClickListener(v -> onRefreshClickListener.onCityClick(city));
        // Navigate to weather details
        holder.itemView.setOnClickListener(v -> onCityClickListener.onCityClick(city));

        // Hold button to show options
        holder.itemView.setOnLongClickListener(v -> {
            showCityOptionsDialog(holder, city);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    public void updateCitiesData(List<City> newCities) {
        // It will check the difference between both lists and only update the items that changed
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new CityDiffCallback(cityList, newCities));
        cityList.clear();
        cityList.addAll(newCities);
        diffResult.dispatchUpdatesTo(this);
    }

    public void deleteCity(City city) {
        weatherViewModel.deleteCity(city.getName()); // Removes it from the DB
        removeCity(city); // Removes it from the UI
    }

    public void removeCity(City city) {
        int position = cityList.indexOf(city);
        if (position != -1) {
            cityList.remove(position);
            notifyItemRemoved(position);
        }
    }

    private int getWeatherIconResource(Context context, String iconName, String weatherDescription) {
        // Removes the last character (d or n) to build the icon name (01d | 01n -> _01)
        String resourceName = "_" + iconName.substring(0, 2) + "d";

        boolean isThunderStorm = iconName.equals("11d") || iconName.equals("11n");
        boolean isRain = weatherDescription.toLowerCase().contains("drizzle") || weatherDescription.toLowerCase().contains("rain");
        if (isThunderStorm && isRain) resourceName = "_storm_rain";

        // Obtains the resource ID to set the ImageView in the Binder
        int resourceID = context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
        return resourceID;
    }


    public static class CityViewHolder extends RecyclerView.ViewHolder {
        TextView cityNameTextView;
        TextView temperatureTextView;
        TextView descriptionTextView;
        ImageView weatherIconImageView;
        ImageButton refreshButton;

        public CityViewHolder(View itemView) {
            super(itemView);
            cityNameTextView = itemView.findViewById(R.id.cityNameTextView);
            temperatureTextView = itemView.findViewById(R.id.temperatureTextView);
            descriptionTextView = itemView.findViewById(R.id.weatherDescriptionTextView);
            weatherIconImageView = itemView.findViewById(R.id.weatherIconImageView);
            //refreshButton = itemView.findViewById(R.id.refreshWeatherButton); Not freaking working for some reason
        }
    }
}
