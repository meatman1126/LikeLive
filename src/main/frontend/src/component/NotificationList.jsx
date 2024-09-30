import "@fortawesome/fontawesome-free/css/all.min.css";

import React from "react";

function NotificationList({
  notifications,
  isNotificationOpen,
  setIsNotificationOpen,
  markAllAsRead,
}) {
  return (
    <>
      {/* 通知リストの表示 */}
      {isNotificationOpen && (
        <div className="absolute right-0 mt-2 w-96 bg-white shadow-lg z-20">
          {/* ヘッダー部分 */}
          <div className="flex justify-between items-center p-2 bg-green-100">
            <h3 className="text-lg font-bold text-black">未読通知リスト</h3>
            <div className="flex space-x-2">
              <button
                onClick={markAllAsRead}
                className="text-xs text-white hover:underline bg-blue-400 rounded-md p-1"
              >
                全て既読にする
              </button>
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
                >
                  {notification.message}
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
