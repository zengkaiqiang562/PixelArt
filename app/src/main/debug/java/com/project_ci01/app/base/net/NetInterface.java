package com.project_ci01.app.base.net;


import com.project_ci01.app.base.bean.gson.LocationBean;
import com.project_ci01.app.base.config.AppConfig;

import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NetInterface {

    /*
    {
      "status": "success",
      "country": "China",
      "countryCode": "CN",
      "region": "HN",
      "regionName": "Hunan",
      "city": "Changsha",
      "zip": "",
      "lat": 28.2014,
      "lon": 112.9611,
      "timezone": "Asia/Shanghai",
      "isp": "Chinanet",
      "org": "Chinanet HN",
      "as": "AS4134 CHINANET-BACKBONE",
      "query": "175.11.203.83"
    }
     */
    @POST(AppConfig.PATH_LOCATION_1)
    @Headers("safe: false")
    Call<LocationBean> request1stLocal();

    /*
    {
      "geoplugin_request":"175.11.203.83",
      "geoplugin_status":200,
      "geoplugin_delay":"1ms",
      "geoplugin_credit":"Some of the returned data includes GeoLite data created by MaxMind, available from <a href='http:\/\/www.maxmind.com'>http:\/\/www.maxmind.com<\/a>.",
      "geoplugin_city":"Changsha",
      "geoplugin_region":"Hunan",
      "geoplugin_regionCode":"HN",
      "geoplugin_regionName":"Hunan",
      "geoplugin_areaCode":"",
      "geoplugin_dmaCode":"",
      "geoplugin_countryCode":"CN",
      "geoplugin_countryName":"China",
      "geoplugin_inEU":0,
      "geoplugin_euVATrate":false,
      "geoplugin_continentCode":"AS",
      "geoplugin_continentName":"Asia",
      "geoplugin_latitude":"28.1821",
      "geoplugin_longitude":"113.1055",
      "geoplugin_locationAccuracyRadius":"10",
      "geoplugin_timezone":"Asia\/Shanghai",
      "geoplugin_currencyCode":"CNY",
      "geoplugin_currencySymbol":"元",
      "geoplugin_currencySymbol_UTF8":"元",
      "geoplugin_currencyConverter":7.1885
    }
     */
    @POST(AppConfig.PATH_LOCATION_2)
    @Headers("safe: false")
    Call<LocationBean> request2ndLocal();

    /*
    {
        "ip": "175.11.203.83",
        "network": "175.11.128.0/17",
        "version": "IPv4",
        "city": "Changsha",
        "region": "Hunan",
        "region_code": "HN",
        "country": "CN",
        "country_name": "China",
        "country_code": "CN",
        "country_code_iso3": "CHN",
        "country_capital": "Beijing",
        "country_tld": ".cn",
        "continent_code": "AS",
        "in_eu": false,
        "postal": null,
        "latitude": 28.2014,
        "longitude": 112.9611,
        "timezone": "Asia/Shanghai",
        "utc_offset": "+0800",
        "country_calling_code": "+86",
        "currency": "CNY",
        "currency_name": "Yuan Renminbi",
        "languages": "zh-CN,yue,wuu,dta,ug,za",
        "country_area": 9596960.0,
        "country_population": 1392730000,
        "asn": "AS4134",
        "org": "Chinanet"
    }
     */
    @POST(AppConfig.PATH_LOCATION_3)
    @Headers("safe: false")
    Call<LocationBean> request3rdLocal();

    /*
    {
      "ip": "175.11.203.83",
      "success": true,
      "type": "IPv4",
      "continent": "Asia",
      "continent_code": "AS",
      "country": "China",
      "country_code": "CN",
      "region": "Hebei",
      "region_code": "13",
      "city": "Chengde",
      "latitude": 42.050934,
      "longitude": 118.022328,
      "is_eu": false,
      "postal": "067000",
      "calling_code": "86",
      "capital": "Beijing",
      "borders": "AF,BT,HK,IN,KG,KP,KZ,LA,MM,MN,MO,NP,PK,RU,TJ,VN",
      "flag": {
        "img": "https://cdn.ipwhois.io/flags/cn.svg",
        "emoji": "🇨🇳",
        "emoji_unicode": "U+1F1E8 U+1F1F3"
      },
      "connection": {
        "asn": 4134,
        "org": "CHINANET Hunan province network",
        "isp": "CHINANET-BACKBONE",
        "domain": "chinatelecom.cn"
      },
      "timezone": {
        "id": "Asia/Shanghai",
        "abbr": "CST",
        "is_dst": false,
        "offset": 28800,
        "utc": "+08:00",
        "current_time": "2022-10-11T18:19:31+08:00"
      }
    }
     */
    @POST(AppConfig.PATH_LOCATION_4)
    @Headers("security: false")
    Call<LocationBean> request4thLocal();
}
