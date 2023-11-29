import React, {FC, useEffect, useState} from 'react';
import {View, Text, ScrollView} from 'react-native';
import {connect} from 'react-redux';
import FastImage from 'react-native-fast-image';
import {AppNexusBanner} from 'react-native-audienzz';
import {styles} from '../../constants';
import {ITEM_TYPES, itemBanner} from '../../constants/values';

type State = {
  placementIdBanner: string;
  placementIdVideo: string;
};

type RootState = {
  settings: State;
};

const FlatListItemScreen: FC<Props> = ({
  navigation,
  route,
  placementIdBanner,
}) => {
  const [item, setItem] = useState<itemBanner>({
    body: '',
    description: '',
    id: '',
    image: '',
    title: '',
    type: '',
  });

  useEffect(() => {
    navigation.setOptions({
      title: 'Item example',
    });
  });

  useEffect(() => {
    setItem(route.params.item);
  }, [route]);

  return (
    <View style={styles.containerItem}>
      <ScrollView showsVerticalScrollIndicator={false}>
        <View style={styles.itemItem}>
          {item.type === ITEM_TYPES.TEXT_WITH_IMAGE && (
            <FastImage
              source={{uri: item.image}}
              resizeMode={FastImage.resizeMode.cover}
              style={styles.itemImageItem}
            />
          )}
          <Text
            style={[
              styles.itemTitleLabelItem,
              item.type === ITEM_TYPES.TEXT
                ? styles.itemTitleLabelNoImagItem
                : null,
            ]}>
            {item.title}
          </Text>
          <Text style={styles.itemDescriptionLabelItem}>
            {item.description}
          </Text>
          <View style={styles.adContainerItem}>
            <AppNexusBanner
              placementId={placementIdBanner}
              sizes={[[300, 250]]}
              keywords={{
                environment: 'test',
              }}
              autoRefreshInterval={60}
            />
          </View>
          <Text style={styles.itemBodyLabelItem}>{item.body}</Text>
        </View>
      </ScrollView>
    </View>
  );
};

const mstp = (state: RootState) => {
  return {
    placementIdBanner: state.settings.placementIdBanner,
  };
};

type RouteParams = {
  params: {
    item: itemBanner;
  };
};

type Props = ReturnType<typeof mstp> & {
  navigation: any;
  route: RouteParams;
};

export default connect(mstp)(FlatListItemScreen);
