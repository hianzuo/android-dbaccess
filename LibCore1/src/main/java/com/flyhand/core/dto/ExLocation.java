package com.flyhand.core.dto;

/**
 * Created with IntelliJ IDEA.
 * User: ryan
 * Date: 12-4-14
 * Time: Afternoon 9:47
 */
public class ExLocation {
    public double lat;
    public double lng;
    public String country;
    public String countryCode;
    public String region;
    public String streetNumber;
    public String postalCode;
    public String city;
    public String area;
    public String street;
    public String formatAddress;

    @Override
    public String toString() {
        return "ExLocation{" +
                "lat=" + lat +
                ", lng=" + lng +
                ", country='" + country + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", region='" + region + '\'' +
                ", streetNumber='" + streetNumber + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", city='" + city + '\'' +
                ", area='" + area + '\'' +
                ", street='" + street + '\'' +
                ", formatAddress='" + formatAddress + '\'' +
                '}';
    }

    public String getAddress() {
        StringBuilder sb = new StringBuilder("");
        if (null != country) {
            sb.append(this.country.trim());
        }
        if (null != region) {
            sb.append(this.region.trim());
        }
        if (null != city) {
            sb.append(this.city.trim());
        }
        if (null != area) {
            sb.append(this.area.trim());
        }
        if (null != street) {
            sb.append(this.street.trim());
        }
        if (null != streetNumber) {
            sb.append(this.streetNumber.trim());
        }
        return sb.toString();
    }
}
