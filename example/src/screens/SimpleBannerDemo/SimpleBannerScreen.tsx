import React, { FC, useEffect } from 'react';
import { View, Text } from 'react-native';
import { connect } from 'react-redux';
import { AppNexusBanner } from 'react-native-audienzz';
import { styles } from '../../constants';

type State = {
  placementIdBanner: string;
  placementIdVideo: string;
};

type RootState = {
  settings: State;
};

const SimpleBannerScreen: FC<Props> = ({ navigation, placementIdBanner }) => {
  useEffect(() => {
    navigation.setOptions({
      title: 'Banner Ad Example',
    });
  });

  return (
    <View style={styles.container}>
      <Text style={styles.label}>This is an example of the banner ad</Text>
      <View style={styles.bannerContainer}>
        <AppNexusBanner
          reloadOnAppStateChangeIfFailed
          placementId={placementIdBanner}
          sizes={[[300, 250]]}
          autoRefreshInterval={30}
          keywords={{
            environment: 'test',
          }}
          onAdLoadSuccess={() => console.log('Loaded!')}
          onAdLoadFail={() => console.log('Failed!')}
          // allowVideo
        />
      </View>
    </View>
  );
};

const mstp = (state: RootState) => {
  return {
    placementIdBanner: state.settings.placementIdBanner,
  };
};

type Props = ReturnType<typeof mstp> & {
  navigation: any;
};

export default connect(mstp)(SimpleBannerScreen);
