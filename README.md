# React Native Audienzz Library (AppNexus)

React Native implementation of the AppNexus SDK (TypeScript).


## Installation

```bash
yarn add react-native-audienzz

(cd ios && pod install)
```

You don't have to manually link this library as it supports React Native auto-linking.


## Usage

There are 2 types of ads implemented in the library: AppNexusBanner and AppNexusVideoBanner. Even though Banner ads may show videos as well (`allowVideo` property), AppNexusVideoBanner has its own separate instance.

### AppNexusBanner

AppNexusBanner component is used to display banners of different sizes.

**Component example:**

```js

import { AppNexusBanner } from 'react-native-audienzz';

  <AppNexusBanner
    placementId={"123456789"}
    sizes={[[300, 250]]}
    reloadOnAppStateChangeIfFailed
    autoRefreshInterval={30}
    keywords={{
      environment: 'test',
    }}
    allowVideo
    onAdLoadSuccess={() => console.log(`Loaded!`)}
    onAdLazyLoadSuccess={() => console.log(`Lazy loaded!`)}
    onAdLoadFail={() => {
      console.log(`Load failed!`);
    }}
    onAdVisibleChange={(visibilityType) => {
      console.log(`The ad visibility has changed`)
    }}
  />

```
**AppNexusBanner props:**

| Name | Description | Required | Type | Example |
| --- | --- | :---: | --- | --- |
| `placementId` | The placement ID identifies your banner in the system. You should have a valid, active placement ID to monetize your application | **YES** | String | `"1234567"` |
| `sizes` | An array of banner ad sizes to request | **YES** | Array | `[[300,250], [320,460]]` |
| `allowVideo` | Enabling Video Ads (outstream only). For the instream video ads, please refer to the `AppNexusVideoBanner`.<br>Default: `false`<br>[AppNexus documentation](https://wiki.xandr.com/display/sdk/Show+Multi-Format+Banner+Ads+on+iOS): depending on which type of creative has the highest bid, either VAST video or regular HTML banner ads will appear in the app. The mechanism for deciding which type of ad to show is handled automatically for you by the SDK.| No | Boolean | `true`  |
| `autoRefreshInterval` | The interval (in seconds) on how often to reload the banner ad. The minimum allowed is 15, default is 30. To disable autorefresh, set to 0. | No | Number | `30` |
| `keywords` | Add a custom keyword to the request URL for the ad. This is used to set custom targeting parameters within the AppNexus platform. You will be given the keys and values to use by your AppNexus account representative or your ad network. | No | Object | `{ environment: 'test' }` |
| `onAdLoadSuccess` | A callback triggered when the ad is loaded and placed within the view. | No | Function | `() => console.log("The ad has been loaded")` |
| `onAdLazyLoadSuccess` | A callback triggered when the ad is pre-loaded. | No | Function | `() => console.log("The ad is lazy loaded / preloaded")` |
| `onAdVisibleChange` | A callback triggered when the visibility of the banner has been changed. Returns a visibility type (`0` - if banner is not visible, `1` - if banner is partially visible, `2` - If banner is fully visible  | No | Function | `(visibilityType: number) => console.log("The visibility has changed")` |
| `onAdLoadFail` | A callback triggered when the ad request to the server has failed. | No | Function | `() => console.log("The ad hasn't been loaded")` |
| `reloadOnAppStateChangeIfFailed` | Reloading ad if the app state has changed (background -> foreground). Works only if the ad is shown within the viewport and it is not loaded. Default: `false` | No | Boolean | `true` |


### AppNexusVideoBanner

AppNexusVideoBanner component is used to display video banners.

**Component example:**

```js

import { AppNexusVideoBanner } from 'react-native-audienzz';

  <AppNexusVideoBanner
    placementId={"123456789"}
    sizes={[[300, 250]]}
    keywords={{
      environment: 'test',
    }}
    onAdLoadSuccess={() => console.log('The video ad has been loaded!')}
    onAdLoadFail={() => console.log('The video ad has failed to load!')}
    onAdVisibleChange={(visibilityType) => {
      console.log(`The ad visibility has changed`)
    }}
  />

```
**AppNexusVideoBanner props:**

| Name | Description | Required | Type | Example |
| --- | --- | :---: | --- | --- |
| `placementId` | The placement ID identifies your banner in the system. You should have a valid, active placement ID to monetize your application | **YES** | String | `"1234567"` |
| `sizes` | An array of banner ad sizes to request | **YES** | Array | `[[300,250], [320,460]]` |
| `keywords` | Add a custom keyword to the request URL for the ad. This is used to set custom targeting parameters within the AppNexus platform. You will be given the keys and values to use by your AppNexus account representative or your ad network. | No | Object | `{ environment: 'test' }` |
| `onAdLoadSuccess` | A callback triggered when the ad is loaded and placed within the view. | No | Function | `() => console.log("The ad has been loaded")` |
| `onAdVisibleChange` | A callback triggered when the visibility of the video banner has been changed. Returns a visibility type (`0` - if banner is not visible, `1` - if banner is partially visible, `2` - If banner is fully visible  | No | Function | `(visibilityType: number) => console.log("The visibility has changed")` |
| `onAdLoadFail` | A callback triggered when the ad request to the server has failed. | No | Function | `() => console.log("The ad hasn't been loaded")` |



## Changelog

Our Changelog is handled by Github releases. [See all releases](https://github.com/audienzzag/react-native-audienzz/releases)