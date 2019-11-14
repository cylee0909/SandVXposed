package com.lody.virtual.client.hook.proxies.location;

import com.lody.virtual.client.env.VirtualGPSSatalines;
import com.lody.virtual.client.ipc.VirtualLocationManager;
import com.lody.virtual.helper.utils.Reflect;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import mirror.android.location.LocationManager;

/**
 * @author Lody
 */
public class MockLocationHelper {

    public static void invokeNmeaReceived(Object listener) {
        if (listener != null) {
            VirtualGPSSatalines satalines = VirtualGPSSatalines.get();
            try {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void setGpsStatus(Object locationManager) {

        VirtualGPSSatalines satalines = VirtualGPSSatalines.get();
        Method setStatus = null;
        int svCount = satalines.getSvCount();
        float[] snrs = satalines.getSnrs();
        int[] prns = satalines.getPrns();
        float[] elevations = satalines.getElevations();
        float[] azimuths = satalines.getAzimuths();
        Object mGpsStatus = Reflect.on(locationManager).get("mGpsStatus");
        try {
            setStatus = mGpsStatus.getClass().getDeclaredMethod("setStatus", Integer.TYPE, int[].class, float[].class, float[].class, float[].class, Integer.TYPE, Integer.TYPE, Integer.TYPE);
            setStatus.setAccessible(true);
            int ephemerisMask = satalines.getEphemerisMask();
            int almanacMask = satalines.getAlmanacMask();
            int usedInFixMask = satalines.getUsedInFixMask();
            setStatus.invoke(mGpsStatus, svCount, prns, snrs, elevations, azimuths, ephemerisMask, almanacMask, usedInFixMask);
        } catch (Exception e) {
            // ignore
        }
        if (setStatus == null) {
            try {
                setStatus = mGpsStatus.getClass().getDeclaredMethod("setStatus", Integer.TYPE, int[].class, float[].class, float[].class, float[].class, int[].class, int[].class, int[].class);
                setStatus.setAccessible(true);
                svCount = satalines.getSvCount();
                int length = satalines.getPrns().length;
                elevations = satalines.getElevations();
                azimuths = satalines.getAzimuths();
                int[] ephemerisMask = new int[length];
                for (int i = 0; i < length; i++) {
                    ephemerisMask[i] = satalines.getEphemerisMask();
                }
                int[] almanacMask = new int[length];
                for (int i = 0; i < length; i++) {
                    almanacMask[i] = satalines.getAlmanacMask();
                }
                int[] usedInFixMask = new int[length];
                for (int i = 0; i < length; i++) {
                    usedInFixMask[i] = satalines.getUsedInFixMask();
                }
                setStatus.invoke(mGpsStatus, svCount, prns, snrs, elevations, azimuths, ephemerisMask, almanacMask, usedInFixMask);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void invokeSvStatusChanged(Object transport) {
        if (transport != null) {
            VirtualGPSSatalines satalines = VirtualGPSSatalines.get();
            try {
                Class<?> aClass = transport.getClass();
                int svCount;
                float[] snrs;
                float[] elevations;
                float[] azimuths;
                float[] carrierFreqs = new float[0];
                if (aClass == LocationManager.GnssStatusListenerTransport.TYPE) {
                    svCount = satalines.getSvCount();
                    int[] prnWithFlags = satalines.getPrnWithFlags();
                    snrs = satalines.getSnrs();
                    elevations = satalines.getElevations();
                    azimuths = satalines.getAzimuths();
                    LocationManager.GnssStatusListenerTransport.onSvStatusChanged.call(transport, svCount, prnWithFlags, snrs, elevations, azimuths, carrierFreqs);
                } else if (aClass == LocationManager.GpsStatusListenerTransport.TYPE) {
                    svCount = satalines.getSvCount();
                    int[] prns = satalines.getPrns();
                    snrs = satalines.getSnrs();
                    elevations = satalines.getElevations();
                    azimuths = satalines.getAzimuths();
                    int ephemerisMask = satalines.getEphemerisMask();
                    int almanacMask = satalines.getAlmanacMask();
                    int usedInFixMask = satalines.getUsedInFixMask();
                    if (LocationManager.GpsStatusListenerTransport.onSvStatusChanged != null) {
                        LocationManager.GpsStatusListenerTransport.onSvStatusChanged.call(transport, svCount, prns, snrs, elevations, azimuths, ephemerisMask, almanacMask, usedInFixMask);
                    } else if (LocationManager.GpsStatusListenerTransportVIVO.onSvStatusChanged != null) {
                        LocationManager.GpsStatusListenerTransportVIVO.onSvStatusChanged.call(transport, svCount, prns, snrs, elevations, azimuths, ephemerisMask, almanacMask, usedInFixMask, new long[svCount]);
                    } else if (LocationManager.GpsStatusListenerTransportSumsungS5.onSvStatusChanged != null) {
                        LocationManager.GpsStatusListenerTransportSumsungS5.onSvStatusChanged.call(transport, svCount, prns, snrs, elevations, azimuths, ephemerisMask, almanacMask, usedInFixMask, new int[svCount]);
                    } else if (LocationManager.GpsStatusListenerTransportOPPO_R815T.onSvStatusChanged != null) {
                        int len = prns.length;
                        int[] ephemerisMasks = new int[len];
                        for (int i = 0; i < len; i++) {
                            ephemerisMasks[i] = satalines.getEphemerisMask();
                        }
                        int[] almanacMasks = new int[len];
                        for (int i = 0; i < len; i++) {
                            almanacMasks[i] = satalines.getAlmanacMask();
                        }
                        int[] usedInFixMasks = new int[len];
                        for (int i = 0; i < len; i++) {
                            usedInFixMasks[i] = satalines.getUsedInFixMask();
                        }
                        LocationManager.GpsStatusListenerTransportOPPO_R815T.onSvStatusChanged.call(transport, svCount, prns, snrs, elevations, azimuths, ephemerisMasks, almanacMasks, usedInFixMasks, svCount);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getGPSLat(double v) {
        int du = (int) v;
        double fen = (v - (double) du) * 60.0d;
        return du + leftZeroPad((int) fen, 2) + ":" + String.valueOf(fen).substring(2);
    }

    private static String leftZeroPad(int num, int size) {
        return leftZeroPad(String.valueOf(num), size);
    }

    private static String leftZeroPad(String num, int size) {
        StringBuilder sb = new StringBuilder(size);
        int i;
        if (num == null) {
            for (i = 0; i < size; i++) {
                sb.append('0');
            }
        } else {
            for (i = 0; i < size - num.length(); i++) {
                sb.append('0');
            }
            sb.append(num);
        }
        return sb.toString();
    }

    public static String checksum(String nema) {
        String checkStr = nema;
        if (nema.startsWith("$")) {
            checkStr = nema.substring(1);
        }
        int sum = 0;
        for (int i = 0; i < checkStr.length(); i++) {
            sum ^= (byte) checkStr.charAt(i);
        }
        return nema + "*" + String.format("%02X", sum).toLowerCase();
    }
}