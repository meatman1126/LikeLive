import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

import Header from "../component/Header";
import config from "../config/properties";
import fetchWithAuth from "../util/fetchUtil";
import { useLoading } from "../util/LoadingContext";

/**
 *
 * @returns ブログ検索ページ
 */
export default function UserSearch({ isAuthenticated, setIsAuthenticated }) {
  // 検索キーワードを管理
  const [searchTerm, setSearchTerm] = useState("");
  // 検索結果を管理
  const [searchResults, setSearchResults] = useState([]);
  // エラーメッセージを管理
  const [errorMessage, setErrorMessage] = useState("");
  // 検索結果メッセージを管理
  const [resultMessage, setResultMessage] = useState("");
  // 検索結果メッセージ表示有無
  const [showResultMessage, setShowResultMessage] = useState(false);

  const navigate = useNavigate();
  // ローディング制御メソッドを取得
  const { startLoading, stopLoading } = useLoading();

  /**
   * ブログ検索API呼び出し処理
   * @param {*} isLoadMore 追加検索（同一検索条件で結果を追加）の場合true
   */
  const fetchBlogs = async (isLoadMore = false) => {
    try {
      startLoading();
      // API呼び出し
      const response = await fetchWithAuth(
        `${config.apiBaseUrl}/api/user/search?keyword=${encodeURIComponent(
          searchTerm
        )}`,
        {
          method: "GET",
        }
      );
      if (response.ok) {
        const data = await response.json();
        // 検索結果0件の場合メッセージを表示
        if (data.length === 0) {
          setResultMessage("検索結果が0件です。");
          setShowResultMessage(true);
          setSearchResults([]); // 結果が0の場合、リストをクリア
        } else {
          // 検索結果ありの場合
          setSearchResults((prevResults) => {
            // 追加検索の場合取得したデータを重複を避けて追加
            if (isLoadMore) {
              // すでに存在するブログのIDをSetで取得（高速なIDチェックを実現）
              const existingIds = new Set(prevResults.map((user) => user.id));

              // 新しいブログをフィルタリングして、まだ存在しないものだけを追加
              const filteredUsers = data.reduce((acc, user) => {
                if (!existingIds.has(user.id)) {
                  acc.push(user); // 存在しない場合に追加
                  existingIds.add(user.id); // 追加したIDをSetに追加
                }
                return acc;
              }, []);

              // フィルタリング後のブログだけを既存の結果に追加して返す
              return [...prevResults, ...filteredUsers];
            } else {
              // 通常の検索では新しい結果で置き換える
              return data;
            }
          });
          setResultMessage("");
          setShowResultMessage(false);
        }
      } else {
        console.error("検索に失敗しました");
      }
    } catch (error) {
      console.error("エラーが発生しました:", error);
    }
    stopLoading();
  };

  // 検索欄でEnter押下時に検索処理を呼び出します。
  const handleKeyDown = (e) => {
    if (e.key === "Enter") {
      if (!searchTerm) {
        setErrorMessage("検索ワードを入力してください");
        return;
      }
      fetchBlogs(false);
    }
  };

  // キーワード入力時の処理
  const handleInputChange = (e) => {
    setSearchTerm(e.target.value);
    if (e.target.value.trim() !== "") {
      setErrorMessage(""); // ユーザーが入力したらエラーメッセージを非表示に
    }
  };

  return (
    <div>
      <Header
        isAuthenticated={isAuthenticated}
        setIsAuthenticated={setIsAuthenticated}
      />
      <div className="container mx-auto px-4 py-8">
        <div className="relative mb-8 w-10/12 mx-auto">
          {/* エラーメッセージの表示 */}
          <div className="h-6">
            {/* 固定高さを指定して検索欄の位置を固定 */}
            {errorMessage && (
              <p className="text-red-500 font-noto-sans">{errorMessage}</p>
            )}
          </div>
          <div className="relative">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center">
              <i className="fas fa-search text-gray-400"></i>
            </div>
            <input
              type="text"
              name="search"
              className={`w-full pl-10 pr-4 py-2 border rounded-lg font-noto-sans ${
                errorMessage ? "border-red-500" : ""
              }`} // エラーメッセージがある場合赤枠を適用
              placeholder="アーティスト名、ユーザ名で検索"
              value={searchTerm}
              onChange={handleInputChange}
              onKeyDown={handleKeyDown}
            />
          </div>
        </div>
        {/* 検索結果メッセージが存在する場合表示 */}
        {showResultMessage && <p>{resultMessage}</p>}
        {/* 検索結果の表示 */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 cursor-pointer">
          {searchResults.map((user) => (
            <div
              key={user.id}
              className="bg-white shadow-lg rounded-lg p-4 flex items-center"
              onClick={() => navigate(`/user/${user.id}`)}
            >
              {/* プロフィール画像 */}
              <div className="w-16 h-16 rounded-full overflow-hidden flex-shrink-0">
                {user.profileImageUrl ? (
                  <img
                    src={`${config.apiBaseUrl}/api/public/files/${user.profileImageUrl}`}
                    alt={`${user.displayName}のプロフィール画像`}
                    className="w-full h-full object-cover"
                  />
                ) : (
                  <div className="w-full h-full bg-gray-200 flex items-center justify-center">
                    <i className="fas fa-user text-gray-400 text-3xl"></i>
                  </div>
                )}
              </div>
              {/* ユーザ情報 */}
              <div className="ml-4">
                {/* ユーザ名 */}
                <p className="text-lg font-semibold text-gray-700">
                  {user.displayName}
                </p>
                {/* 自己紹介 */}
                <p className="text-sm text-gray-500 line-clamp-2">
                  {user.selfIntroduction || ""}
                </p>
              </div>
            </div>
          ))}
        </div>{" "}
      </div>
    </div>
  );
}
