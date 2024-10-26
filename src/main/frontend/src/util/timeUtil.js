export const getDate = (dateString) => {
  // 文字列をDateオブジェクトに変換
  const date = new Date(dateString);

  // 年、月、日を取得してフォーマット
  const year = date.getFullYear();
  const month = date.getMonth() + 1; // 月は0から始まるので +1 する
  const day = date.getDate();

  // フォーマットされた文字列を返す
  return `${year}-${month}-${day}`;
};
