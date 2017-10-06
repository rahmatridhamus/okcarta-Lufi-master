package com.buahbatu.okarta.helper;

import android.content.Context;
import android.renderscript.Double2;

import com.buahbatu.okarta.R;

/**
 * Helper to read Margin
 */

public class MarginHelper {
    private static final String KAMPAS = "Brake Shoe";
    private static final String FILTER = "Air Cleaner";
    private static final String AKI = "Accumulator";
    private static final String SERVICE = "Service";

    private static final double[] BREAK_SHOE_MARGIN = new double[]{1, 2};
    private static final double[] FILTER_MARGIN = new double[]{90};
    private static final double[] ACCU_MARGIN = new double[]{12};
    private static final double[] SERVICE_MARGIN = new double[]{1120, 5000, 10000, 15000, 20000, 25000};

    public static String getTranslated(String attribute) {
        switch (attribute) {
            case "kampas":
                return KAMPAS;
            case "filter":
                return FILTER;
            case "aki":
                return AKI;
            case "service":
                return SERVICE;
            default:
                return SERVICE;
        }
    }

    public static String getBreakShoeStatus(double read) {
        if (read < BREAK_SHOE_MARGIN[0])
            return "CHANGE";
        else if (read < BREAK_SHOE_MARGIN[1])
            return "BAD";
        else return "GOOD";
    }

    public static String getFilterStatus(double read) {
        return read + " pa";
    }

    public static String getAccuStatus(double read) {
        return read + " V";
    }

    public static String getServiceStatus(double read) {
        return read + " KM";
    }

    public static String getStatusMessage(Context context, String attribute, String status) {
        double val = 0;
        switch (attribute) {
            case FILTER:
                val = Double.parseDouble(status.split(" ")[0]);
                if (val < FILTER_MARGIN[0])
                    status = "BAD";
                else status = "GOOD";
                break;
            case AKI:
                val = Double.parseDouble(status.split(" ")[0]);
                if (val < ACCU_MARGIN[0])
                    status = "BAD";
                else status = "GOOD";
                break;
            case SERVICE:
                val = Double.parseDouble(status.split(" ")[0]);
                break;
            default:
                val = Double.parseDouble(status.split(" ")[0]);
                // bikin if if an
                break;
        }

        switch (status) {
            case "CHANGE":
                return String.format(context.getString(R.string.change_condition), attribute);
            case "BAD":
                return String.format(context.getString(R.string.bad_condition), attribute);
            case "GOOD":
                return String.format(context.getString(R.string.good_condition), attribute);
            default:
                return String.format(context.getString(R.string.good_condition), attribute);
        }
    }

    public static String getDetailStatus(Context context, String attribute, String status) {
        double val = 0;
        switch (attribute) {
            case FILTER:
                val = Double.parseDouble(status.split(" ")[0]);
                // bikin if if an
                // status = CHANGE or BAD or GOOD
                break;
            case AKI:
                val = Double.parseDouble(status.split(" ")[0]);
                // bikin if if an
                break;
            case SERVICE:
                val = Double.parseDouble(status.split(" ")[0]);
                if (val == SERVICE_MARGIN[0])
                    status = "1120";
                else if (val == SERVICE_MARGIN[1])
                    status = "5000";
                else if (val == SERVICE_MARGIN[2])
                    status = "10000";
                else if (val == SERVICE_MARGIN[3])
                    status = "15000";
                else if (val == SERVICE_MARGIN[4])
                    status = "20000";
                else status = "25000";
                break;
            default:
                val = Double.parseDouble(status.split(" ")[0]);
                // bikin if if an
                break;
        }

        switch (status) {
            case "1120":
                return "Silahkan Lakukan Perawatan Mobil Untuk: \n" +
                        "1. ndied \n" +
                        "2. ndoej \n" +
                        "3. nierfj \n";
            case "5000":
                return "Silahkan Lakukan Perawatan Mobil Untuk: \n" +
                        "1. frkgn \n" +
                        "2. fmorgmor \n" +
                        "3. jogt \n";
            case "10000":
                return "Silahkan Lakukan Perawatan Mobil Untuk: \n" +
                        "1. fnrig \n" +
                        "2. nirgjr \n" +
                        "3.nfrigj \n";
            case "15000":
                return "Silahkan Lakukan Perawatan Mobil Untuk: \n" +
                        "1. fbrug \n" +
                        "2. fnirgh \n" +
                        "3. nfirjg \n";
            case "20000":
                return "Silahkan Lakukan Perawatan Mobil Untuk: \n" +
                        "1. dneifj \n" +
                        "2. nifejf \n" +
                        "3. jforf \n";
            case "25000":
                return "Silahkan Lakukan Perawatan Mobil Untuk: \n" +
                        "1. gnirgn \n" +
                        "2. nirg \n" +
                        "3. mogtjg \n";
            default:
                return String.format(context.getString(R.string.good_condition), attribute);
        }
    }
}