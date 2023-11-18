package com.project_ci01.app.base.bean.gson;

import com.google.gson.annotations.SerializedName;

public class LocationBean {

    @SerializedName(value = "query", alternate = {"geoplugin_request", "ip", })
    private String host;

    @SerializedName(value = "country", alternate = {"geoplugin_countryName", "country_name", })
    private String country;

    @SerializedName(value = "countryCode", alternate = {"geoplugin_countryCode", "country_code", })
    private String abbr; // 国家代码

    @SerializedName(value = "regionName", alternate = {"geoplugin_regionName", "region", })
    private String province; // 省份

    @SerializedName(value = "lat", alternate = {"geoplugin_latitude", "latitude", })
    private float lat; // 维度

    @SerializedName(value = "lon", alternate = {"geoplugin_longitude", "longitude", })
    private float lon; // 经度

    public String getHost() {
        return host;
    }

    public String getCountry() {
        return country;
    }

    public String getAbbr() {
        return abbr;
    }

    public String getProvince() {
        return province;
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }

    @Override
    public String toString() {
        return "LocationBean{" +
                "host='" + host + '\'' +
                ", country='" + country + '\'' +
                ", abbr='" + abbr + '\'' +
                ", province='" + province + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
