import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import Footer from "../component/Fotter";
import Header from "../component/Header";
import UserRegistrationModal from "../component/UserRegistrationModal";
import config from "../config/properties";
import fetchWithAuth from "../util/fetchUtil";
import { useLoading } from "../util/LoadingContext";

function Dashboard({ isAuthenticated, setIsAuthenticated }) {
  const location = useLocation();
  const [userInfo, setUserInfo] = useState(location.state?.userInfo || null); // location.stateから値を受け取る
  const [isFirstLogin, setIsFirstLogin] = useState(
    location.state?.isFirstLogin || false //TODO　falseが正しいよ
  );
  // ローディング制御メソッドを取得
  const { startLoading, stopLoading } = useLoading();

  const [posts, setPosts] = useState([]);
  const [visiblePosts, setVisiblePosts] = useState(4);

  // APIからpostsを取得する処理
  useEffect(() => {
    // APIを呼び出してデータを取得
    const fetchPosts = async () => {
      try {
        startLoading();
        const response = await fetchWithAuth(
          `${config.apiBaseUrl}/api/blog/interest`,
          {
            method: "GET",
          }
        );
        if (response.ok) {
          const data = await response.json();
          setPosts(data); // 取得したデータをpostsにセット
        } else {
          console.error("Failed to fetch posts");
        }
      } catch (error) {
        console.error("Error fetching posts:", error);
      }
      stopLoading();
    };
    fetchPosts();
  }, []);

  const showMorePosts = () => {
    setVisiblePosts(Math.min(visiblePosts + 7, posts.length));
  };

  return (
    <div className="dashboard-container">
      <Header
        isAuthenticated={isAuthenticated}
        setIsAuthenticated={setIsAuthenticated}
        userInfo={userInfo ? userInfo : undefined} // userInfoがnullの場合は渡さない
      />
      {isFirstLogin && (
        // 初回ログイン時のみユーザ情報登録モーダルを表示する
        <UserRegistrationModal
          isOpen={isFirstLogin} // 初回ログイン時のみ表示
          onClose={() => setIsFirstLogin(false)}
          initialUsername={userInfo.displayName}
          setUserInfo={setUserInfo}
        />
      )}
      <div className="p-6 bg-gray-50 min-h-screen">
        {/* ダッシュボードタイトル */}
        <div className="mb-6">
          <h1 className="text-4xl font-bold text-gray-800 font-sans">
            ダッシュボード
          </h1>
        </div>

        {/* メインコンテンツ */}
        <main className="mx-auto max-w-5xl">
          <div className="flex justify-between items-center mb-8">
            <h2 className="text-2xl font-semibold text-gray-800">
              おすすめの新着投稿
            </h2>
          </div>

          {/* 投稿カードリスト（グリッドレイアウト） */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
            {posts.slice(0, visiblePosts).map((post) => (
              <div
                key={post.id}
                className="post-card bg-white rounded-lg shadow-lg p-6 hover:shadow-xl transition-shadow cursor-pointer"
                onClick={() => (window.location.href = `/blog/${post.id}`)}
              >
                <h3 className="text-2xl font-bold text-gray-900 mb-4">
                  {post.blogTitle}
                </h3>
                <div className="flex items-center mb-4">
                  <img
                    src={`${config.apiBaseUrl}/api/public/files/${post.profileImageUrl}`}
                    alt={`${post.profileImageUrl}のアイコン`}
                    className="w-10 h-10 rounded-full border-2 border-gray-300 mr-4"
                  />
                  <div className="text-base flex justify-between w-full">
                    <p className="font-semibold text-gray-700">
                      {post.authorName}
                    </p>
                    <span className="text-xs text-gray-500">
                      {post.isFollowAuthor ? "フォロー中" : ""}
                    </span>
                  </div>
                </div>
                <p className="text-gray-500 text-sm text-right">
                  投稿日: {post.blogCreatedTime}
                </p>
              </div>
            ))}
          </div>

          {/* もっと見るボタン */}
          {visiblePosts < posts.length && (
            <div className="flex justify-center mt-8">
              <button
                onClick={showMorePosts}
                className="mt-4 bg-blue-500 text-white py-2 px-6 rounded-lg hover:bg-blue-600 transition"
              >
                もっと見る
              </button>
            </div>
          )}
        </main>
      </div>

      <Footer />
    </div>
  );
}

export default Dashboard;
