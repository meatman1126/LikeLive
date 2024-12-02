import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import Footer from "../component/Fotter";
import Header from "../component/Header";
import UserRegistrationModal from "../component/UserRegistrationModal";
import config from "../config/properties";
import noImage from "../images/no_image.jpeg";
import fetchWithAuth from "../util/fetchUtil";
import { useLoading } from "../util/LoadingContext";

function Dashboard({ isAuthenticated, setIsAuthenticated }) {
  const location = useLocation();
  const [userInfo, setUserInfo] = useState(location.state?.userInfo || null);
  // 初回ログインの場合true
  const [isFirstLogin, setIsFirstLogin] = useState(
    location.state?.isFirstLogin || false
  );
  // ローディング制御メソッドを取得
  const { startLoading, stopLoading } = useLoading();

  // おすすめブログ情報を管理
  const [posts, setPosts] = useState([]);
  const [visiblePosts, setVisiblePosts] = useState(4);

  // おすすめユーザ情報を保持
  const [recomendedUsers, setRecomendedUsers] = useState([]);

  // 下書きブログ情報を管理
  const [drafts, setDrafts] = useState([]);

  const navigate = useNavigate();

  // APIからpostsを取得する処理
  useEffect(() => {
    // APIを呼び出してデータを取得
    // おすすめブログ情報を取得
    const fetchPosts = async () => {
      startLoading();

      try {
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

    const fetchUsers = async () => {
      startLoading();
      try {
        // API呼び出し
        const response = await fetchWithAuth(
          `${config.apiBaseUrl}/api/user/recommended-users`,
          {
            method: "GET",
          }
        );

        // レスポンスのステータス確認
        if (response.ok) {
          const data = await response.json();
          // dataの構造を整形する
          const formattedUsers = data.map((user) => ({
            id: user.user.id,
            displayName: user.user.displayName,
            profileImageUrl: user.user.profileImageUrl,
            selfIntroduction: user.user.selfIntroduction,
            isFollow: user.isFollow,
          }));
          setRecomendedUsers(formattedUsers); // APIから取得したユーザリストをセット
        } else {
          console.error("おすすめユーザの取得に失敗しました");
        }
      } catch (error) {
        console.error("エラーが発生しました:", error);
      }
      stopLoading();
    };

    const fetchDrafts = async () => {
      try {
        const response = await fetchWithAuth(
          `${config.apiBaseUrl}/api/blog/my-drafts`,
          {
            method: "GET",
          }
        );

        // レスポンスのステータス確認
        if (response.ok) {
          const data = await response.json();
          setDrafts(data); // APIの結果をdraftsにセット
        } else {
          console.error("下書きの取得に失敗しました");
        }
      } catch (error) {
        console.error("エラーが発生しました:", error);
      }
    };

    fetchPosts();
    fetchUsers();
    fetchDrafts();
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
          <div>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-6 mb-8">
              {posts.slice(0, visiblePosts).map((post) => (
                <div
                  key={post.id}
                  className="post-card bg-white rounded-lg shadow-lg p-6 hover:shadow-xl transition-shadow cursor-pointer"
                  onClick={() => navigate(`/blog/${post.id}`)}
                >
                  {/* サムネイル画像 */}
                  {post.thumbnailUrl ? (
                    <img
                      src={`${config.apiBaseUrl}/api/public/files/${post.thumbnailUrl}`}
                      alt={`${post.blogTitle}のサムネイル`}
                      className="w-full h-36 object-cover rounded-t-lg mb-4" // サムネイル画像のスタイル
                    />
                  ) : (
                    <img
                      src={noImage} // カスタムプレースホルダー画像
                      alt="サムネイルがありません"
                      className="w-full h-36 object-cover rounded-t-lg mb-4"
                    />
                  )}

                  {/* ブログタイトル */}
                  <h3 className="text-2xl font-bold text-gray-900 mb-4">
                    {post.blogTitle}
                  </h3>

                  {/* 著者情報 */}
                  <div className="flex items-center mb-4">
                    {post.profileImageUrl ? (
                      <img
                        src={`${config.apiBaseUrl}/api/public/files/${post.profileImageUrl}`}
                        alt={`${post.profileImageUrl}のアイコン`}
                        className="w-10 h-10 rounded-full border-2 border-gray-300 mr-4"
                      />
                    ) : (
                      <i className="rouded-full fas fa-user fa-2x text-blue-300 mr-4"></i>
                    )}
                    <div className="text-base flex justify-between w-full">
                      <p className="font-semibold text-gray-700">
                        {post.authorName}
                      </p>
                      <span className="text-xs text-gray-500">
                        {post.isFollowAuthor ? "フォロー中" : ""}
                      </span>
                    </div>
                  </div>

                  {/* 投稿日 */}
                  <p className="text-gray-500 text-sm text-right">
                    投稿日: {post.blogCreatedTime}
                  </p>
                </div>
              ))}
            </div>

            {/* もっと見るボタン */}
            {visiblePosts < posts.length && (
              <div className="flex justify-center mb-6">
                <button
                  onClick={showMorePosts}
                  className="mt-4 bg-blue-500 text-white py-2 px-6 rounded-lg hover:bg-blue-600 transition"
                >
                  もっと見る
                </button>
              </div>
            )}
          </div>
          <div className="flex justify-between items-center mt-12 mb-6">
            <h2 className="text-2xl font-semibold text-gray-800">
              おすすめのユーザ
            </h2>
          </div>
          {/* ユーザカードリスト（グリッドレイアウト） */}
          <div className="mb-12">
            {recomendedUsers.length === 0 ? (
              <div className="text-center my-8">
                <p className="text-left sm:text-center text-gray-700 text-lg">
                  共通のアーティストが好きなユーザはまだいないみたいです。。
                </p>
                <p className="text-left sm:text-center text-gray-700 text-lg mb-6">
                  興味のあるアーティストを追加してみましょう
                </p>
                <button
                  className="bg-blue-500 text-white py-2 px-6 rounded-lg hover:bg-blue-600 transition"
                  onClick={() => navigate("/user/profile")}
                >
                  プロフィール設定
                </button>
              </div>
            ) : (
              <div>
                <p className="text-left text-gray-700 text-lg mb-6">
                  共通のアーティストが好きなユーザ
                </p>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
                  {recomendedUsers.map((user) => (
                    <div
                      key={user.id}
                      className="user-card bg-white rounded-lg shadow-lg p-6 hover:shadow-xl transition-shadow cursor-pointer"
                      onClick={() => navigate(`/user/${user.id}`)}
                    >
                      <div className="flex items-center mb-4">
                        {user.profileImageUrl ? (
                          <img
                            src={`${config.apiBaseUrl}/api/public/files/${user.profileImageUrl}`}
                            alt={`${user.profileImageUrl}のアイコン`}
                            className="w-10 h-10 rounded-full border-2 border-gray-300 mr-4"
                          />
                        ) : (
                          <i className="rouded-full fas fa-user fa-2x text-blue-300 mr-4"></i>
                        )}
                        <div className="text-base flex justify-between w-full">
                          <p className="font-semibold text-gray-700">
                            {user.displayName}
                          </p>
                          <span className="text-xs text-gray-500">
                            {user.isFollow ? "フォロー中" : ""}
                          </span>
                        </div>
                      </div>
                      <p className="text-gray-500 text-sm">
                        {user.selfIntroduction}
                      </p>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>

          {/* 下書きブログセクション */}
          {drafts.length > 0 && (
            <>
              <div className="flex justify-between items-center mb-6">
                <h2 className="text-2xl font-semibold text-gray-800">
                  作成途中の記事
                </h2>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-6 mb-8">
                {drafts.map((draft) => (
                  <div
                    key={draft.id}
                    className="post-card bg-white rounded-lg shadow-lg p-6 hover:shadow-xl transition-shadow cursor-pointer"
                    onClick={() => navigate(`/blog/edit/${draft.id}`)}
                  >
                    <h3 className="text-2xl font-bold text-gray-900 ">
                      {draft.title}
                    </h3>
                  </div>
                ))}
              </div>
            </>
          )}
        </main>
      </div>

      <Footer />
    </div>
  );
}

export default Dashboard;
