package com.dxy.library.util.coordinate;


import com.dxy.library.json.gson.GsonUtil;
import com.dxy.library.network.http.Http;
import com.dxy.library.network.http.param.Params;
import com.dxy.library.util.common.ListUtils;

import java.math.BigDecimal;

/**
 * 地理坐标工具类
 * Dye-Duanxinyuan
 * 2017/1/20 12:15
 */
public interface CoordinateUtil {
    String ak = "7FpoT6craxXL10HUYOky3PPKl7POc5jH";
    String sk = "eKyAkm7PLhtnbL4U1raQPQG7ZIBiSW3C";

    /**
     * 将高德的经纬度转换成百度经纬度
     * @param lat 纬度
     * @param lng 经度
     */
    static Coordinate convertGaodeToBaidu(Double lat, Double lng) {
        if (lat == null || lng == null) {
            throw new IllegalArgumentException("lat or lng is null");
        } else {
            //开发者密钥
            Params params = new Params("ak", ak);
            //源坐标,格式：经度,纬度;经度,纬度…限制：最多支持100个,格式举例：114.21892734521,29.575429778924;114.21892734521,29.575429778924
            params.add("coords", lng + "," + lat);
            //源坐标类型，取值为如下：1：GPS设备获取的角度坐标，wgs84坐标;2：GPS获取的米制坐标、sogou地图所用坐标;
            // 3：google地图、soso地图、aliyun地图、mapabc地图和amap地图所用坐标，国测局坐标;
            //4：3中列表地图坐标对应的米制坐标;5：百度地图采用的经纬度坐标;6：百度地图采用的米制坐标;7：mapbar地图坐标;8：51地图坐标
            params.add("from", 3);
            //目的坐标类型，有两种可供选择：5、6。5：bd09ll(百度经纬度坐标),6：bd09mc(百度米制经纬度坐标);
            params.add("to", 5);
            //返回结果格式，json或者xml
            params.add("output", "json");
            String s = Http.disableLog().get("http://api.map.baidu.com/geoconv/v1/", params);
            CoordinateConvert coordinateConvert = GsonUtil.from(s, CoordinateConvert.class);
            if (null == coordinateConvert || ListUtils.isEmpty(coordinateConvert.result)) {
                //重试一次
                coordinateConvert = GsonUtil.from(s, CoordinateConvert.class);
            }
            if (null == coordinateConvert || ListUtils.isEmpty(coordinateConvert.result)) {
                return null;
            }
            return new Coordinate(coordinateConvert.result.get(0).y, coordinateConvert.result.get(0).x);
        }
    }

    /**
     * 计算两点之间距离（单位为米，保留2位小数）
     * @param lat1 - start纬度
     * @param lon1 - start经度
     * @param lat2 - end纬度
     * @param lon2 - end经度
     * @return 米(四舍五入)
     */
    static double distance(double lat1, double lon1, double lat2, double lon2) {
        double km = distanceKm(lat1, lon1, lat2, lon2);
        return new BigDecimal(String.valueOf(km * 1000)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 计算两点之间距离（单位为千米，保留5位小数）
     * @param lat1 - start纬度
     * @param lon1 - start经度
     * @param lat2 - end纬度
     * @param lon2 - end经度
     * @return 千米(四舍五入)
     */
    static double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        lat1 = (Math.PI / 180) * lat1;
        lat2 = (Math.PI / 180) * lat2;

        lon1 = (Math.PI / 180) * lon1;
        lon2 = (Math.PI / 180) * lon2;

        //地球赤道半径，单位为千米
        double radius = 6378.137;
        //计算距离
        double distance = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * radius;

        return new BigDecimal(String.valueOf(distance)).setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}
