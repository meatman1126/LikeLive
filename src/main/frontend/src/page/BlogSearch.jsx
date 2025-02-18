import React, { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";

import Header from "../component/Header";
import config from "../config/properties";
import noImage from "../images/no_image.jpeg";
import fetchWithAuth from "../util/fetchUtil";
import { useLoading } from "../util/LoadingContext";
import { getDate } from "../util/timeUtil";

/**
 *
 * @returns ブログ検索ページ
 */
export default function BlogSearch({ isAuthenticated, setIsAuthenticated }) {
  // 検索キーワードを管理
  const [searchTerm, setSearchTerm] = useState("");
  // 検索結果を管理
  const [searchResults, setSearchResults] = useState([]);
  // ソート条件を管理
  const [sortBy, setSortBy] = useState("newest");
  // エラーメッセージを管理
  const [errorMessage, setErrorMessage] = useState("");
  // 検索結果メッセージを管理
  const [resultMessage, setResultMessage] = useState("");
  // 検索結果メッセージ表示有無
  const [showResultMessage, setShowResultMessage] = useState(false);
  // ページ番号を管理
  const [currentPage, setCurrentPage] = useState(0);
  // 初回の検索実行有無を管理
  const [isSearchInitiated, setIsSearchInitiated] = useState(false);
  // オートスクロール用の監視対象ref
  const loadMoreRef = useRef(null);
  // 検索結果に次のページがあるかどうかのフラグ
  const [hasMore, setHasMore] = useState(true);

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
        `${config.apiBaseUrl}/api/blog/search?keyword=${encodeURIComponent(
          searchTerm
        )}&sort=${sortBy}&page=${currentPage}`,
        {
          method: "GET",
        }
      );
      if (response.ok) {
        const data = await response.json();
        // 検索結果0件の場合メッセージを表示
        if (data.resultCount === 0) {
          setResultMessage("検索結果が0件です。");
          setShowResultMessage(true);
          setSearchResults([]); // 結果が0の場合、リストをクリア
        } else {
          // 検索結果ありの場合
          setSearchResults((prevResults) => {
            // 追加検索の場合取得したデータを重複を避けて追加
            if (isLoadMore) {
              // すでに存在するブログのIDをSetで取得（高速なIDチェックを実現）
              const existingIds = new Set(prevResults.map((blog) => blog.id));

              // 新しいブログをフィルタリングして、まだ存在しないものだけを追加
              const filteredBlogs = data.blogs.reduce((acc, blog) => {
                if (!existingIds.has(blog.id)) {
                  acc.push(blog); // 存在しない場合に追加
                  existingIds.add(blog.id); // 追加したIDをSetに追加
                }
                return acc;
              }, []);

              // フィルタリング後のブログだけを既存の結果に追加して返す
              return [...prevResults, ...filteredBlogs];
            } else {
              // 通常の検索では新しい結果で置き換える
              return data.blogs;
            }
          });
          setResultMessage("");
          setShowResultMessage(false);
          setHasMore(data.hasMore);
        }
      } else {
        console.error("検索に失敗しました");
      }
    } catch (error) {
      console.error("エラーが発生しました:", error);
    }
    stopLoading();
  };

  // オートスクロール（ページ下部に到達したら次のページを取得）
  useEffect(() => {
    // 初回の検索が未実行または検索結果の次ページがない場合はオートスクロール検索を行わない
    if (!isSearchInitiated || !hasMore) return;
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting) {
          setCurrentPage((prevPage) => prevPage + 1); // ページ番号をインクリメント
          fetchBlogs(true); // 追加のブログを取得
        }
      },
      { threshold: 1.0 }
    );

    if (loadMoreRef.current) {
      observer.observe(loadMoreRef.current); // 監視開始
    }

    return () => {
      if (loadMoreRef.current) {
        observer.unobserve(loadMoreRef.current); // 監視解除
      }
    };
  }, [searchResults, isSearchInitiated]);

  // ソート条件変更時の処理
  useEffect(() => {
    // 初回検索未実行の場合はスキップ
    if (!isSearchInitiated) return;
    // 検索実行
    fetchBlogs(false);
  }, [sortBy]);

  // 検索欄でEnter押下時に検索処理を呼び出します。
  const handleKeyDown = (e) => {
    if (e.key === "Enter") {
      if (!searchTerm) {
        setErrorMessage("検索ワードを入力してください");
        return;
      }
      setIsSearchInitiated(true); // 検索が実行されたことを記録
      setCurrentPage(0); // 新しい検索を行う時はページ番号をリセット
      fetchBlogs(false);
    }
  };

  // ソートセレクトボックス操作時の処理
  const handleSort = (e) => {
    // ページ番号をリセット
    setCurrentPage(0);
    setSortBy(e.target.value);
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
              placeholder="キーワードで検索"
              value={searchTerm}
              onChange={handleInputChange}
              onKeyDown={handleKeyDown}
            />
          </div>
        </div>

        {/* ソート機能 */}
        {searchResults.length > 0 && (
          <div className="mb-6 flex justify-end">
            <select
              name="sort"
              value={sortBy}
              onChange={handleSort}
              className="border rounded-lg px-4 py-2"
            >
              <option value="newest">投稿日が新しい順</option>
              <option value="oldest">投稿日が古い順</option>
              <option value="mostLiked">いいねが多い順</option>
            </select>
          </div>
        )}

        {/* 検索結果メッセージが存在する場合表示 */}
        {showResultMessage && <p>{resultMessage}</p>}

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {searchResults.map((result) => (
            <div
              key={result.id}
              className="border rounded-lg overflow-hidden shadow-lg flex flex-col justify-between hover:shadow-xl transition-shadow cursor-pointer"
              style={{ height: "100%" }} // カード全体の高さを指定
              onClick={() => navigate(`/blog/${result.id}`)}
            >
              {/* サムネイル画像 */}
              {result.thumbnailUrl ? (
                <img
                  src={`${config.getImageUrl}/${result.thumbnailUrl}`}
                  alt={`${result.title}のサムネイル画像`}
                  className="w-full h-48 object-cover"
                />
              ) : (
                <img
                  src={noImage} // カスタムプレースホルダー画像
                  alt="サムネイルがありません"
                  className="w-full h-48 object-cover rounded-t-lg"
                />
              )}

              {/* コンテンツ */}
              <div className="p-4 flex flex-col flex-grow">
                {/* タイトルを上部に配置 */}
                <h2 className="text-xl font-bold mb-4 font-noto-sans">
                  {result.title}
                </h2>

                {/* フッター（プロフィール情報と投稿日を下部に配置） */}
                <div className="mt-auto">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center">
                      <img
                        src={`${config.getImageUrl}/${result.author.profileImageUrl}`}
                        alt={`${result.author.displayName}のプロフィール画像`}
                        className="w-10 h-10 rounded-full object-cover mr-3"
                      />
                      <p className="font-medium font-noto-sans">
                        {result.author.displayName}
                      </p>
                    </div>
                    <p className="text-gray-500 text-sm font-noto-sans text-right">
                      投稿日:{getDate(result.blogCreatedTime)}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
        {/* オートスクロール用の監視ターゲット */}
        <div ref={loadMoreRef} className="h-6"></div>
      </div>
    </div>
  );
}
