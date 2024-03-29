/*
 *    Copyright 2014 APPNEXUS INC
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.appnexus.opensdk;

import android.graphics.Bitmap;
import android.view.View;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public interface NativeAdResponse {

    String NATIVE_ELEMENT_OBJECT = "ELEMENT";

    public enum Network{
        FACEBOOK,
        APPNEXUS,
        ADMOB,
        CUSTOM
    }
    /**
     *
     * @return Network Identifier object
     */
    public Network getNetworkIdentifier();

    /**
     * The Title for this native ad
     *
     * @return empty string "" if not present
     */
    public String getTitle();

    /**
     * The text description of the ad , the text body
     *
     * @return empty string "" if not present
     */
    public String getDescription();

    /**
     * The URL of the main image can be used to manage image resources manually
     *
     * @return The URL of the main image or empty string "" if not present
     */
    public String getImageUrl();

    /**
     * Retrieve the main image resource, null if there was no image available or if
     * shouldLoadImage() was set to false.
     *
     * @return The Bitmap of the main image
     */
    public Bitmap getImage();

    /**
     * Set the main image resource
     *
     * @param bitmap The Bitmap of the main image
     */
    public void setImage(Bitmap bitmap);

    /**
     * Retrieve the AdResponseInfo
     *
     * @return {@link ANAdResponseInfo}
     */
    public ANAdResponseInfo getAdResponseInfo();

    /**
     * Set the AdResponseInfo
     *
     * @param adResponseInfo
     */
    public void setAdResponseInfo(ANAdResponseInfo adResponseInfo);


    /**
     * The URL of the Icon image resource can be used to manage image resources manually
     *
     * @return The URL of the Icon or empty string "" if not present
     */
    public String getIconUrl();

    /**
     * Retrieve the icon image resource, null if there was no image available or if
     * shouldLoadIcon returned false for the ad call
     *
     * @return The Bitmap of the Icon
     */
    public Bitmap getIcon();

    /**
     * Set the icon image resource
     *
     * @param bitmap The Bitmap of the main icon
     */
    public void setIcon(Bitmap bitmap);

    /**
     * The text for the call to action
     *
     * @return empty string "" if not present
     */
    public String getCallToAction();

    /**
     * Retrieve a map of all elements within the native ad response.
     *
     * @return A map of the native ad elements
     */
    public HashMap<String, Object> getNativeElements();

    /**
     * Retrieve rating from the native ad response.
     */
    public Rating getAdStarRating();


    /**
     * Retrieve sponspored by text from native ad response
     */
    public String getSponsoredBy();

    /**
     * Determine if this Response has expired
     *
     * @return true if it has expired
     */
    public boolean hasExpired();


    /**
     * Destroy the response
     */
    public void destroy();

    public class MainMedia {

    }

    public class Rating{
        private final double value;
        private final double scale;

        public Rating(double value, double scale) {
            this.value = value;
            this.scale = scale;
        }

        public double getValue() { return this.value; }

        public double getScale() { return this.scale; }
    }

    public class ImageSize {
        private final int width;
        private final int height;

        public ImageSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() { return this.width; }

        public int getHeight() { return this.height; }
    }
    /**
     * Retrieve Image Size from the native ad response.
     *
     * @return ImageSize with width and height set to -1, if not present
     */
    public ImageSize getImageSize();


    /**
     * Retrieve the Additional description
     *
     * @return empty string "" if not present
     *
     */
    public String getAdditionalDescription();

    /**
     * Retrieve Icon Image Size from the native ad response.
     *
     * @return ImageSize with width and height set to -1, if not present
     */
    public ImageSize getIconSize();

    /**
     * Retrieve Video VAST content from the native ad response.
     *
     * @return empty string "" if not present
     */
    public String getVastXml();

    /**
     * Retrieve Privacy Link from the native ad response.
     *
     * @return empty string "" if not present
     */
    public String getPrivacyLink();

}
