// Class with functions for working with Map
class UtilsMap {
  //============================================================================
  // Function for getting value for specific key from Map
  static dynamic getValueForKey(Map<String, dynamic> paramMap, String paramKey) {
    dynamic returnValue;

    if(paramMap.containsKey(paramKey)) {
      returnValue = paramMap[paramKey];
    }

    return returnValue;
  }
}