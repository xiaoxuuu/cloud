package cc.xiaoxu.cloud.my.utils;

public class DistanceUtils {

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // WGS-84 椭球体参数
        final double a = 6378137.0;            // 长半轴（赤道半径），单位：米
        final double f = 1 / 298.257223563;    // 扁率
        final double b = (1 - f) * a;          // 短半轴（极半径）

        // 将角度转换为弧度
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double lambda1 = Math.toRadians(lon1);
        double lambda2 = Math.toRadians(lon2);

        // 归化纬度 (Reduced latitude)
        double U1 = Math.atan((1 - f) * Math.tan(phi1));
        double U2 = Math.atan((1 - f) * Math.tan(phi2));
        double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
        double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);

        // 经度差
        double L = lambda2 - lambda1;
        double lambda = L;
        double lambdaP;
        double iterLimit = 100; // 迭代次数限制

        double sinLambda, cosLambda, sinSigma, cosSigma, sigma, sinAlpha, cosSqAlpha, cos2SigmaM;

        // 迭代计算
        do {
            sinLambda = Math.sin(lambda);
            cosLambda = Math.cos(lambda);
            sinSigma = Math.sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda)
                    + (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda) * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));

            if (sinSigma == 0) return 0; // 两点重合

            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
            sigma = Math.atan2(sinSigma, cosSigma);
            sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
            cosSqAlpha = 1 - sinAlpha * sinAlpha;

            // 避免赤道上的除零错误
            if (cosSqAlpha == 0) {
                cos2SigmaM = 0;
            } else {
                cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
            }

            double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
            lambdaP = lambda;
            lambda = L + (1 - C) * f * sinAlpha *
                    (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));

        } while (Math.abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0);

        if (iterLimit == 0) return 0; // 迭代未收敛（极少数情况，如对趾点）

        double uSq = cosSqAlpha * (a * a - b * b) / (b * b);
        double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
        double deltaSigma = B * sinSigma * (cos2SigmaM + B / 4 * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)
                - B / 6 * cos2SigmaM * (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM * cos2SigmaM)));

        return b * A * (sigma - deltaSigma); // 返回距离（米）
    }

    public static double calculateDistanceSimple(double lat1, double lon1, double lat2, double lon2) {
        // 地球平均半径，单位：米 (WGS84标准)
        final double EARTH_RADIUS = 6371009.0;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c; // 返回米
    }
}
