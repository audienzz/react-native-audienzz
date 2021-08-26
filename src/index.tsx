import { requireNativeComponent, ViewStyle } from 'react-native';

type AudienzzProps = {
  color: string;
  style: ViewStyle;
};

export const AudienzzViewManager =
  requireNativeComponent<AudienzzProps>('AudienzzView');

export default AudienzzViewManager;
