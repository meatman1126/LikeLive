import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import config from "../config/properties";
import { blogCategories } from "../constants/enum";
import { formatDateTime } from "../util/dateTimeUtil";
import { getDisplayNameByDescription } from "../util/enumUtil";
import fetchWithAuth from "../util/fetchUtil";
import { useLoading } from "../util/LoadingContext";
import CommentList from "./CommentList";
import TiptapViewer from "./TiptapViewer";

/**
 *
 * @param {*} targetBlogId 閲覧対象のブログID
 */
export default function BlogViewer({ targetBlogId, showComments }) {
  // ローディング制御メソッドを取得
  const { startLoading, stopLoading } = useLoading();
  // navigateフック
  const navigate = useNavigate();

  // コンテンツを保持するstate
  const [blogInfo, setBlogInfo] = useState(null);

  // 閲覧対象のブログが自身のブログである場合true
  const [isMyBlog, setIsMyBlog] = useState(false);

  // ブログに関連するアーティストリスト
  const [artistList, setArtistList] = useState(null);

  // エラーメッセージ用のstate
  const [error, setError] = useState(null);

  // セットリストの表示状態を管理
  const [showSetlist, setShowSetlist] = useState(true);

  // ブログへのいいね状態を管理
  const [isLikeBlog, setIsLikeBlog] = useState(false);

  // 初期表示するコメントを管理
  const [initialComments, setInitialComments] = useState([]);

  // ブログデータ取得処理
  const fetchBlogContent = async (blogId) => {
    startLoading();
    try {
      const response = await fetchWithAuth(
        `${config.apiBaseUrl}/api/blog/${blogId}`
      );
      if (!response.ok) {
        throw new Error("データの取得に失敗しました");
      }
      const data = await response.json();
      setBlogInfo(data.blog);
      setIsMyBlog(data.blog.author.id == localStorage.getItem("ll_userId"));
      setIsLikeBlog(data.isLike);
      setArtistList(data.artistList);
    } catch (err) {
      // エラーが発生した場合はエラーメッセージをセット
      setError("対象のブログデータが見つかりませんでした。");
    } finally {
      stopLoading(); // ローディング停止
    }
  };

  // コメントデータ取得処理
  const fetchInitialComments = async (blogId) => {
    startLoading();
    try {
      const response = await fetchWithAuth(
        `${config.apiBaseUrl}/api/comment/blog/${blogId}`
      );
      if (!response.ok) {
        throw new Error("データの取得に失敗しました");
      }
      const data = await response.json();
      const processedComments = data.map((item) => {
        return {
          ...item.comment, // commentオブジェクトの既存フィールド
          replyCount: item.replyCount, // replyCountを追加
        };
      });
      setInitialComments(processedComments);
    } catch (err) {
      // エラーが発生した場合はエラーメッセージをセット
      setError("対象のコメントデータが見つかりませんでした。");
    } finally {
      stopLoading(); // ローディング停止
    }
  };

  // 閲覧対象のブログ変更をトリガーにデータ取得処置を呼び出す
  useEffect(() => {
    if (targetBlogId) {
      fetchBlogContent(targetBlogId);
      fetchInitialComments(targetBlogId);
    }
  }, [targetBlogId]);

  // いいね解除処理
  const clearLikeBlog = async (blogId) => {
    try {
      const response = await fetchWithAuth(
        `${config.apiBaseUrl}/api/blog/unlike/${blogId}`,
        {
          method: "POST",
        }
      );

      if (response.ok) {
        const updatedLikeCount = await response.json();
        setBlogInfo((prevBlogInfo) => ({
          ...prevBlogInfo,
          likeCount: updatedLikeCount, // いいね数を更新
        }));
      } else {
        console.error("いいねの解除に失敗しました");
      }
    } catch (error) {
      console.error("API呼び出しに失敗しました:", error);
    } finally {
      // 成否にかかわらずいいね状態をfalseにする
      setIsLikeBlog(false);
    }
  };
  // いいね追加処理
  const likeBlog = async (blogId) => {
    try {
      const response = await fetchWithAuth(
        `${config.apiBaseUrl}/api/blog/like/${blogId}`,
        {
          method: "POST",
        }
      );

      if (response.ok) {
        const updatedLikeCount = await response.json();
        setBlogInfo((prevBlogInfo) => ({
          ...prevBlogInfo,
          likeCount: updatedLikeCount, // いいね数を更新
        }));
      } else {
        console.error("いいねの追加に失敗しました");
      }
    } catch (error) {
      console.error("API呼び出しに失敗しました:", error);
    } finally {
      // 成否に関わらずいいね状態をtrueにする
      setIsLikeBlog(true);
    }
  };

  // カテゴリに応じた背景色を返す
  const getCategoryBackgroundClass = (categoryDescription) => {
    switch (categoryDescription) {
      case "DIARY":
        return "bg-blue-300";
      case "REPORT":
        return "bg-green-300";
      case "OTHER":
        return "bg-gray-300";
      default:
        return "";
    }
  };

  // ブログ取得処理に失敗した場合エラーモーダルを表示しダッシュボード画面へのボタンを表示
  if (error) {
    return (
      <div className="flex flex-col items-center justify-center h-screen">
        <p className="text-red-500 mb-4">{error}</p>
        <button
          className="p-4 w-18 bg-gray-300 rounded-lg"
          onClick={() => navigate("/dashboard")}
        >
          ホームへ戻る
        </button>
      </div>
    );
  }
  if (!blogInfo) {
    return <div>読み込み中</div>;
  }

  return (
    <div>
      {blogInfo && (
        <>
          {blogInfo.status === "DRAFT" ? (
            <p className="text-gray-500 -ml-6 -mt-6 underline">
              ※本記事は下書きです。
            </p>
          ) : blogInfo.status === "ARCHIVED" ? (
            <p className="text-gray-500 -ml-6 -mt-6 underline">
              ※本記事は現在非公開です。
            </p>
          ) : (
            <></>
          )}
          {/* タイトルとauthor情報*/}
          <div className="mb-4">
            {/* カテゴリ表示 */}
            <div className="mt-2 flex justify-between items-center">
              <button
                className={`text-white py-1 px-2 text-sm font-bold cursor-auto rounded ${getCategoryBackgroundClass(
                  blogInfo.category
                )}`}
                type="button"
              >
                {getDisplayNameByDescription(blogCategories, blogInfo.category)}
              </button>

              {/* isMyBlogがtrueの場合のみ編集ボタンを表示 */}
              {isMyBlog && (
                <button
                  className="bg-gray-500 text-white px-4 py-2 rounded-xl hover:bg-blue-600"
                  onClick={() => navigate(`/blog/edit/${blogInfo.id}`)}
                  type="button"
                >
                  <i className="fa-solid fa-pen-to-square"></i>
                </button>
              )}
            </div>
            {/* サムネイル画像 */}
            <div className="relative w-10/12 mx-auto py-8">
              {blogInfo.thumbnailUrl && (
                <>
                  <img
                    src={`${config.apiBaseUrl}/api/public/files/${blogInfo.thumbnailUrl}`}
                    alt="選択されたプロフィール画像"
                    className="w-full object-contain"
                  />
                </>
              )}
            </div>
            {/* タイトル */}
            <div className="w-full">
              <p className="w-full py-2 px-3 text-gray-700 text-4xl text-center font-bold">
                {blogInfo.title}
              </p>
            </div>

            {/* 著者情報、ブログ公開日時、いいねカウント */}
            <div className="w-full flex flex-col sm:flex-row sm:items-center sm:justify-center mt-4 pl-4 sm:pl-0">
              {/* 著者情報 */}
              <div
                className="flex items-center mb-2 sm:mb-0 sm:mr-8 text-left cursor-pointer"
                onClick={() => navigate(`/user/${blogInfo.author.id}`)}
              >
                <img
                  src={`${config.apiBaseUrl}/api/public/files/${blogInfo.author.profileImageUrl}`}
                  alt="プロフィール画像"
                  className="w-10 h-10 rounded-full mr-2"
                />
                <p className="text-gray-700">{blogInfo.author.displayName}</p>
              </div>

              {/* ブログ公開日時 */}
              <div className="mb-2 sm:mb-0 sm:mr-8 text-left text-sm">
                <p className="text-gray-500">
                  公開日時：{formatDateTime(blogInfo.blogCreatedTime)}
                </p>
              </div>

              {/* いいねカウント */}
              <div
                className="flex items-center text-left cursor-pointer"
                onClick={() => {
                  if (isLikeBlog) {
                    // いいねを解除する
                    clearLikeBlog(blogInfo.id);
                  } else {
                    // いいねを追加する
                    likeBlog(blogInfo.id);
                  }
                }}
              >
                <i
                  className={`fas fa-heart mr-1 ${
                    isLikeBlog ? "text-red-500" : "text-gray-300"
                  }`}
                ></i>
                <p>{blogInfo.likeCount}</p>
              </div>
            </div>
          </div>
          {/* 関連アーティスト情報の表示 */}
          {artistList && artistList.length > 0 && (
            <div className="my-8">
              <h3 className="text-xl font-bold text-gray-700 mb-4">
                本ブログに関連するアーティスト
              </h3>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                {artistList.map((artist) => (
                  <div
                    key={artist.id}
                    className="flex items-center justify-between bg-gray-100 p-2 rounded-lg shadow-md"
                  >
                    <div className="flex items-center">
                      <img
                        src={artist.imageUrl}
                        alt={artist.name}
                        className="w-12 h-12 rounded-full mr-4"
                      />
                      <p className="text-gray-700">{artist.name}</p>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
          {/* TiptapViewerコンポーネントに取得したcontentを渡す */}
          <TiptapViewer content={blogInfo.content} />
          {/* セットリスト情報 */}
          {(blogInfo.setlist.mainSetList.length > 0 ||
            blogInfo.setlist.encoreSections.length > 0) && (
            <div className="mt-12">
              <div className="flex items-center mb-4">
                <h3 className="text-xl font-bold text-gray-700 mr-2">
                  セットリスト
                </h3>
                <button
                  className="bg-gray-300 w-10 h-8 text-white py-1 px-2 m-2 rounded"
                  onClick={() => setShowSetlist(!showSetlist)}
                >
                  {showSetlist ? "-" : "+"}
                </button>
              </div>
              {showSetlist && (
                <>
                  {/* 本編セットリストの表示 */}
                  {blogInfo.setlist.mainSetList.length > 0 && (
                    <div className="mb-4">
                      <table className="table-fixed w-full mb-4 border-collapse">
                        <thead>
                          <tr>
                            <th className="border px-4 py-2 w-2/12">No.</th>
                            <th className="border px-4 py-2 w-10/12">曲名</th>
                          </tr>
                        </thead>
                        <tbody>
                          {blogInfo.setlist.mainSetList.map((track, index) => (
                            <tr key={index}>
                              <td className="border px-4 py-2 text-center">
                                {track.trackNumber}
                              </td>
                              <td className="border px-4 py-2">
                                {track.trackName || "未入力"}
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  )}

                  {/* アンコールセットリストの表示 */}
                  {blogInfo.setlist.encoreSections.length > 0 && (
                    <div>
                      {blogInfo.setlist.encoreSections.map(
                        (encoreSection, encoreIndex) => (
                          <div key={encoreIndex} className="mb-4">
                            <h3 className="text-xl font-bold text-gray-700">
                              en {encoreIndex + 1}
                            </h3>
                            <table className="table-fixed w-full mb-4 border-collapse">
                              <thead>
                                <tr>
                                  <th className="border px-4 py-2 w-2/12">
                                    No.
                                  </th>
                                  <th className="border px-4 py-2 w-10/12">
                                    曲名
                                  </th>
                                </tr>
                              </thead>
                              <tbody>
                                {encoreSection.map((track, trackIndex) => (
                                  <tr key={trackIndex}>
                                    <td className="border px-4 py-2 text-center">
                                      {track.trackNumber}
                                    </td>
                                    <td className="border px-4 py-2">
                                      {track.trackName || "未入力"}
                                    </td>
                                  </tr>
                                ))}
                              </tbody>
                            </table>
                          </div>
                        )
                      )}
                    </div>
                  )}
                </>
              )}
            </div>
          )}{" "}
          {blogInfo.status === "PUBLISHED" && (
            <div id="comment-section">
              {initialComments ? (
                <CommentList
                  initialComments={initialComments}
                  targetBlogId={targetBlogId}
                  showComments={showComments}
                />
              ) : (
                <p>Loading comments...</p>
              )}
            </div>
          )}
        </>
      )}
    </div>
  );
}
