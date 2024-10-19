export const getDisplayNameByDescription = (enumArray, description) => {
  const result = enumArray.find((item) => item.description === description);
  return result ? result.displayName : undefined;
};

/**
 * descriptionからcodeを取得するユーティリティメソッド
 * @param {Array} enumArray - Enum配列 (例: blogCategories, blogStatusなど)
 * @param {string} description - 検索するdescription
 * @return {number | undefined} - 合致するcode、見つからない場合はundefinedを返す
 */
export const getCodeByDescription = (enumArray, description) => {
  const result = enumArray.find((item) => item.description === description);
  return result ? result.code : undefined;
};
