import { PLACEMENT_ID } from '../constants/values';

/**
 * Actions
 */
const SET_PLACEMENT_ID_BANNER_SUCCESS =
  'app/settings/SET_PLACEMENT_ID_BANNER_SUCCESS';
const SET_PLACEMENT_ID_VIDEO_SUCCESS =
  'app/global/SET_PLACEMENT_ID_VIDEO_SUCCESS';

/**
 * Initial state
 */

export type State = {
  placementIdBanner: string;
  placementIdVideo: string;
};

export type RootState = {
  settings: State;
};

const initialState: State = {
  placementIdBanner: PLACEMENT_ID.DEFAULT_PLACEMENT_ID_BANNER,
  placementIdVideo: PLACEMENT_ID.DEFAULT_PLACEMENT_ID_VIDEO,
};

/**
 * Reducer
 */
const AppReducer: (
  state: State,
  action: any
) => { placementIdVideo: string; placementIdBanner: string } = (
  state = initialState,
  action: any
) => {
  switch (action.type) {
    case SET_PLACEMENT_ID_BANNER_SUCCESS:
      return {
        ...state,
        placementIdBanner: action.payload,
      };
    case SET_PLACEMENT_ID_VIDEO_SUCCESS:
      return {
        ...state,
        placementIdVideo: action.payload,
      };
    default:
      return state;
  }
};

export default AppReducer;

/**
 * Action creators
 */

export const setPlacementIdBannerSuccess = (id: any) => ({
  type: SET_PLACEMENT_ID_BANNER_SUCCESS,
  payload: id,
});
export const setPlacementIdVideoSuccess = (id: any) => ({
  type: SET_PLACEMENT_ID_VIDEO_SUCCESS,
  payload: id,
});
