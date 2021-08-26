import * as React from 'react';

import { StyleSheet, View } from 'react-native';
import { AppNexusBanner } from 'react-native-audienzz';

export default function App() {
  return (
    <View style={styles.container}>
      <AppNexusBanner placementId="123" sizes={[[100, 200]]} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
