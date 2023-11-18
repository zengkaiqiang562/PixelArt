package com.project_ci01.app.base.manage;

import android.content.Context;
import android.content.res.Resources;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.Utils;
import com.project_ci01.app.base.bean.gson.LocationBean;
import com.project_ci01.app.base.net.NetExecutor;
import com.project_ci01.app.base.utils.LogUtils;

import java.util.HashMap;
import java.util.Map;

public enum LocationManager {

    INSTANCE;

    private static final String TAG = "LocationManager";

    private LocationBean locationBean;

//    public static String getIconUri(String countryCode, @DrawableRes int defRes, Context context) {
//        if (TextUtils.isEmpty(countryCode)) {
//            return getDefIconUri(context, defRes);
//        }
//        return "file:///android_asset/flag_folder/" + countryCode.toUpperCase() + ".png";
//    }
//
//    public static String getDefIconUri(Context context, @DrawableRes int defRes) {
//        return "android.resource://" + context.getPackageName()+ "/" + defRes;
//    }

    public String getHost() {
        if (locationBean != null && !TextUtils.isEmpty(locationBean.getHost())) {
            return locationBean.getHost();
        }
        return NetworkUtils.getIPAddress(true);
    }

    public String getCountry() {
        if (locationBean != null && !TextUtils.isEmpty(locationBean.getCountry())) {
            return locationBean.getCountry();
        }

        return getCountry(getCountryCode());
    }

    public String getCountryCode() {
        if (locationBean != null && !TextUtils.isEmpty(locationBean.getAbbr())) {
            return locationBean.getAbbr();
        }
        String code = getSimCode();
        return TextUtils.isEmpty(code) ? getLangCode() : code;
    }

    public String getSimCode() {
        TelephonyManager manager = (TelephonyManager) Utils.getApp().getSystemService(Context.TELEPHONY_SERVICE);
        if (manager != null) {
            return manager.getSimCountryIso().toUpperCase();
        }
        return "";
    }

    // 获取公共移动陆地网
    public String getPulicNet() {
        TelephonyManager telephonyManager = (TelephonyManager) Utils.getApp().getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getNetworkOperator();
    }

    public void initLocation() {
        NetExecutor.INSTANCE.requestLocalInfo(new RequestCallback() {
            @Override
            public void onSuccess(LocationBean locationBean) {
                LogUtils.e(TAG, "--> initLocation() onSuccess  locationBean=" + locationBean);
                LocationManager.this.locationBean = locationBean;
            }

            @Override
            public void onFailure(@Nullable Throwable t) {
                LogUtils.e(TAG, "--> initLocation() onFailure: " + t);
            }
        });
    }

    /**
     * Return the country by system language.
     *
     * @return the country
     */
    private static String getLangCode() {
        return Resources.getSystem().getConfiguration().locale.getCountry();
    }

    private static String getCountry(String code) {
        if (TextUtils.isEmpty(code)) {
            return "null";
        }

        String countryName = countryNameMap.get(code);
        return TextUtils.isEmpty(countryName) ? "null" : countryName;
    }

    public interface RequestCallback {
        void onSuccess(LocationBean locationBean);
        void onFailure(@Nullable Throwable t);
    }

