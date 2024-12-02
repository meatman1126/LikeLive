"use client";
import "@fortawesome/fontawesome-free/css/all.min.css";
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import config from "../config/properties";
import icon from "../images/icon_header.png";
import fetchWithAuth from "../util/fetchUtil";
import LoginButton from "./LoginButton";
import NotificationList from "./NotificationList";

function MainComponent({
  onLogin,
  isAuthenticated,
  setIsAuthenticated,
  // userInfo: initialUserInfo,
  profileImage,
}) {
  const navigate = useNavigate();
  // ブログページ内でのメニューを使用した画面遷移はメニューが開きっぱなしになる問題に対処するメソッド
  // ヘッダーメニューからの遷移はこのメソッドに統一するかも？
  const navigatePage = (path) => {
    setIsMenuOpen(false);
    navigate(path);
  };
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const toggleMenu = () => {
    setIsMenuOpen(!isMenuOpen);
  };
  const [isBlogModalOpen, setIsBlogModalOpen] = useState(false);
  const toggleBlogModal = () => {
    setIsBlogModalOpen(!isBlogModalOpen);
  };
  const [isUserModalOpen, setIsUserModalOpen] = useState(false);
  const toggleUserModal = () => {
    setIsUserModalOpen(!isUserModalOpen);
  };
  const [userInfo, setUserInfo] = useState(null); // userInfoの状態管理
  // API からユーザ情報を取得する処理を追加
  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const response = await fetchWithAuth(
          `${config.apiBaseUrl}/api/user/me`,
          {
            method: "GET",
          }
        );
        if (response.ok) {
          const data = await response.json();
          setUserInfo(data); // 取得したユーザ情報をstateにセット
        } else {
          console.error("ユーザ情報の取得に失敗しました");
        }
      } catch (error) {
        console.error("API呼び出しに失敗しました:", error);
      }
    };

    // userInfo が未指定かつ認証済みの場合にAPIを呼び出す
    if (isAuthenticated) {
      fetchUserInfo();
    }
  }, [isAuthenticated]);

  const [isNotificationOpen, setIsNotificationOpen] = useState(false);
  const [notifications, setNotifications] = useState([]);
  // 通知データの取得
  useEffect(() => {
    const fetchNotifications = async () => {
      try {
        const response = await fetchWithAuth(
          `${config.apiBaseUrl}/api/notification/unread`,
          {
            method: "GET",
          }
        );
        if (response.ok) {
          const data = await response.json();
          setNotifications(data); // 取得した通知データをstateにセット
        } else {
          console.error("通知データの取得に失敗しました");
        }
      } catch (error) {
        console.error("API呼び出しに失敗しました:", error);
      }
    };

    if (isAuthenticated) {
      fetchNotifications();
    }
  }, [isAuthenticated]);

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
    // 全部既読APIを呼び出す
    try {
      const response = fetchWithAuth(
        `${config.apiBaseUrl}/api/notification/mark-all-read`,
        {
          method: "POST",
        }
      );
    } catch (error) {
      console.log(error);
    }
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
        <div
          className="flex items-center cursor-pointer"
          onClick={() => navigate("/")}
        >
          <img src={icon} alt="会社のロゴ" className="w-10 h-10 mr-3" />
          <h5 className="text-sm font-bold font-sans">
            {isAuthenticated ? "LikeLive" : "音楽好きのためのSNS"}
          </h5>
        </div>

        <div className="flex space-x-7 items-center">
          {/* 未認証の場合ログインボタンを表示 */}
          {!isAuthenticated && <LoginButton onLogin={onLogin} />}

          {/* 通知アイコン (認証済みの場合のみ) */}
          {isAuthenticated && (
            <div>
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
                unreadCount={unreadCount}
              />
            </div>
          )}
          {/* ユーザアイコンの表示 (認証済みの場合のみ) */}
          {isAuthenticated && (
            <>
              {profileImage ? (
                <button
                  className="px-2 py-1 w-13 h-13 rounded-full"
                  onClick={() => navigate("/user/profile")}
                >
                  {/* 動的な画像の呼び出し方はAPIを呼ぶ方針とします。 */}
                  <img
                    src={`${config.apiBaseUrl}/api/public/files/${profileImage}`}
                    alt="Profile Image"
                    className="w-9 h-9 rounded-full"
                  />
                </button>
              ) : userInfo && userInfo.profileImageUrl ? (
                <button
                  className="px-2 py-1 w-13 h-13 rounded-full"
                  onClick={() => navigate("/user/profile")}
                >
                  {/* 動的な画像の呼び出し方はAPIを呼ぶ方針とします。 */}
                  <img
                    src={`${config.apiBaseUrl}/api/public/files/${userInfo.profileImageUrl}`}
                    alt="Profile Image"
                    className="w-9 h-9 rounded-full"
                  />
                </button>
              ) : (
                <button
                  className="px-2 py-1 w-13 h-13 rouded-full bg-white"
                  onClick={() => navigate("/user/profile")}
                >
                  <i className="fas fa-user text-black cursor-pointer"></i>
                </button>
              )}
            </>
          )}
          {isAuthenticated && (
            <button
              onClick={toggleMenu}
              type="button"
              className={isMenuOpen ? "z-20 space-y-2 pb-6" : "z-20 space-y-2"}
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
          )}
        </div>

        {/* nav */}
        <nav
          className={
            isMenuOpen
              ? "z-10 text-left fixed right-0 top-0 sm:w-5/12 w-7/12 h-screen flex flex-col justify-start ease-linear duration-300 bg-green-50"
              : "fixed right-[-100%] ease-linear duration-300"
          }
        >
          <ul className="mt-12 text-black">
            <li
              className="p-2 hover:text-blue-500 cursor-pointer flex justify-between items-center"
              onClick={() => navigatePage("/dashboard")}
            >
              <span className="py-2 inline-block">ダッシュボード</span>
              <span className="text-blue-500 ml-auto mr-2 text-lg">{">"}</span>
            </li>
            <li
              className="p-2 hover:text-blue-500 cursor-pointer flex justify-between items-center"
              onClick={toggleBlogModal}
            >
              <span className="py-2 inline-block">ブログ</span>
              <span className="text-blue-500 ml-auto mr-2 text-lg">
                {isBlogModalOpen ? "-" : "+"}
              </span>
            </li>
            {isBlogModalOpen && (
              <ul className="pl-4 bg-gray-50">
                <li
                  className="p-2 hover:text-blue-500 cursor-pointer flex justify-between items-center"
                  onClick={() => navigatePage("/blog/create")}
                >
                  <span className="py-2 inline-block">ブログを書く</span>
                  <span className="text-blue-500 ml-auto mr-2 text-lg">
                    {">"}
                  </span>
                </li>
                <li
                  className="p-2 hover:text-blue-500 cursor-pointer flex justify-between items-center"
                  onClick={() => navigatePage("/blog/search")}
                >
                  <span className="py-2 inline-block">ブログを検索する</span>
                  <span className="text-blue-500 ml-auto mr-2 text-lg">
                    {">"}
                  </span>
                </li>
              </ul>
            )}
            <li
              className="p-2 hover:text-blue-500 cursor-pointer flex justify-between items-center"
              onClick={toggleUserModal}
            >
              <span className="py-2 inline-block">ユーザ管理</span>
              <span className="text-blue-500 ml-auto mr-2 text-lg">
                {isUserModalOpen ? "-" : "+"}
              </span>
            </li>
            {isUserModalOpen && (
              <ul className="pl-4 bg-gray-50">
                <li
                  className="p-2 hover:text-blue-500 cursor-pointer flex justify-between items-center"
                  onClick={() => navigatePage("/user/profile")}
                >
                  <span className="py-2 inline-block">プロフィール管理</span>
                  <span className="text-blue-500 ml-auto mr-2 text-lg">
                    {">"}
                  </span>
                </li>
                <li
                  className="p-2 hover:text-blue-500 cursor-pointer flex justify-between items-center"
                  onClick={() => navigatePage("/user/search")}
                >
                  <span className="py-2 inline-block">ユーザを検索する</span>
                  <span className="text-blue-500 ml-auto mr-2 text-lg">
                    {">"}
                  </span>
                </li>
              </ul>
            )}
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
