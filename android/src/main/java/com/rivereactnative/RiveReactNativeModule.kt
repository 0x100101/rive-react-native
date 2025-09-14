package com.rivereactnative

import com.facebook.react.bridge.*
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.uimanager.UIManagerModule

class RiveReactNativeModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
  override fun getName() = "RiveReactNativeModule"

  private fun <T> handleState(node: Int, promise: Promise, stateGetter: (RiveReactNativeView) -> T) {
    val uiManager = UIManagerHelper.getUIManager(reactApplicationContext, node)
    val view = uiManager?.resolveView(node) as? RiveReactNativeView
    if (view != null) {
      val value = stateGetter(view)
      promise.resolve(value)
    } else {
      promise.reject("VIEW_NOT_FOUND", "Could not find RiveReactNativeView")
    }
  }

  @ReactMethod
  fun getBooleanState(node: Int, inputName: String, promise: Promise) {
    handleState(node, promise) { view -> view.getBooleanState(inputName) }
  }

  @ReactMethod
  fun getNumberState(node: Int, inputName: String, promise: Promise) {
    handleState(node, promise) { view -> view.getNumberState(inputName) }
  }

  @ReactMethod
  fun getBooleanStateAtPath(node: Int, inputName: String, path: String, promise: Promise) {
    handleState(node, promise) { view -> view.getBooleanStateAtPath(inputName, path) }
  }

  @ReactMethod
  fun getNumberStateAtPath(node: Int, inputName: String, path: String, promise: Promise) {
    handleState(node, promise) { view -> view.getNumberStateAtPath(inputName, path) }
  }

  @ReactMethod
  fun getCurrentPropertyValue(
    reactTag: Int,
    path: String,
    propertyType: String,
    promise: Promise
  ) {
    try {
      val uiManager = UIManagerHelper.getUIManager(reactApplicationContext, reactTag)
      val view = uiManager?.resolveView(reactTag) as? RiveReactNativeView

      if (view == null) {
        promise.resolve(null)
        return
      }

      val propertyTypeEnum = RNPropertyType.mapToRNPropertyType(propertyType)
      val value = when (propertyTypeEnum) {
        RNPropertyType.Boolean -> view.getCurrentBooleanPropertyValue(path)
        RNPropertyType.Color -> view.getCurrentColorPropertyValue(path)
        RNPropertyType.Number -> view.getCurrentNumberPropertyValue(path)
        RNPropertyType.String -> view.getCurrentStringPropertyValue(path)
        RNPropertyType.Enum -> view.getCurrentEnumPropertyValue(path)
        RNPropertyType.Trigger -> null // Triggers have no current value
      }

      promise.resolve(value)
    } catch (e: Exception) {
      promise.reject("GET_PROPERTY_ERROR", e.message, e)
    }
  }

  @ReactMethod
  fun addListener(type: String?) {
    // Keep: Required for RN built in Event Emitter Calls.
  }

  @ReactMethod
  fun removeListeners(type: Int?) {
    // Keep: Required for RN built in Event Emitter Calls.
  }
}
