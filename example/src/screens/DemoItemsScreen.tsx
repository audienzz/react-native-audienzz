import React, { FC, useEffect } from 'react';
import { View, Text, ScrollView, TouchableOpacity } from 'react-native';
import { connect } from 'react-redux';
import { styles } from '../constants';
import { DEMO_ITEMS, PLACEMENT_ID_TYPES } from '../constants/values';
import TextInputWithButton from '../components/ui/TextInputWithButton';
import {
  RootState,
  setPlacementIdBannerSuccess,
  setPlacementIdVideoSuccess,
} from '../modules/settings';

const DemoItemsList: FC<Props> = ({
  navigation,
  setIdBannerSuccess,
  setIdVideoSuccess,
  placementIdBanner,
  placementIdVideo,
}) => {
  useEffect(() => {
    navigation.setOptions({
      title: 'Banner Ad Example',
    });
  });

  const onItemPressHandler = (screen: string) => {
    navigation.navigate(screen);
  };

  const onPlacementIdChangeHandler = (val: string, type: string) => {
    if (type === PLACEMENT_ID_TYPES.BANNER) {
      setIdBannerSuccess(val);
    } else {
      setIdVideoSuccess(val);
    }
  };

  const getDemoItemsList = () => {
    return DEMO_ITEMS.map((item, idx) => (
      <TouchableOpacity
        key={idx}
        style={styles.itemContainer}
        onPress={() => onItemPressHandler(item.screen)}
      >
        <Text>{item.title}</Text>
      </TouchableOpacity>
    ));
  };

  return (
    <View style={styles.container}>
      <TextInputWithButton
        title={'Banner Placement ID'}
        containerStyle={styles.placementIdSelectorContainer}
        id={placementIdBanner}
        onValueSave={(val: string) =>
          onPlacementIdChangeHandler(val, PLACEMENT_ID_TYPES.BANNER)
        }
      />
      <TextInputWithButton
        title={'Video Banner Placement ID'}
        containerStyle={styles.placementIdSelectorContainer}
        id={placementIdVideo}
        onValueSave={(val: string) =>
          onPlacementIdChangeHandler(val, PLACEMENT_ID_TYPES.VIDEO)
        }
      />
      <ScrollView style={styles.scrollView}>{getDemoItemsList()}</ScrollView>
    </View>
  );
};

type Props = ReturnType<typeof mstp> &
  ReturnType<typeof mdtp> & {
    navigation: any;
    setPlacementIdBanner: () => void;
    setPlacementIdVideo: () => void;
    placementIdBanner: string;
    placementIdVideo: string;
  };

const mstp = (state: RootState) => {
  return {
    placementIdBanner: state.settings.placementIdBanner,
    placementIdVideo: state.settings.placementIdVideo,
  };
};

const mdtp = (dispatch: any) => ({
  setIdBannerSuccess: (id: string) => dispatch(setPlacementIdBannerSuccess(id)),
  setIdVideoSuccess: (id: string) => dispatch(setPlacementIdVideoSuccess(id)),
});

export default connect(mstp, mdtp)(DemoItemsList);
