import React, { FC, useEffect } from 'react';
import { View, Text, Dimensions } from 'react-native';
import { connect } from 'react-redux';
import { AppNexusVideoBanner } from 'react-native-audienzz';
import { styles } from '../../constants';

type State = {
  settings: {
    placementIdBanner: string;
    placementIdVideo: string;
  };
};

const SimpleVideoScreen: FC<Props> = ({ navigation, placementIdVideo }) => {
  useEffect(() => {
    navigation.setOptions({
      title: 'Video Ad Example',
    });
  });

  return (
    <View style={styles.container}>
      <Text style={styles.label}>This is an example of the video ad</Text>
      <View style={styles.bannerContainer}>
        <AppNexusVideoBanner
          sizes={[[Dimensions.get('window').width, 300]]}
          placementId={placementIdVideo}
          keywords={{
            environment: 'test',
          }}
          onAdLoadSuccess={() => console.log('Loaded!')}
          onAdLoadFail={() => console.log('Failed!')}
        />
      </View>
    </View>
  );
};

const mstp = (state: State) => {
  return {
    placementIdVideo: state.settings.placementIdVideo,
  };
};

type Props = ReturnType<typeof mstp> & {
  navigation: any;
};

export default connect(mstp)(SimpleVideoScreen);
