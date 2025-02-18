"use client";
import React, { useEffect, useState } from "react";
import config from "../config/properties";
import fetchWithAuth from "../util/fetchUtil";

/**
 *
 * @param {*} initialComments 初期表示時のコメント情報
 * @returns コメントリストコンポーネント
 */
export default function CommentList({
  isAuthenticated,
  initialComments,
  targetBlogId,
  showComments = false,
}) {
  // コメントのリストを管理するState
  const [comments, setComments] = useState(initialComments);
  // 返信欄の表示状態のコメントIDを管理するState
  const [expandedCommentIds, setExpandedCommentIds] = useState(new Set());
  // 新規追加されるコメントの内容を管理するState
  const [newComment, setNewComment] = useState("");
  // 新規追加される返信コメントの内容を管理するState（親コメントIDと返信テキストのマッピング）
  const [replyTexts, setReplyTexts] = useState({});
  // 各コメントに対する返信入力欄の表示有無を管理するState
  const [showReplyInput, setShowReplyInput] = useState({});
  // コメントリストの表示有無を管理
  const [showCommentList, setShowCommentList] = useState(showComments);

  // initialCommentsが更新されたらcommentsも更新する
  useEffect(() => {
    setComments(initialComments);
  }, [initialComments]);

  const toggleReplies = async (commentId) => {
    // すでに返信が設定されているかを確認
    const comment = comments.find((c) => c.id === commentId);

    // 返信が設定済みの場合は既存処理を実行
    if (comment.replies && comment.replies.length > 0) {
      setExpandedCommentIds((prev) => {
        const newSet = new Set(prev);
        if (newSet.has(commentId)) {
          newSet.delete(commentId); // 表示されている場合は非表示に
        } else {
          newSet.add(commentId); // 非表示の場合は表示に
        }
        return newSet;
      });
      return;
    }

    // 返信が未設定の場合、APIから返信を取得
    try {
      // 認証有無でAPI呼び出し先を切り替える
      const response = isAuthenticated
        ? await fetchWithAuth(
            `${config.apiBaseUrl}/api/comment/parent/${commentId}`
          )
        : await fetch(
            `${config.apiBaseUrl}/api/public/comment/parent/${commentId}`,
            {
              method: "GET",
              headers: {
                "Content-Type": "application/json",
              },
            }
          );

      if (!response.ok) {
        throw new Error("Failed to fetch replies");
      }
      const replies = await response.json();

      // 該当コメントに取得した返信をセット
      const updatedComments = comments.map((c) => {
        if (c.id === commentId) {
          return {
            ...c,
            replies: replies, // 取得した返信コメントを設定
          };
        }
        return c;
      });

      // commentsInfoを更新
      setComments(updatedComments);

      // 返信表示の切り替えを実行
      setExpandedCommentIds((prev) => {
        const newSet = new Set(prev);
        if (newSet.has(commentId)) {
          newSet.delete(commentId);
        } else {
          newSet.add(commentId);
        }
        return newSet;
      });
    } catch (error) {
      console.error("返信の取得に失敗しました:", error);
    }
  };

  // 返信入力欄の表示/非表示を切り替える関数
  const toggleReplyInput = (commentId) => {
    setShowReplyInput((prev) => ({
      ...prev,
      [commentId]: !prev[commentId], // 現在の表示状態を反転
    }));
  };

  // 新しいコメントを追加する関数
  const handleNewComment = async () => {
    if (!newComment.trim()) return; // コメントが空の場合は何もしない

    try {
      // API呼び出しのためのリクエストボディ
      const requestBody = {
        content: newComment,
        blogId: targetBlogId,
        parentCommentId: "", // 親コメントとして登録するため空文字
      };

      // API呼び出し
      const response = await fetchWithAuth(
        `${config.apiBaseUrl}/api/comment/create`,
        {
          method: "POST",
          body: JSON.stringify(requestBody),
        }
      );

      if (!response.ok) {
        throw new Error("コメントの登録に失敗しました");
      }

      // レスポンスのデータを取得
      const newCommentData = await response.json();

      // 新しいコメントを現在のコメントリストに追加
      setComments((prev) => [newCommentData, ...prev]);
      setNewComment(""); // コメント入力欄をクリア
      setShowCommentList(true);
    } catch (error) {
      console.error("エラー:", error);
    }
  };

  // 返信コメントを追加する関数
  const handleNewReply = async (parentId) => {
    const replyText = replyTexts[parentId]; // 親コメントに対応する返信テキストを取得
    if (!replyText?.trim()) return; // テキストが空の場合は何もしない

    try {
      // API呼び出しのためのリクエストボディ
      const requestBody = {
        content: replyText,
        blogId: targetBlogId,
        parentCommentId: parentId,
      };

      // API呼び出し
      const response = await fetchWithAuth(
        `${config.apiBaseUrl}/api/comment/reply`,
        {
          method: "POST",
          body: JSON.stringify(requestBody),
        }
      );

      if (!response.ok) {
        throw new Error("返信コメントの登録に失敗しました");
      }

      // レスポンスのデータを取得
      const newReplyData = await response.json();

      // 親コメントのリストに返信を追加し、replyCountをインクリメント
      setComments((prev) =>
        prev.map((comment) => {
          if (comment.id === parentId) {
            return {
              ...comment,
              replies: [...(comment.replies || []), newReplyData], // 返信を追加
              replyCount: (comment.replyCount || 0) + 1, // replyCountを1増加
            };
          }
          return comment;
        })
      );

      // 返信欄を表示状態にする
      setExpandedCommentIds((prev) => {
        const newSet = new Set(prev);
        if (!newSet.has(parentId)) {
          newSet.add(parentId);
        }
        return newSet;
      });

      // 返信テキストをリセット
      setReplyTexts((prev) => ({
        ...prev,
        [parentId]: "",
      }));

      // 返信入力欄を非表示にする
      setShowReplyInput((prev) => ({
        ...prev,
        [parentId]: false,
      }));
    } catch (error) {
      console.error("エラー:", error);
    }
  };
  const toggleCommentList = () => {
    setShowCommentList((prev) => !prev); // showCommentListの状態を切り替える
  };

  /**
   * コメントを描画します。返信コメントが含まれる場合は再起的に実行されます
   * @param {*} comment 描画されるコメント情報
   * @param {*} isReply 返信コメントの場合true
   * @returns コメントリスト
   */
  const renderComment = (comment, isReply = false) => {
    return (
      <div
        key={comment.id} // コメントのユニークIDをキーとして使用
        className={`flex gap-4 p-4 ${
          !isReply ? "border-b border-gray-200" : "" // 親コメント毎に区切る
        }`}
        onClick={() =>
          isAuthenticated && !isReply && toggleReplyInput(comment.id)
        } // 返信ではない場合、クリックで返信入力欄の表示を切り替え
      >
        {comment.author.profileImageUrl ? (
          <img
            src={`${config.getImageUrl}/${comment.author.profileImageUrl}`}
            alt={`${comment.author.displayName}のプロフィール画像`}
            className="w-10 h-10 rounded-full object-cover"
          />
        ) : (
          <i className="fas fa-user fa-2x text-blue-300"></i>
        )}

        <div className="flex-1">
          {/* ユーザ名 */}
          <div className="font-roboto text-sm text-gray-700 mb-1">
            {comment.author.displayName}
          </div>
          {/* コメント内容 */}
          <div className="font-roboto text-gray-900">{comment.content}</div>

          {/* 返信がある場合、返信表示ボタン */}
          {!isReply && comment.replyCount > 0 && (
            <button
              onClick={(e) => {
                e.stopPropagation(); // 親要素へのクリックイベントを伝播させない
                toggleReplies(comment.id); // 返信の表示/非表示を切り替え
              }}
              className="text-blue-600 text-sm mt-2 hover:text-blue-800"
            >
              {expandedCommentIds.has(comment.id)
                ? "返信を非表示"
                : `返信を表示 (${comment.replyCount})`}
            </button>
          )}

          {/* 返信リストの表示 */}
          {!isReply && expandedCommentIds.has(comment.id) && (
            <div className="mt-4">
              {comment.replies?.map((reply) => renderComment(reply, true))}
            </div>
          )}

          {/* 返信入力欄の表示 */}
          {!isReply && showReplyInput[comment.id] && (
            <div className="mt-4 flex gap-2">
              <input
                type="text"
                name={`reply-${comment.id}`} // 返信テキストの入力欄
                value={replyTexts[comment.id] || ""}
                onChange={(e) =>
                  setReplyTexts((prev) => ({
                    ...prev,
                    [comment.id]: e.target.value, // 返信テキストをStateに保存
                  }))
                }
                onClick={(e) => e.stopPropagation()} // 親要素へのクリックイベントを伝播させない
                placeholder="返信を入力..."
                className="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-blue-500"
              />
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  handleNewReply(comment.id); // 返信コメントを追加
                }}
                className="px-4 py-2 text-white bg-blue-600 rounded-lg hover:bg-blue-700"
              >
                <i className="fas fa-paper-plane"></i> {/* 送信アイコン */}
              </button>
            </div>
          )}
        </div>
      </div>
    );
  };

  return (
    <>
      {/* コメント入力欄 */}
      <div className="w-full mt-16 bg-white rounded-lg shadow-2xl">
        {isAuthenticated && (
          <div className="p-4 border-b border-gray-200">
            <div className="flex gap-2">
              <input
                type="text"
                name="new-comment" // 新規コメントの入力欄
                value={newComment}
                onChange={(e) => setNewComment(e.target.value)} // 新規コメントの内容をStateに保存
                placeholder="コメントを入力..."
                className="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-blue-500"
              />
              <button
                onClick={handleNewComment} // コメントを追加する
                className="px-4 py-2 text-white bg-blue-600 rounded-lg hover:bg-blue-700"
              >
                <i className="fas fa-paper-plane"></i> {/* 送信アイコン */}
              </button>
            </div>
          </div>
        )}
        {/* コメントの表示 */}
        {showCommentList ? (
          <>
            <div className="m-4">
              <a
                onClick={toggleCommentList}
                className="cursor-pointer text-blue-500 hover:underline"
              >
                コメント欄を非表示
              </a>
            </div>

            {comments.map((comment) => renderComment(comment))}
          </>
        ) : (
          <div className="m-4 pb-4">
            <a
              onClick={toggleCommentList}
              className="cursor-pointer text-blue-500 hover:underline"
            >
              コメント欄を表示
            </a>
          </div>
        )}
      </div>
    </>
  );
}