    private static final Map<String, String> countryCodeMap = new HashMap<String, String>() {{
        put("AL", "+355");
        put("DZ", "+213");
        put("AF", "+93");
        put("AR", "+54");
        put("AE", "+971");
        put("AW", "+297");
        put("OM", "+968");
        put("AZ", "+994");
        put("AC", "+247");
        put("EG", "+20");
        put("ET", "+251");
        put("IE", "+353");
        put("EE", "+372");
        put("AD", "+376");
        put("AO", "+244");
        put("AI", "+1");
        put("AG", "+1");
        put("AT", "+43");
        put("AX", "+358");
        put("AU", "+61");
        put("BB", "+1");
        put("PG", "+675");
        put("BS", "+1");
        put("PK", "+92");
        put("PY", "+595");
        put("PS", "+970");
        put("BH", "+973");
        put("PA", "+507");
        put("BR", "+55");
        put("BY", "+375");
        put("BM", "+1");
        put("BG", "+359");
        put("MP", "+1");
        put("BJ", "+229");
        put("BE", "+32");
        put("IS", "+354");
        put("PR", "+1");
        put("PL", "+48");
        put("BA", "+387");
        put("BO", "+591");
        put("BZ", "+501");
        put("BW", "+267");
        put("BT", "+975");
        put("BF", "+226");
        put("BI", "+257");
        put("KP", "+850");
        put("GQ", "+240");
        put("DK", "+45");
        put("DE", "+49");
        put("TL", "+670");
        put("TG", "+228");
        put("DO", "+1");
        put("DM", "+1");
        put("RU", "+7");
        put("EC", "+593");
        put("ER", "+291");
        put("FR", "+33");
        put("FO", "+298");
        put("PF", "+689");
        put("GF", "+594");
        put("VA", "+39");
        put("PH", "+63");
        put("FJ", "+679");
        put("FI", "+358");
        put("CV", "+238");
        put("FK", "+500");
        put("GM", "+220");
        put("CG", "+242");
        put("CD", "+243");
        put("CO", "+57");
        put("CR", "+506");
        put("GG", "+44");
        put("GD", "+1");
        put("GL", "+299");
        put("GE", "+995");
        put("CU", "+53");
        put("GP", "+590");
        put("GU", "+1");
        put("GY", "+592");
        put("KZ", "+7");
        put("HT", "+509");
        put("KR", "+82");
        put("NL", "+31");
        put("BQ", "+599");
        put("SX", "+1");
        put("ME", "+382");
        put("HN", "+504");
        put("KI", "+686");
        put("DJ", "+253");
        put("KG", "+996");
        put("GN", "+224");
        put("GW", "+245");
        put("CA", "+1");
        put("GH", "+233");
        put("GA", "+241");
        put("KH", "+855");
        put("CZ", "+420");
        put("ZW", "+263");
        put("CM", "+237");
        put("QA", "+974");
        put("KY", "+1");
        put("CC", "+61");
        put("KM", "+269");
        put("XK", "+383");
        put("CI", "+225");
        put("KW", "+965");
        put("HR", "+385");
        put("KE", "+254");
        put("CK", "+682");
        put("CW", "+599");
        put("LV", "+371");
        put("LS", "+266");
        put("LA", "+856");
        put("LB", "+961");
        put("LT", "+370");
        put("LR", "+231");
        put("LY", "+218");
        put("LI", "+423");
        put("RE", "+262");
        put("LU", "+352");
        put("RW", "+250");
        put("RO", "+40");
        put("MG", "+261");
        put("IM", "+44");
        put("MV", "+960");
        put("MT", "+356");
        put("MW", "+265");
        put("MY", "+60");
        put("ML", "+223");
        put("MK", "+389");
        put("MH", "+692");
        put("MQ", "+596");
        put("YT", "+262");
        put("MU", "+230");
        put("MR", "+222");
        put("US", "+1");
        put("AS", "+1");
        put("VI", "+1");
        put("MN", "+976");
        put("MS", "+1");
        put("BD", "+880");
        put("PE", "+51");
        put("FM", "+691");
        put("MM", "+95");
        put("MD", "+373");
        put("MA", "+212");
        put("MC", "+377");
        put("MZ", "+258");
        put("MX", "+52");
        put("NA", "+264");
        put("ZA", "+27");
        put("SS", "+211");
        put("NR", "+674");
        put("NI", "+505");
        put("NP", "+977");
        put("NE", "+227");
        put("NG", "+234");
        put("NU", "+683");
        put("NO", "+47");
        put("NF", "+672");
        put("PW", "+680");
        put("PT", "+351");
        put("JP", "+81");
        put("SE", "+46");
        put("CH", "+41");
        put("SV", "+503");
        put("WS", "+685");
        put("RS", "+381");
        put("SL", "+232");
        put("SN", "+221");
        put("CY", "+357");
        put("SC", "+248");
        put("SA", "+966");
        put("BL", "+590");
        put("CX", "+61");
        put("ST", "+239");
        put("SH", "+290");
        put("PN", "+870");
        put("KN", "+1");
        put("LC", "+1");
        put("MF", "+590");
        put("SM", "+378");
        put("PM", "+508");
        put("VC", "+1");
        put("LK", "+94");
        put("SK", "+421");
        put("SI", "+386");
        put("SJ", "+47");
        put("SZ", "+268");
        put("SD", "+249");
        put("SR", "+597");
        put("SB", "+677");
        put("SO", "+252");
        put("TJ", "+992");
        put("TH", "+66");
        put("TZ", "+255");
        put("TO", "+676");
        put("TC", "+1");
        put("TA", "+290");
        put("TT", "+1");
        put("TN", "+216");
        put("TV", "+688");
        put("TR", "+90");
        put("TM", "+993");
        put("TK", "+690");
        put("WF", "+681");
        put("VU", "+678");
        put("GT", "+502");
        put("VE", "+58");
        put("BN", "+673");
        put("UG", "+256");
        put("UA", "+380");
        put("UY", "+598");
        put("UZ", "+998");
        put("GR", "+30");
        put("ES", "+34");
        put("EH", "+212");
        put("SG", "+65");
        put("NC", "+687");
        put("NZ", "+64");
        put("HU", "+36");
        put("SY", "+963");
        put("JM", "+1");
        put("AM", "+374");
        put("YE", "+967");
        put("IQ", "+964");
        put("UM", "+1");
        put("IR", "+98");
        put("IL", "+972");
        put("IT", "+39");
        put("IN", "+91");
        put("ID", "+62");
        put("GB", "+44");
        put("VG", "+1");
        put("IO", "+246");
        put("JO", "+962");
        put("VN", "+84");
        put("ZM", "+260");
        put("JE", "+44");
        put("TD", "+235");
        put("GI", "+350");
        put("CL", "+56");
        put("CF", "+236");
        put("CN", "+86");
        put("MO", "+853");
        put("TW", "+886");
        put("HK", "+852");
    }};

