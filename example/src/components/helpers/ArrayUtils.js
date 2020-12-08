class ArrayUtils {
  removeAllElements(arr, value) {
    return arr.filter(function (ele) {
      return JSON.stringify(ele) !== JSON.stringify(value);
    });
  }
}

export default new ArrayUtils();
