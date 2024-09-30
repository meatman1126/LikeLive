import React, { useState } from "react";
import { useLocation } from "react-router-dom";
import Footer from "../component/Fotter";
import Header from "../component/Header";
import UserRegistrationModal from "../component/UserRegistrationModal";
import user_icon from "../images/user_icon.png";

function Dashboard({ isAuthenticated, setIsAuthenticated }) {
  const location = useLocation();
  const [isFirstLogin, setIsFirstLogin] = useState(
    location.state?.isFirstLogin || false
  );
  console.log(isFirstLogin);

  const [posts, setPosts] = useState([
    {
      id: 1,
      title: "ブログ投稿1",
      user: { name: "ユーザーA", icon: user_icon, isFollowed: true },
      date: "2024-09-28",
    },
    {
      id: 2,
      title: "ブログ投稿2",
      user: { name: "ユーザーB", icon: user_icon, isFollowed: false },
      date: "2024-09-27",
    },
    {
      id: 3,
      title: "ブログ投稿3",
      user: { name: "ユーザーC", icon: user_icon, isFollowed: true },
      date: "2024-09-26",
    },
    {
      id: 4,
      title: "ブログ投稿4",
      user: { name: "ユーザーD", icon: user_icon, isFollowed: false },
      date: "2024-09-25",
    },
    {
      id: 5,
      title: "ブログ投稿5",
      user: { name: "ユーザーE", icon: user_icon, isFollowed: false },
      date: "2024-10-01",
    },
  ]);
  const [visiblePosts, setVisiblePosts] = useState(4);

  const showMorePosts = () => {
    setVisiblePosts(Math.min(visiblePosts + 7, posts.length));
  };

  return (
    <div className="dashboard-container">
      <Header
        isAuthenticated={isAuthenticated}
        setIsAuthenticated={setIsAuthenticated}
      />
      {/* モーダルの表示 */}
      <UserRegistrationModal
        isOpen={isFirstLogin} // 初回ログイン時のみ表示
        onClose={() => setIsFirstLogin(false)}
        initialUsername="初期ユーザ名"
      />
      <div className="p-6 bg-gray-50 min-h-screen">
        {/* ダッシュボードタイトル */}
        <div className="mb-6">
          <h1 className="text-4xl font-bold text-gray-800 font-sans">
            ダッシュボード
          </h1>
        </div>

        {/* メインコンテンツ */}
        <main className="mx-auto max-w-4xl">
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
                <h3 className="text-xl font-bold text-gray-900 mb-4">
                  {post.title}
                </h3>
                <div className="flex items-center mb-4">
                  <img
                    src={post.user.icon}
                    alt={`${post.user.name}のアイコン`}
                    className="w-10 h-10 rounded-full border-2 border-gray-300 mr-4"
                  />
                  <div className="text-sm">
                    <p className="font-semibold text-gray-700">
                      {post.user.name}
                      <span className="ml-4 text-xs text-gray-500">
                        {post.user.isFollowed ? "フォロー中" : ""}
                      </span>
                    </p>
                  </div>
                </div>
                <p className="text-gray-500 text-sm">投稿日: {post.date}</p>
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
