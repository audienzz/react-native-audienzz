import React, { FC, useEffect, useState } from 'react';
import {
  View,
  FlatList,
  Text,
  TouchableOpacity,
  ActivityIndicator,
  Image,
  Dimensions,
} from 'react-native';
import { connect } from 'react-redux';
import FastImage from 'react-native-fast-image';
import { AppNexusBanner, AppNexusVideoBanner } from 'react-native-audienzz';
import { styles } from '../../constants';
import {
  ITEM_TYPES,
  BANNER_ADS_PLACES,
  VIDEO_ADS_PLACES,
  itemBanner,
} from '../../constants/values';
// @ts-ignore
import posts from '../../assets/data/posts.json';

type State = {
  placementIdBanner: string;
  placementIdVideo: string;
};

type RootState = {
  settings: State;
};

const FlatListScreen: FC<Props> = ({
  navigation,
  placementIdBanner,
  placementIdVideo,
}) => {
  useEffect(() => {
    navigation.setOptions({
      title: 'Banner Ad Example',
    });
  });

  // @ts-ignore
  const [items, setItems] = useState<itemBanner[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  const openItem = (item: object) => {
    navigation.navigate('FlatListItemScreen', { item });
  };

  const renderItem = (item: itemBanner, index: number) => {
    const { type } = item;
    switch (type) {
      case ITEM_TYPES.BANNER_AD: {
        return (
          <View style={styles.viewAd}>
            <AppNexusBanner
              reloadOnAppStateChangeIfFailed
              autoRefreshInterval={30}
              placementId={placementIdBanner}
              sizes={[[300, 250]]}
              keywords={{
                environment: 'test',
              }}
              allowVideo
              onAdLoadSuccess={() =>
                console.log(`Loaded! Item index = ${index}`)
              }
              onAdLoadFail={() => console.log(`Failed! Item index = ${index}`)}
            />
          </View>
        );
      }
      case ITEM_TYPES.VIDEO_AD: {
        return (
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
        );
      }
      case ITEM_TYPES.TEXT_WITH_IMAGE: {
        return (
          <TouchableOpacity
            style={styles.item}
            onPress={() => openItem(item)}
            activeOpacity={0.95}
          >
            <Image
              source={{ uri: item.image }}
              resizeMode={FastImage.resizeMode.cover}
              style={styles.itemImage}
            />
            <Text style={styles.itemTitleLabelFlatList}>{item.title}</Text>
            <Text style={styles.itemDescriptionLabelFlatList}>
              {item.description}
            </Text>
          </TouchableOpacity>
        );
      }
      case ITEM_TYPES.TEXT: {
        return (
          <TouchableOpacity
            style={styles.item}
            onPress={() => openItem(item)}
            activeOpacity={0.95}
          >
            <Text
              style={[
                styles.itemTitleLabelFlatList,
                styles.itemTitleLabelNoImage,
              ]}
            >
              {item.title}
            </Text>
            <Text style={styles.itemDescriptionLabelFlatList}>
              {item.description}
            </Text>
          </TouchableOpacity>
        );
      }
      default:
        return null;
    }
  };

  const fillPostsWithAds = (rows: itemBanner[]) => {
    const listItems: itemBanner[] = [];

    rows.forEach((row: itemBanner, idx: number) => {
      if (BANNER_ADS_PLACES.includes(idx + 1)) {
        listItems.push({
          body: '',
          description: '',
          image: '',
          title: '',
          // @ts-ignore
          id:
            Math.random().toString(36).substring(2, 15) +
            Math.random().toString(36).substring(2, 15),
          // @ts-ignore
          type: ITEM_TYPES.BANNER_AD,
        });
      }
      if (VIDEO_ADS_PLACES.includes(idx + 1)) {
        listItems.push({
          body: '',
          description: '',
          image: '',
          title: '',
          // @ts-ignore
          id:
            Math.random().toString(36).substring(2, 15) +
            Math.random().toString(36).substring(2, 15),
          // @ts-ignore
          type: ITEM_TYPES.VIDEO_AD,
        });
      }
      listItems.push(row);
    });

    return listItems;
  };

  useEffect(() => {
    const getItems = () => {
      try {
        setItems(fillPostsWithAds(posts));
        setLoading(false);
      } catch (error) {
        console.error('getItems: ', error);
        setLoading(false);
      }
    };

    getItems();
  }, []);

  return (
    <View style={styles.containerFlatList}>
      {loading && (
        <ActivityIndicator size="large" style={styles.loadingIndicator} />
      )}
      <FlatList
        // @ts-ignore
        keyExtractor={(item) => item.id.toString()}
        showsVerticalScrollIndicator={false}
        data={items}
        extraData={items}
        renderItem={({ item, index }) => renderItem(item, index)}
      />
    </View>
  );
};

const mstp = (state: RootState) => {
  return {
    placementIdBanner: state.settings.placementIdBanner,
    placementIdVideo: state.settings.placementIdVideo,
  };
};

type Props = ReturnType<typeof mstp> & {
  navigation: any;
};

export default connect(mstp)(FlatListScreen);
