"use client";
import "@fortawesome/fontawesome-free/css/all.min.css";
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import icon from "../images/icon_header.png";
import LoginButton from "./LoginButton";
import NotificationList from "./NotificationList";

function MainComponent({ onLogin, isAuthenticated, setIsAuthenticated }) {
  const navigate = useNavigate();
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const toggleMenu = () => {
    setIsMenuOpen(!isMenuOpen);
  };
  const [isModalOpen, setIsModalOpen] = useState(false);
  const toggleModal = () => {
    setIsModalOpen(!isModalOpen);
  };

  const [isNotificationOpen, setIsNotificationOpen] = useState(false);
  const [notifications, setNotifications] = useState([
    { id: 1, message: "新しいコメントがあります", isRead: false },
    { id: 2, message: "フォロワーが増えました", isRead: false },
    { id: 3, message: "あなたのブログが「いいね！」されました", isRead: false },
  ]);

  const unreadCount = notifications.filter(
    (notification) => !notification.isRead
  ).length;

  const toggleNotificationMenu = () => {
    setIsNotificationOpen(!isNotificationOpen);
  };

  // すべての通知を既読にするメソッド
  const markAllAsRead = () => {
    const updatedNotifications = notifications.map((notification) => ({
      ...notification,
      isRead: true,
    }));
    setNotifications(updatedNotifications);
  };

  const handleLogout = () => {
    // 認証状態およびアクセストークンをlocalStorageから削除
    localStorage.setItem("ll_isAuthenticated", "false");
    localStorage.removeItem("ll_accessToken");
    localStorage.removeItem("ll_refreshToken");
    localStorage.removeItem("ll_tokenExpires");

    // 認証状態を更新してfalseに
    setIsAuthenticated(false);
    setIsMenuOpen(false);
    navigate("/");
  };

  return (
    <div>
      <header className="bg-black text-white px-4 md:px-8 py-4 flex items-center justify-between border-b border-b-white">
        <div className="flex items-center">
          <img src={icon} alt="会社のロゴ" className="w-10 h-10 mr-3" />
          <h5 className="text-sm font-bold font-sans">
            {isAuthenticated ? "LikeLive" : "音楽好きのためのSNS"}
          </h5>
        </div>

        <div className="flex space-x-7 items-center">
          {/* 未認証の場合ログインボタンを表示 */}
          {!isAuthenticated && (
            <LoginButton
              onLogin={onLogin}
              setIsAuthenticated={setIsAuthenticated}
            />
          )}

          {/* 通知アイコン (認証済みの場合のみ) */}
          {isAuthenticated && (
            <div className="relative">
              <button
                className="px-2 py-1 rounded-3xl bg-yellow-300"
                onClick={toggleNotificationMenu}
              >
                <i className="fas fa-bell text-white cursor-pointer"></i>
              </button>
              {unreadCount > 0 && (
                <div className="inline">
                  <span className="text-sm text-red-100 bg-red-600 px-1 rounded">
                    {unreadCount > 99 ? "99+" : unreadCount}
                  </span>
                </div>
              )}
              {/* NotificationListコンポーネントを呼び出し */}
              <NotificationList
                notifications={notifications}
                isNotificationOpen={isNotificationOpen}
                setIsNotificationOpen={setIsNotificationOpen}
                markAllAsRead={markAllAsRead}
              />
            </div>
          )}
          {/* ユーザアイコンの表示 (認証済みの場合のみ) */}
          {isAuthenticated && (
            <button
              className="px-2 py-1 rounded-3xl bg-green-300"
              onClick={() => navigate("/sample")}
            >
              <i className="fas fa-user text-white cursor-pointer"></i>
            </button>
          )}

          <button
            onClick={toggleMenu}
            type="button"
            className={isMenuOpen ? "z-10 space-y-2 pb-6" : "z-10 space-y-2"}
          >
            <div
              className={
                isMenuOpen
                  ? "w-8 h-0.5 bg-gray-600 translate-y-2.5 rotate-45 transition duration-500 ease-in-out"
                  : "w-8 h-0.5 bg-gray-300 transition duration-500 ease-in-out"
              }
            />
            <div
              className={
                isMenuOpen
                  ? "opacity-0 transition duration-500 ease-in-out"
                  : "w-8 h-0.5 bg-gray-300 transition duration-500 ease-in-out"
              }
            />
            <div
              className={
                isMenuOpen
                  ? "w-8 h-0.5 bg-gray-600 -rotate-45 transition duration-500 ease-in-out"
                  : "w-8 h-0.5 bg-gray-300 transition duration-500 ease-in-out"
              }
            />
          </button>
        </div>

        {/* nav */}
        <nav
          className={
            isMenuOpen
              ? "text-left fixed bg-slate-50 right-0 top-0 w-5/12 h-screen flex flex-col justify-start ease-linear duration-300"
              : "fixed right-[-100%] ease-linear duration-300"
          }
        >
          <ul className="mt-12 text-black">
            <li
              className="p-2 hover:text-blue-500 cursor-pointer flex justify-between items-center"
              onClick={() => navigate("/dashboard")}
            >
              <span className="py-2 inline-block">ダッシュボード</span>
              <span className="text-blue-500 ml-auto mr-2 text-lg">{">"}</span>
            </li>
            <li
              className="p-2 hover:text-blue-500 cursor-pointer flex justify-between items-center"
              onClick={toggleModal}
            >
              <span className="py-2 inline-block">ブログ</span>
              <span className="text-blue-500 ml-auto mr-2 text-lg">
                {isModalOpen ? "-" : "+"}
              </span>
            </li>
            {isModalOpen && (
              <ul className="pl-4 bg-gray-100">
                <li
                  className="p-2 hover:text-blue-500 cursor-pointer flex justify-between items-center"
                  onClick={() => navigate("/blog/category1")}
                >
                  <span className="py-2 inline-block">ブログを書く</span>
                  <span className="text-blue-500 ml-auto mr-2 text-lg">
                    {">"}
                  </span>
                </li>
                <li
                  className="p-2 hover:text-blue-500 cursor-pointer flex justify-between items-center"
                  onClick={() => navigate("/blog/category2")}
                >
                  <span className="py-2 inline-block">ブログを検索する</span>
                  <span className="text-blue-500 ml-auto mr-2 text-lg">
                    {">"}
                  </span>
                </li>
                <li
                  className="p-2 hover:text-blue-500 cursor-pointer flex justify-between items-center"
                  onClick={() => navigate("/blog/category3")}
                >
                  <span className="py-2 inline-block">マイブログ</span>
                  <span className="text-blue-500 ml-auto mr-2 text-lg">
                    {">"}
                  </span>
                </li>
              </ul>
            )}
            <li
              className="p-2 hover:text-blue-500 cursor-pointer flex justify-between items-center"
              onClick={() => navigate("/user")}
            >
              <span className="py-2 inline-block">ユーザ</span>
              <span className="text-blue-500 ml-auto mr-2 text-lg">{">"}</span>
            </li>
            <li
              className="p-2 hover:text-blue-500 cursor-pointer flex justify-between items-center"
              onClick={() => navigate("/setting")}
            >
              <span className="py-2 inline-block">SETTING</span>
              <span className="text-blue-500 ml-auto mr-2 text-lg">{">"}</span>
            </li>
            {isAuthenticated && (
              <li
                className="p-2 hover:text-blue-500 cursor-pointer flex justify-between items-center"
                onClick={() => handleLogout()}
              >
                <span className="py-2 inline-block">ログアウト</span>
                <span className="text-blue-500 ml-auto mr-2 text-lg">
                  {">"}
                </span>
              </li>
            )}
          </ul>
        </nav>
      </header>
    </div>
  );
}

export default MainComponent;
