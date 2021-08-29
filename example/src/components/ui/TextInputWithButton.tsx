import React, { FC, useState } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleProp,
} from 'react-native';
import { styles } from '../../constants';
import { MODES } from '../../constants/values';

const TextInputWithButton: FC<Props> = ({
  id,
  onValueSave,
  containerStyle,
  title,
}) => {
  const [value, setValue] = useState(id);
  const [mode, setMode] = useState(MODES.VIEW);

  const onChangeTextHandler = (text: string) => {
    setValue(text);
  };

  const onButtonPress = () => {
    if (mode === MODES.EDIT) {
      onValueSave(value);
      setMode(MODES.VIEW);
    } else if (mode === MODES.VIEW) {
      setMode(MODES.EDIT);
    }
  };

  return (
    <View style={[styles.containerTextInput, containerStyle]}>
      <Text style={styles.titleLabel}>{title}</Text>
      <View style={styles.textInputWrapper}>
        <View style={styles.textInputContainer}>
          <TextInput
            style={[
              styles.textInput,
              mode === MODES.VIEW ? styles.textInputDisabled : null,
            ]}
            editable={mode === MODES.EDIT}
            value={value}
            keyboardType="number-pad"
            onChangeText={onChangeTextHandler}
          />
        </View>
        <TouchableOpacity style={styles.button} onPress={onButtonPress}>
          <Text style={styles.buttonLabel}>
            {mode === MODES.VIEW ? 'Edit' : 'Save'}
          </Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};

type Props = {
  id: string;
  onValueSave: (value: string) => void;
  containerStyle: StyleProp<any>;
  title: string;
};

export default TextInputWithButton;