    /*
    ISO 3166-1 table
    English short name (using title case) |	Alpha-2 code |	Alpha-3 code |	Numeric code |	Link to ISO 3166-2 subdivision codes | Independent
     */
    private static final Map<String, String> countryNameMap = new HashMap<String, String>() {{

        put("AF", "Afghanistan"); /*  AFG	004	ISO 3166-2:AF	Yes  */
        put("AX", "Åland Islands"); /*  ALA	248	ISO 3166-2:AX	No  */
        put("AL", "Albania"); /*  ALB	008	ISO 3166-2:AL	Yes  */
        put("DZ", "Algeria"); /*  DZA	012	ISO 3166-2:DZ	Yes  */
        put("AS", "American Samoa"); /*  ASM	016	ISO 3166-2:AS	No  */
        put("AD", "Andorra"); /*  AND	020	ISO 3166-2:AD	Yes  */
        put("AO", "Angola"); /*  AGO	024	ISO 3166-2:AO	Yes  */
        put("AI", "Anguilla"); /*  AIA	660	ISO 3166-2:AI	No  */
        put("AQ", "Antarctica"); /*  ATA	010	ISO 3166-2:AQ	No  */
        put("AG", "Antigua and Barbuda"); /*  ATG	028	ISO 3166-2:AG	Yes  */
        put("AR", "Argentina"); /*  ARG	032	ISO 3166-2:AR	Yes  */
        put("AM", "Armenia"); /*  ARM	051	ISO 3166-2:AM	Yes  */
        put("AW", "Aruba"); /*  ABW	533	ISO 3166-2:AW	No  */
        put("AU", "Australia"); /*  AUS	036	ISO 3166-2:AU	Yes  */
        put("AT", "Austria"); /*  AUT	040	ISO 3166-2:AT	Yes  */
        put("AZ", "Azerbaijan"); /*  AZE	031	ISO 3166-2:AZ	Yes  */
        put("BS", "Bahamas"); /*  BHS	044	ISO 3166-2:BS	Yes  */
        put("BH", "Bahrain"); /*  BHR	048	ISO 3166-2:BH	Yes  */
        put("BD", "Bangladesh"); /*  BGD	050	ISO 3166-2:BD	Yes  */
        put("BB", "Barbados"); /*  BRB	052	ISO 3166-2:BB	Yes  */
        put("BY", "Belarus"); /*  BLR	112	ISO 3166-2:BY	Yes  */
        put("BE", "Belgium"); /*  BEL	056	ISO 3166-2:BE	Yes  */
        put("BZ", "Belize"); /*  BLZ	084	ISO 3166-2:BZ	Yes  */
        put("BJ", "Benin"); /*  BEN	204	ISO 3166-2:BJ	Yes  */
        put("BM", "Bermuda"); /*  BMU	060	ISO 3166-2:BM	No  */
        put("BT", "Bhutan"); /*  BTN	064	ISO 3166-2:BT	Yes  */
        put("BO", "Bolivia (Plurinational State of)"); /*  BOL	068	ISO 3166-2:BO	Yes  */
        put("BQ", "Bonaire, Sint Eustatius and Saba"); /*  BES	535	ISO 3166-2:BQ	No  */
        put("BA", "Bosnia and Herzegovina"); /*  BIH	070	ISO 3166-2:BA	Yes  */
        put("BW", "Botswana"); /*  BWA	072	ISO 3166-2:BW	Yes  */
        put("BV", "Bouvet Island"); /*  BVT	074	ISO 3166-2:BV	No  */
        put("BR", "Brazil"); /*  BRA	076	ISO 3166-2:BR	Yes  */
        put("IO", "British Indian Ocean Territory"); /*  IOT	086	ISO 3166-2:IO	No  */
        put("BN", "Brunei Darussalam"); /*  BRN	096	ISO 3166-2:BN	Yes  */
        put("BG", "Bulgaria"); /*  BGR	100	ISO 3166-2:BG	Yes  */
        put("BF", "Burkina Faso"); /*  BFA	854	ISO 3166-2:BF	Yes  */
        put("BI", "Burundi"); /*  BDI	108	ISO 3166-2:BI	Yes  */
        put("CV", "Cabo Verde"); /*  CPV	132	ISO 3166-2:CV	Yes  */
        put("KH", "Cambodia"); /*  KHM	116	ISO 3166-2:KH	Yes  */
        put("CM", "Cameroon"); /*  CMR	120	ISO 3166-2:CM	Yes  */
        put("CA", "Canada"); /*  CAN	124	ISO 3166-2:CA	Yes  */
        put("KY", "Cayman Islands"); /*  CYM	136	ISO 3166-2:KY	No  */
        put("CF", "Central African Republic"); /*  CAF	140	ISO 3166-2:CF	Yes  */
        put("TD", "Chad"); /*  TCD	148	ISO 3166-2:TD	Yes  */
        put("CL", "Chile"); /*  CHL	152	ISO 3166-2:CL	Yes  */
        put("CN", "China"); /*  CHN	156	ISO 3166-2:CN	Yes  */
        put("CX", "Christmas Island"); /*  CXR	162	ISO 3166-2:CX	No  */
        put("CC", "Cocos (Keeling) Islands"); /*  CCK	166	ISO 3166-2:CC	No  */
        put("CO", "Colombia"); /*  COL	170	ISO 3166-2:CO	Yes  */
        put("KM", "Comoros"); /*  COM	174	ISO 3166-2:KM	Yes  */
        put("CG", "Congo"); /*  COG	178	ISO 3166-2:CG	Yes  */
        put("CD", "Congo, Democratic Republic of the"); /*  COD	180	ISO 3166-2:CD	Yes  */
        put("CK", "Cook Islands"); /*  COK	184	ISO 3166-2:CK	No  */
        put("CR", "Costa Rica"); /*  CRI	188	ISO 3166-2:CR	Yes  */
        put("CI", "Côte d'Ivoire"); /*  CIV	384	ISO 3166-2:CI	Yes  */
        put("HR", "Croatia"); /*  HRV	191	ISO 3166-2:HR	Yes  */
        put("CU", "Cuba"); /*  CUB	192	ISO 3166-2:CU	Yes  */
        put("CW", "Curaçao"); /*  CUW	531	ISO 3166-2:CW	No  */
        put("CY", "Cyprus"); /*  CYP	196	ISO 3166-2:CY	Yes  */
        put("CZ", "Czechia"); /*  CZE	203	ISO 3166-2:CZ	Yes  */
        put("DK", "Denmark"); /*  DNK	208	ISO 3166-2:DK	Yes  */
        put("DJ", "Djibouti"); /*  DJI	262	ISO 3166-2:DJ	Yes  */
        put("DM", "Dominica"); /*  DMA	212	ISO 3166-2:DM	Yes  */
        put("DO", "Dominican Republic"); /*  DOM	214	ISO 3166-2:DO	Yes  */
        put("EC", "Ecuador"); /*  ECU	218	ISO 3166-2:EC	Yes  */
        put("EG", "Egypt"); /*  EGY	818	ISO 3166-2:EG	Yes  */
        put("SV", "El Salvador"); /*  SLV	222	ISO 3166-2:SV	Yes  */
        put("GQ", "Equatorial Guinea"); /*  GNQ	226	ISO 3166-2:GQ	Yes  */
        put("ER", "Eritrea"); /*  ERI	232	ISO 3166-2:ER	Yes  */
        put("EE", "Estonia"); /*  EST	233	ISO 3166-2:EE	Yes  */
        put("SZ", "Eswatini"); /*  SWZ	748	ISO 3166-2:SZ	Yes  */
        put("ET", "Ethiopia"); /*  ETH	231	ISO 3166-2:ET	Yes  */
        put("FK", "Falkland Islands (Malvinas)"); /*  FLK	238	ISO 3166-2:FK	No  */
        put("FO", "Faroe Islands"); /*  FRO	234	ISO 3166-2:FO	No  */
        put("FJ", "Fiji"); /*  FJI	242	ISO 3166-2:FJ	Yes  */
        put("FI", "Finland"); /*  FIN	246	ISO 3166-2:FI	Yes  */
        put("FR", "France"); /*  FRA	250	ISO 3166-2:FR	Yes  */
        put("GF", "French Guiana"); /*  GUF	254	ISO 3166-2:GF	No  */
        put("PF", "French Polynesia"); /*  PYF	258	ISO 3166-2:PF	No  */
        put("TF", "French Southern Territories"); /*  ATF	260	ISO 3166-2:TF	No  */
        put("GA", "Gabon"); /*  GAB	266	ISO 3166-2:GA	Yes  */
        put("GM", "Gambia"); /*  GMB	270	ISO 3166-2:GM	Yes  */
        put("GE", "Georgia"); /*  GEO	268	ISO 3166-2:GE	Yes  */
        put("DE", "Germany"); /*  DEU	276	ISO 3166-2:DE	Yes  */
        put("GH", "Ghana"); /*  GHA	288	ISO 3166-2:GH	Yes  */
        put("GI", "Gibraltar"); /*  GIB	292	ISO 3166-2:GI	No  */
        put("GR", "Greece"); /*  GRC	300	ISO 3166-2:GR	Yes  */
        put("GL", "Greenland"); /*  GRL	304	ISO 3166-2:GL	No  */
        put("GD", "Grenada"); /*  GRD	308	ISO 3166-2:GD	Yes  */
        put("GP", "Guadeloupe"); /*  GLP	312	ISO 3166-2:GP	No  */
        put("GU", "Guam"); /*  GUM	316	ISO 3166-2:GU	No  */
        put("GT", "Guatemala"); /*  GTM	320	ISO 3166-2:GT	Yes  */
        put("GG", "Guernsey"); /*  GGY	831	ISO 3166-2:GG	No  */
        put("GN", "Guinea"); /*  GIN	324	ISO 3166-2:GN	Yes  */
        put("GW", "Guinea-Bissau"); /*  GNB	624	ISO 3166-2:GW	Yes  */
        put("GY", "Guyana"); /*  GUY	328	ISO 3166-2:GY	Yes  */
        put("HT", "Haiti"); /*  HTI	332	ISO 3166-2:HT	Yes  */
        put("HM", "Heard Island and McDonald Islands"); /*  HMD	334	ISO 3166-2:HM	No  */
        put("VA", "Holy See"); /*  VAT	336	ISO 3166-2:VA	Yes  */
        put("HN", "Honduras"); /*  HND	340	ISO 3166-2:HN	Yes  */
        put("HK", "Hong Kong"); /*  HKG	344	ISO 3166-2:HK	No  */
        put("HU", "Hungary"); /*  HUN	348	ISO 3166-2:HU	Yes  */
        put("IS", "Iceland"); /*  ISL	352	ISO 3166-2:IS	Yes  */
        put("IN", "India"); /*  IND	356	ISO 3166-2:IN	Yes  */
        put("ID", "Indonesia"); /*  IDN	360	ISO 3166-2:ID	Yes  */
        put("IR", "Iran (Islamic Republic of)"); /*  IRN	364	ISO 3166-2:IR	Yes  */
        put("IQ", "Iraq"); /*  IRQ	368	ISO 3166-2:IQ	Yes  */
        put("IE", "Ireland"); /*  IRL	372	ISO 3166-2:IE	Yes  */
        put("IM", "Isle of Man"); /*  IMN	833	ISO 3166-2:IM	No  */
        put("IL", "Israel"); /*  ISR	376	ISO 3166-2:IL	Yes  */
        put("IT", "Italy"); /*  ITA	380	ISO 3166-2:IT	Yes  */
        put("JM", "Jamaica"); /*  JAM	388	ISO 3166-2:JM	Yes  */
        put("JP", "Japan"); /*  JPN	392	ISO 3166-2:JP	Yes  */
        put("JE", "Jersey"); /*  JEY	832	ISO 3166-2:JE	No  */
        put("JO", "Jordan"); /*  JOR	400	ISO 3166-2:JO	Yes  */
        put("KZ", "Kazakhstan"); /*  KAZ	398	ISO 3166-2:KZ	Yes  */
        put("KE", "Kenya"); /*  KEN	404	ISO 3166-2:KE	Yes  */
        put("KI", "Kiribati"); /*  KIR	296	ISO 3166-2:KI	Yes  */
        put("KP", "Korea (Democratic People's Republic of)"); /*  PRK	408	ISO 3166-2:KP	Yes  */
        put("KR", "Korea, Republic of"); /*  KOR	410	ISO 3166-2:KR	Yes  */
        put("KW", "Kuwait"); /*  KWT	414	ISO 3166-2:KW	Yes  */
        put("KG", "Kyrgyzstan"); /*  KGZ	417	ISO 3166-2:KG	Yes  */
        put("LA", "Lao People's Democratic Republic"); /*  LAO	418	ISO 3166-2:LA	Yes  */
        put("LV", "Latvia"); /*  LVA	428	ISO 3166-2:LV	Yes  */
        put("LB", "Lebanon"); /*  LBN	422	ISO 3166-2:LB	Yes  */
        put("LS", "Lesotho"); /*  LSO	426	ISO 3166-2:LS	Yes  */
        put("LR", "Liberia"); /*  LBR	430	ISO 3166-2:LR	Yes  */
        put("LY", "Libya"); /*  LBY	434	ISO 3166-2:LY	Yes  */
        put("LI", "Liechtenstein"); /*  LIE	438	ISO 3166-2:LI	Yes  */
        put("LT", "Lithuania"); /*  LTU	440	ISO 3166-2:LT	Yes  */
        put("LU", "Luxembourg"); /*  LUX	442	ISO 3166-2:LU	Yes  */
        put("MO", "Macao"); /*  MAC	446	ISO 3166-2:MO	No  */
        put("MG", "Madagascar"); /*  MDG	450	ISO 3166-2:MG	Yes  */
        put("MW", "Malawi"); /*  MWI	454	ISO 3166-2:MW	Yes  */
        put("MY", "Malaysia"); /*  MYS	458	ISO 3166-2:MY	Yes  */
        put("MV", "Maldives"); /*  MDV	462	ISO 3166-2:MV	Yes  */
        put("ML", "Mali"); /*  MLI	466	ISO 3166-2:ML	Yes  */
        put("MT", "Malta"); /*  MLT	470	ISO 3166-2:MT	Yes  */
        put("MH", "Marshall Islands"); /*  MHL	584	ISO 3166-2:MH	Yes  */
        put("MQ", "Martinique"); /*  MTQ	474	ISO 3166-2:MQ	No  */
        put("MR", "Mauritania"); /*  MRT	478	ISO 3166-2:MR	Yes  */
        put("MU", "Mauritius"); /*  MUS	480	ISO 3166-2:MU	Yes  */
        put("YT", "Mayotte"); /*  MYT	175	ISO 3166-2:YT	No  */
        put("MX", "Mexico"); /*  MEX	484	ISO 3166-2:MX	Yes  */
        put("FM", "Micronesia (Federated States of)"); /*  FSM	583	ISO 3166-2:FM	Yes  */
        put("MD", "Moldova, Republic of"); /*  MDA	498	ISO 3166-2:MD	Yes  */
        put("MC", "Monaco"); /*  MCO	492	ISO 3166-2:MC	Yes  */
        put("MN", "Mongolia"); /*  MNG	496	ISO 3166-2:MN	Yes  */
        put("ME", "Montenegro"); /*  MNE	499	ISO 3166-2:ME	Yes  */
        put("MS", "Montserrat"); /*  MSR	500	ISO 3166-2:MS	No  */
        put("MA", "Morocco"); /*  MAR	504	ISO 3166-2:MA	Yes  */
        put("MZ", "Mozambique"); /*  MOZ	508	ISO 3166-2:MZ	Yes  */
        put("MM", "Myanmar"); /*  MMR	104	ISO 3166-2:MM	Yes  */
        put("NA", "Namibia"); /*  NAM	516	ISO 3166-2:NA	Yes  */
        put("NR", "Nauru"); /*  NRU	520	ISO 3166-2:NR	Yes  */
        put("NP", "Nepal"); /*  NPL	524	ISO 3166-2:NP	Yes  */
        put("NL", "Netherlands"); /*  NLD	528	ISO 3166-2:NL	Yes  */
        put("NC", "New Caledonia"); /*  NCL	540	ISO 3166-2:NC	No  */
        put("NZ", "New Zealand"); /*  NZL	554	ISO 3166-2:NZ	Yes  */
        put("NI", "Nicaragua"); /*  NIC	558	ISO 3166-2:NI	Yes  */
        put("NE", "Niger"); /*  NER	562	ISO 3166-2:NE	Yes  */
        put("NG", "Nigeria"); /*  NGA	566	ISO 3166-2:NG	Yes  */
        put("NU", "Niue"); /*  NIU	570	ISO 3166-2:NU	No  */
        put("NF", "Norfolk Island"); /*  NFK	574	ISO 3166-2:NF	No  */
        put("MK", "North Macedonia"); /*  MKD	807	ISO 3166-2:MK	Yes  */
        put("MP", "Northern Mariana Islands"); /*  MNP	580	ISO 3166-2:MP	No  */
        put("NO", "Norway"); /*  NOR	578	ISO 3166-2:NO	Yes  */
        put("OM", "Oman"); /*  OMN	512	ISO 3166-2:OM	Yes  */
        put("PK", "Pakistan"); /*  PAK	586	ISO 3166-2:PK	Yes  */
        put("PW", "Palau"); /*  PLW	585	ISO 3166-2:PW	Yes  */
        put("PS", "Palestine, State of"); /*  PSE	275	ISO 3166-2:PS	No  */
        put("PA", "Panama"); /*  PAN	591	ISO 3166-2:PA	Yes  */
        put("PG", "Papua New Guinea"); /*  PNG	598	ISO 3166-2:PG	Yes  */
        put("PY", "Paraguay"); /*  PRY	600	ISO 3166-2:PY	Yes  */
        put("PE", "Peru"); /*  PER	604	ISO 3166-2:PE	Yes  */
        put("PH", "Philippines"); /*  PHL	608	ISO 3166-2:PH	Yes  */
        put("PN", "Pitcairn"); /*  PCN	612	ISO 3166-2:PN	No  */
        put("PL", "Poland"); /*  POL	616	ISO 3166-2:PL	Yes  */
        put("PT", "Portugal"); /*  PRT	620	ISO 3166-2:PT	Yes  */
        put("PR", "Puerto Rico"); /*  PRI	630	ISO 3166-2:PR	No  */
        put("QA", "Qatar"); /*  QAT	634	ISO 3166-2:QA	Yes  */
        put("RE", "Réunion"); /*  REU	638	ISO 3166-2:RE	No  */
        put("RO", "Romania"); /*  ROU	642	ISO 3166-2:RO	Yes  */
        put("RU", "Russian Federation"); /*  RUS	643	ISO 3166-2:RU	Yes  */
        put("RW", "Rwanda"); /*  RWA	646	ISO 3166-2:RW	Yes  */
        put("BL", "Saint Barthélemy"); /*  BLM	652	ISO 3166-2:BL	No  */
        put("SH", "Saint Helena, Ascension and Tristan da Cunha"); /*  SHN	654	ISO 3166-2:SH	No  */
        put("KN", "Saint Kitts and Nevis"); /*  KNA	659	ISO 3166-2:KN	Yes  */
        put("LC", "Saint Lucia"); /*  LCA	662	ISO 3166-2:LC	Yes  */
        put("MF", "Saint Martin (French part)"); /*  MAF	663	ISO 3166-2:MF	No  */
        put("PM", "Saint Pierre and Miquelon"); /*  SPM	666	ISO 3166-2:PM	No  */
        put("VC", "Saint Vincent and the Grenadines"); /*  VCT	670	ISO 3166-2:VC	Yes  */
        put("WS", "Samoa"); /*  WSM	882	ISO 3166-2:WS	Yes  */
        put("SM", "San Marino"); /*  SMR	674	ISO 3166-2:SM	Yes  */
        put("ST", "Sao Tome and Principe"); /*  STP	678	ISO 3166-2:ST	Yes  */
        put("SA", "Saudi Arabia"); /*  SAU	682	ISO 3166-2:SA	Yes  */
        put("SN", "Senegal"); /*  SEN	686	ISO 3166-2:SN	Yes  */
        put("RS", "Serbia"); /*  SRB	688	ISO 3166-2:RS	Yes  */
        put("SC", "Seychelles"); /*  SYC	690	ISO 3166-2:SC	Yes  */
        put("SL", "Sierra Leone"); /*  SLE	694	ISO 3166-2:SL	Yes  */
        put("SG", "Singapore"); /*  SGP	702	ISO 3166-2:SG	Yes  */
        put("SX", "Sint Maarten (Dutch part)"); /*  SXM	534	ISO 3166-2:SX	No  */
        put("SK", "Slovakia"); /*  SVK	703	ISO 3166-2:SK	Yes  */
        put("SI", "Slovenia"); /*  SVN	705	ISO 3166-2:SI	Yes  */
        put("SB", "Solomon Islands"); /*  SLB	090	ISO 3166-2:SB	Yes  */
        put("SO", "Somalia"); /*  SOM	706	ISO 3166-2:SO	Yes  */
        put("ZA", "South Africa"); /*  ZAF	710	ISO 3166-2:ZA	Yes  */
        put("GS", "South Georgia and the South Sandwich Islands"); /*  SGS	239	ISO 3166-2:GS	No  */
        put("SS", "South Sudan"); /*  SSD	728	ISO 3166-2:SS	Yes  */
        put("ES", "Spain"); /*  ESP	724	ISO 3166-2:ES	Yes  */
        put("LK", "Sri Lanka"); /*  LKA	144	ISO 3166-2:LK	Yes  */
        put("SD", "Sudan"); /*  SDN	729	ISO 3166-2:SD	Yes  */
        put("SR", "Suriname"); /*  SUR	740	ISO 3166-2:SR	Yes  */
        put("SJ", "Svalbard and Jan Mayen"); /*  SJM	744	ISO 3166-2:SJ	No  */
        put("SE", "Sweden"); /*  SWE	752	ISO 3166-2:SE	Yes  */
        put("CH", "Switzerland"); /*  CHE	756	ISO 3166-2:CH	Yes  */
        put("SY", "Syrian Arab Republic"); /*  SYR	760	ISO 3166-2:SY	Yes  */
        put("TW", "Taiwan, Province of China"); /*  TWN	158	ISO 3166-2:TW	No  */
        put("TJ", "Tajikistan"); /*  TJK	762	ISO 3166-2:TJ	Yes  */
        put("TZ", "Tanzania, United Republic of"); /*  TZA	834	ISO 3166-2:TZ	Yes  */
        put("TH", "Thailand"); /*  THA	764	ISO 3166-2:TH	Yes  */
        put("TL", "Timor-Leste"); /*  TLS	626	ISO 3166-2:TL	Yes  */
        put("TG", "Togo"); /*  TGO	768	ISO 3166-2:TG	Yes  */
        put("TK", "Tokelau"); /*  TKL	772	ISO 3166-2:TK	No  */
        put("TO", "Tonga"); /*  TON	776	ISO 3166-2:TO	Yes  */
        put("TT", "Trinidad and Tobago"); /*  TTO	780	ISO 3166-2:TT	Yes  */
        put("TN", "Tunisia"); /*  TUN	788	ISO 3166-2:TN	Yes  */
        put("TR", "Türkiye"); /*  TUR	792	ISO 3166-2:TR	Yes  */
        put("TM", "Turkmenistan"); /*  TKM	795	ISO 3166-2:TM	Yes  */
        put("TC", "Turks and Caicos Islands"); /*  TCA	796	ISO 3166-2:TC	No  */
        put("TV", "Tuvalu"); /*  TUV	798	ISO 3166-2:TV	Yes  */
        put("UG", "Uganda"); /*  UGA	800	ISO 3166-2:UG	Yes  */
        put("UA", "Ukraine"); /*  UKR	804	ISO 3166-2:UA	Yes  */
        put("AE", "United Arab Emirates"); /*  ARE	784	ISO 3166-2:AE	Yes  */
        put("GB", "United Kingdom of Great Britain and Northern Ireland"); /*  GBR	826	ISO 3166-2:GB	Yes  */
        put("US", "United States of America"); /*  USA	840	ISO 3166-2:US	Yes  */
        put("UM", "United States Minor Outlying Islands"); /*  UMI	581	ISO 3166-2:UM	No  */
        put("UY", "Uruguay"); /*  URY	858	ISO 3166-2:UY	Yes  */
        put("UZ", "Uzbekistan"); /*  UZB	860	ISO 3166-2:UZ	Yes  */
        put("VU", "Vanuatu"); /*  VUT	548	ISO 3166-2:VU	Yes  */
        put("VE", "Venezuela (Bolivarian Republic of)"); /*  VEN	862	ISO 3166-2:VE	Yes  */
        put("VN", "Viet Nam"); /*  VNM	704	ISO 3166-2:VN	Yes  */
        put("VG", "Virgin Islands (British)"); /*  VGB	092	ISO 3166-2:VG	No  */
        put("VI", "Virgin Islands (U.S.)"); /*  VIR	850	ISO 3166-2:VI	No  */
        put("WF", "Wallis and Futuna"); /*  WLF	876	ISO 3166-2:WF	No  */
        put("EH", "Western Sahara"); /*  ESH	732	ISO 3166-2:EH	No  */
        put("YE", "Yemen"); /*  YEM	887	ISO 3166-2:YE	Yes  */
        put("ZM", "Zambia"); /*  ZMB	894	ISO 3166-2:ZM	Yes  */
        put("ZW", "Zimbabwe"); /*  ZWE	716	ISO 3166-2:ZW	Yes  */
    }};
}
