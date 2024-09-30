import config from "../config/properties";
// リフレッシュトークンで新しいアクセストークンを取得する関数
const refreshAccessToken = async () => {
  const refreshToken = localStorage.getItem("ll_refreshToken");
  if (!refreshToken) {
    throw new Error("リフレッシュトークンが存在しません");
  }

  const response = await fetch(`${config.apiBaseUrl}/api/oauth/refresh`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      refresh_token: refreshToken,
    }),
  });

  if (!response.ok) {
    throw new Error("アクセストークンの更新に失敗しました");
  }

  const data = await response.json();
  console.log(data);
  const newAccessToken = data.access_token;

  // 新しいアクセストークンを保存
  localStorage.setItem("ll_accessToken", newAccessToken);
  // 有効期限も保存
  const expiryTime = Date.now() + data.expires_in * 1000; // 現在時刻 + 有効期限（秒）
  localStorage.setItem("ll_tokenExpires", expiryTime);

  return newAccessToken;
};

const fetchWithAuth = async (url, options = {}) => {
  const defaultHeaders = {};

  let token = localStorage.getItem("ll_accessToken");
  const tokenExpiry = localStorage.getItem("ll_tokenExpires");

  // トークンの有効期限をチェック
  if (token && Date.now() > tokenExpiry) {
    try {
      // 有効期限が切れているのでリフレッシュトークンでトークンを更新
      token = await refreshAccessToken();
      console.log("アクセストークンを更新しました");
    } catch (error) {
      console.error("トークンの更新に失敗しました:", error);
      throw new Error("認証エラー");
    }
  }

  // アクセストークンがある場合、Authorizationヘッダーに設定
  if (token) {
    defaultHeaders.Authorization = `Bearer ${token}`;
  }

  // `Content-Type` が既に設定されていない場合のみ `application/json` を設定
  const finalHeaders = {
    ...defaultHeaders,
    ...(options.headers || {}),
  };

  if (!finalHeaders["Content-Type"] && !(options.body instanceof FormData)) {
    finalHeaders["Content-Type"] = "application/json";
  }

  const finalOptions = {
    ...options,
    headers: finalHeaders,
  };

  try {
    const response = await fetch(url, finalOptions);
    if (!response.ok) {
      throw new Error(`HTTPエラー! status: ${response.status}`);
    }
    return response;
  } catch (error) {
    console.error("フェッチ中のエラー:", error);
    throw error;
  }
};
export default fetchWithAuth;
