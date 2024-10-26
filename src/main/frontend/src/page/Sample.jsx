import React, { useState } from "react";
import ThumbnailCropper from "../component/ThumbnailCropper";
import config from "../config/properties";
import fetchWithAuth from "../util/fetchUtil";

export default function Sample() {
  const [thumbnailUrl, setThumbnailUrl] = useState(null);
  const [imageUrl, setImageUrl] = useState(null);

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    const reader = new FileReader();
    reader.onload = (event) => {
      setImageUrl(event.target.result);
    };
    reader.readAsDataURL(file);
  };

  const handleCropComplete = (croppedImage) => {
    setThumbnailUrl(croppedImage); // 切り取られた画像のURLをセット
  };

  const saveBlog = async () => {
    const formData = new FormData();
    formData.append("thumbnail", thumbnailUrl);

    try {
      const response = await fetchWithAuth(
        `${config.apiBaseUrl}/api/blog/save-thumbnail`,
        {
          method: "POST",
          body: formData,
        }
      );

      if (response.ok) {
        console.log("サムネイル画像が保存されました");
      } else {
        console.error("サムネイル画像の保存に失敗しました");
      }
    } catch (error) {
      console.error("サーバーへのリクエストに失敗しました:", error);
    }
  };

  return (
    <div className="Sample">
      <input type="file" accept="image/*" onChange={handleFileChange} />
      {imageUrl && (
        <ThumbnailCropper
          imageUrl={imageUrl}
          onCropComplete={handleCropComplete}
        />
      )}
      {thumbnailUrl && (
        <div>
          <img src={thumbnailUrl} alt="サムネイルプレビュー" />
        </div>
      )}
      <button onClick={saveBlog}>ブログを保存</button>
    </div>
  );
}
