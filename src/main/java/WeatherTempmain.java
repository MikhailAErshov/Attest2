import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherTempmain {

    private CoordResponse coord;
    private ArrayList<WeatherResponse> weather;
    private SysResponse sys;
    private Integer id;
    private String name;

    public CoordResponse getCoord() {
        return coord;
    }

    public ArrayList<WeatherResponse> getWeather() {
        return weather;
    }

    public SysResponse getSys() {
        return sys;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CoordResponse {
        private String lon;
        private String lat;

        public String getLon() {
            return lon;
        }

        public String getLat() {
            return lat;
        }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeatherResponse {
        private Integer id;
        private String name;
        private String description;
        private String icon;

        public Integer getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getIcon() {
            return icon;
        }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SysResponse {
        private Integer type;
        private Integer id;
        private String country;
        private Integer sunrise;
        private Integer sunset;

        public Integer getType() {
            return type;
        }

        public Integer getId() {
            return id;
        }

        public String getCountry() {
            return country;
        }

        public Integer getSunrise() {
            return sunrise;
        }

        public Integer getSunset() {
            return sunset;
        }
    }
}
