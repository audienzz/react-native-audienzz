const PLACEMENT_ID = {
  DEFAULT_PLACEMENT_ID_BANNER: '15624474',
  DEFAULT_PLACEMENT_ID_VIDEO: '15624512',
};

const DEMO_ITEMS = [
  {
    title: 'Banner Ad Example',
    screen: 'SimpleBannerDemo',
  },
  {
    title: 'Video Ad Example',
    screen: 'SimpleVideoDemo',
  },
  {
    title: 'Flat List (News Feed) Example',
    screen: 'FlatListDemo',
  },
];

const PLACEMENT_ID_TYPES = {
  BANNER: 'BANNER',
  VIDEO: 'VIDEO',
};

const MODES = {
  VIEW: 'VIEW',
  EDIT: 'EDIT',
};

const ITEM_TYPES = {
  TEXT_WITH_IMAGE: 'TEXT_WITH_IMAGE',
  TEXT: 'TEXT',
  BANNER_AD: 'BANNER_AD',
  VIDEO_AD: 'VIDEO_AD',
};

const BANNER_VISIBLE_TYPES = {
  0: 'BANNER_NOT_VISIBLE',
  1: 'BANNER_PARTIALLY_VISIBLE',
  2: 'BANNER_FULLY_VISIBLE',
};

const BANNER_ADS_PLACES: number[] = [2, 6, 10, 22];

const VIDEO_ADS_PLACES: number[] = [];

export interface itemBanner {
  id: string;
  type: string;
  title: string;
  description: string;
  image: string;
  body: string;
}

export {
  PLACEMENT_ID,
  DEMO_ITEMS,
  PLACEMENT_ID_TYPES,
  MODES,
  ITEM_TYPES,
  BANNER_ADS_PLACES,
  VIDEO_ADS_PLACES,
  BANNER_VISIBLE_TYPES,
};
