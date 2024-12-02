import axios from "axios";
import config from "./properties";

// Axiosインスタンスの作成
const axiosInstance = axios.create({
  // サーバのベースURLを指定、環境によって指定
  baseURL: config.apiBaseUrl,
});

// リクエスト時に共通で処理したい内容（アクセストークンの付加）
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("ll_accessToken"); // ローカルストレージからトークンを取得
    if (token) {
      config.headers.Authorization = `Bearer ${token}`; // ヘッダーにトークンを含める
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default axiosInstance;
