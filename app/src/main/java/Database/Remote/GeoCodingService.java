package Database.Remote;

import java.util.List;

import Domain.Models.GeoCodingResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GeoCodingService {
    @GET("search")
    Call<List<GeoCodingResponse>> getCoordinates(
            @Query("q") String cityName,
            @Query("format") String format,
            @Query("limit") int limit
    );
}
