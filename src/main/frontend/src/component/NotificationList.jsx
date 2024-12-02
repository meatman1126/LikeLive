import "@fortawesome/fontawesome-free/css/all.min.css";

import React from "react";
import { useNavigate } from "react-router-dom";
import config from "../config/properties";
import fetchWithAuth from "../util/fetchUtil";

function NotificationList({
  notifications,
  isNotificationOpen,
  setIsNotificationOpen,
  markAllAsRead,
  unreadCount,
}) {
  const navigate = useNavigate();

  // メッセージ生成用の関数
  const generateNotificationMessage = (notification) => {
    let message = "";

    // notificationTypeに基づいて動的メッセージを生成
    switch (notification.notificationType) {
      case "BLOG_CREATED":
        message = `${notification.triggerUser.displayName}が新規でブログを作成しました。`;
        break;
      case "COMMENT":
        message = `${notification.triggerUser.displayName}があなたのブログにコメントしました。`;
        break;
      case "FOLLOW":
        message = `${notification.triggerUser.displayName}があなたをフォローしました。`;
        break;
      default:
        message = "不明な通知タイプです。";
        break;
    }

    return message;
  };

  const handleNotification = async (notification) => {
    try {
      // 通知を既読としてマークするAPIを呼び出す
      const response = await fetchWithAuth(
        `${config.apiBaseUrl}/api/notification/mark-read`,
        {
          method: "POST",
          body: JSON.stringify([notification.id]), // 通知IDを配列で送信
        }
      );

      if (!response.ok) {
        console.error("通知の既読処理に失敗しました");
        return;
      }

      // notificationType に基づいて遷移する画面を判定
      switch (notification.notificationType) {
        case "BLOG_CREATED":
          // 新しいブログ投稿の場合、ブログの詳細ページへ遷移
          navigate(`/blog/${notification.relatedBlog.id}`);
          break;

        case "FOLLOW":
          // フォロー通知の場合、フォローユーザーのプロフィールページへ遷移
          navigate(`/user/${notification.triggerUser.id}`);
          break;

        case "COMMENT":
          // コメントへの返信通知の場合、該当するコメント付きブログページへ遷移
          navigate(`/blog/${notification.relatedBlog.id}`, {
            state: { showComments: true },
          });
          break;

        default:
          console.warn(
            "未対応の通知タイプです:",
            notification.notificationType
          );
      }
    } catch (error) {
      console.error("通知処理でエラーが発生しました:", error);
    }
  };
  return (
    <>
      {/* 通知リストの表示 */}
      {isNotificationOpen && (
        <div className="absolute right-0 mt-2 w-96 bg-white shadow-lg z-20">
          {/* ヘッダー部分 */}
          <div className="flex justify-between items-center p-2 bg-green-100">
            <h3 className="text-lg font-bold text-black">未読通知リスト</h3>
            <div className="flex space-x-2">
              {unreadCount > 0 && (
                <button
                  onClick={markAllAsRead}
                  className="text-xs text-white hover:underline bg-blue-400 rounded-md p-1"
                >
                  全て既読にする
                </button>
              )}
              <button
                onClick={() => setIsNotificationOpen(false)}
                className="text-gray-600 hover:text-gray-800"
              >
                <i className="fas fa-times"></i>
              </button>
            </div>
          </div>

          {/* 通知リスト */}
          <ul className="list-none text-sm">
            {notifications.length === 0 ? (
              <li className="text-gray-500">新しい通知はありません</li>
            ) : (
              notifications.map((notification) => (
                <li
                  key={notification.id}
                  className={`py-2 px-4 cursor-pointer border-black border ${
                    notification.isRead ? "bg-gray-300" : "font-bold text-black"
                  } hover:bg-gray-100`}
                  onClick={() => handleNotification(notification)}
                >
                  {generateNotificationMessage(notification)}
                </li>
              ))
            )}
          </ul>
        </div>
      )}
    </>
  );
}

export default NotificationList;
