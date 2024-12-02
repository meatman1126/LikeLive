import heic2any from "heic2any";

/**
 *
 * @param {*} file 変換対象の画像ファイル
 * @returns jpeg形式に変換された画像ファイル
 */
export const convertHEICtoJpeg = async (file) => {
  try {
    console.log(file.type);
    console.log(file);
    // HEIC形式かどうかを判定
    if (file.type === "image/heic" || file.type === "image/heif") {
      // HEICファイルをJPEGに変換
      const convertedFile = await convertHEICToJPEG(file);
      return convertedFile;
    } else {
      // HEIC形式ではない場合何もしない
      return file;
    }
  } catch (error) {
    console.error(error);
  }
};
// HEIC形式をJPEGに変換する関数
const convertHEICToJPEG = async (imageFile) => {
  const outputBlob = await heic2any({
    blob: imageFile,
    toType: "image/jpeg",
  });
  return outputBlob;
};
