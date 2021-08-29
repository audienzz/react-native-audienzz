import { StyleSheet } from 'react-native';
import colors from './colors';

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },
  scrollView: {
    marginTop: 16,
  },
  placementIdSelectorContainer: {
    marginTop: 16,
    marginHorizontal: 16,
  },
  itemContainer: {
    height: 48,
    marginBottom: 16,
    backgroundColor: colors.white,
    justifyContent: 'center',
    paddingLeft: 16,

    elevation: 1,
    shadowOffset: { width: 0, height: 0 },
    shadowColor: colors.blackish,
    shadowOpacity: 0.05,
    shadowRadius: 8,
  },
  itemTitleLabel: {
    fontSize: 16,
  },
  itemDescriptionLabel: {
    fontSize: 14,
  },
  containerTextInput: {},
  textInputContainer: {
    flex: 1,
  },
  textInputWrapper: {
    flexDirection: 'row',
  },
  titleLabel: {
    marginBottom: 8,
    fontWeight: '600',
    opacity: 0.4,
  },
  textInput: {
    backgroundColor: colors.white,
    fontSize: 22,
    padding: 8,
    borderRadius: 8,

    elevation: 5,
    shadowOffset: { width: 0, height: 0 },
    shadowColor: colors.blackish,
    shadowOpacity: 0.05,
    shadowRadius: 8,
  },
  textInputDisabled: {
    opacity: 0.5,
  },
  button: {
    width: 80,
    alignItems: 'center',
    justifyContent: 'center',
  },
  buttonLabel: {
    color: colors.primary,
    fontSize: 16,
    fontWeight: '500',
  },
  containerBanner: {
    flex: 1,
    backgroundColor: colors.background,
  },
  label: {
    fontSize: 16,
    padding: 16,
    textAlign: 'center',
  },
  bannerContainer: {
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: colors.white,
    paddingVertical: 16,

    elevation: 5,
    shadowOffset: { width: 0, height: 0 },
    shadowColor: '#000',
    shadowOpacity: 0.05,
    shadowRadius: 8,
  },
  containerFlatList: {
    flex: 1,
    backgroundColor: colors.background,
  },
  item: {
    backgroundColor: colors.white,
    padding: 16,
    marginVertical: 8,

    borderBottomWidth: 1,
    borderTopWidth: 1,
    borderColor: '#0000000F',
  },
  itemImage: {
    height: 200,
  },
  adContainer: {
    backgroundColor: colors.white,
    alignItems: 'center',
    padding: 16,
    marginVertical: 8,

    borderBottomWidth: 1,
    borderTopWidth: 1,
    borderColor: '#0000000F',
  },
  itemTitleLabelFlatList: {
    fontSize: 20,
    marginTop: 16,
    fontWeight: '600',
  },
  itemTitleLabelNoImage: {
    marginTop: 0,
  },
  itemDescriptionLabelFlatList: {
    marginTop: 4,
    fontSize: 16,
  },
  loadingIndicator: {
    flex: 1,
  },
  containerItem: {
    flex: 1,
    backgroundColor: colors.background,
  },
  itemItem: {
    backgroundColor: colors.white,
    padding: 16,

    borderBottomWidth: 1,
    borderTopWidth: 1,
    borderColor: '#0000000F',
  },
  adContainerItem: {
    alignItems: 'center',
  },
  itemImageItem: {
    height: 200,
    width: '100%',
  },
  itemTitleLabelItem: {
    fontSize: 20,
    marginTop: 16,
    fontWeight: '600',
  },
  itemTitleLabelNoImagItem: {
    marginTop: 0,
  },
  itemDescriptionLabelItem: {
    marginTop: 4,
    fontSize: 16,
  },
  itemBodyLabelItem: {
    marginTop: 4,
    fontSize: 16,
  },
  viewAd: {
    alignItems: 'center',
  },
});

export default styles;
