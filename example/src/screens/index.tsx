import React, {FC} from 'react';
import {NavigationContainer} from '@react-navigation/native';
import {createStackNavigator} from '@react-navigation/stack';

import DemoItemsScreen from './DemoItemsScreen';
import SimpleBannerDemo from './SimpleBannerDemo';
import SimpleVideoDemo from './SimpleVideoDemo';
import {FlatListDemo, FlatListItemScreen} from './FlatListDemo';

const Navigator: FC = () => {
  const Stack = createStackNavigator();

  return (
    <NavigationContainer>
      <Stack.Navigator>
        <Stack.Screen name="DemoItemsScreen" component={DemoItemsScreen} />
        <Stack.Screen name="SimpleBannerDemo" component={SimpleBannerDemo} />
        <Stack.Screen name="SimpleVideoDemo" component={SimpleVideoDemo} />
        <Stack.Screen name="FlatListDemo" component={FlatListDemo} />
        <Stack.Screen
          name="FlatListItemScreen"
          component={FlatListItemScreen}
        />
      </Stack.Navigator>
    </NavigationContainer>
  );
};

export default Navigator;
