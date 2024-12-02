"use client";
import React, { useState } from "react";
import { useNavigate } from "react-router";
import config from "../config/properties";
import fetchWithAuth from "../util/fetchUtil";

/**
 *
 * @param {isFollow} 表示対象がフォロー中のユーザかフォロワーかを判断する、trueの場合フォロー中のユーザを表示
 * @returns
 */
export default function FollowListModal({
  isFollow,
  usersInfo,
  setUsersInfo,
  onClose,
  isOthersInfo,
}) {
  const navigate = useNavigate();

  // フォロー関係の変更有無を保持する
  const [isFollowUpdate, setIsFollowUpdate] = useState(false);

  // モーダル外をクリックした場合に閉じる処理
  const handleOutsideClick = (e) => {
    if (e.target.id === "modalBackground") {
      onClose(isFollowUpdate);
    }
  };

  const handleNavigate = (userInfo) => {
    onClose(isFollowUpdate);

    if (userInfo.user.id == localStorage.getItem("ll_userId")) {
      navigate("/user/profile");
    } else {
      navigate(`/user/${userInfo.user.id}`);
    }
  };

  const clearFollow = async (userInfo) => {
    try {
      // APIを呼び出してフォロー解除を実行する
      const response = await fetchWithAuth(
        `${config.apiBaseUrl}/api/follow/cancel/${userInfo.user.id}`, // APIのURLにtargetIdを挿入
        {
          method: "POST", // POSTメソッドを使用
        }
      );

      if (response.ok) {
        // フォロー解除が成功したら、usersの状態を更新する
        setUsersInfo((prevUsersInfo) =>
          prevUsersInfo.map(
            (u) =>
              u.user.id === userInfo.user.id
                ? { ...u, isFollowing: !u.isFollowing } // idが一致する場合、isFollowingを反転
                : u // 一致しない場合はそのまま
          )
        );
        setIsFollowUpdate(true);
      } else {
        console.error("フォロー解除に失敗しました。");
      }
    } catch (error) {
      console.error("フォロー解除中にエラーが発生しました:", error);
    }
  };

  const follow = async (userInfo) => {
    try {
      // APIを呼び出してフォローを実行する
      const response = await fetchWithAuth(
        `${config.apiBaseUrl}/api/follow/${userInfo.user.id}`, // APIのURLにtargetIdを挿入
        {
          method: "POST", // POSTメソッドを使用
        }
      );

      if (response.ok) {
        // フォローが成功したら、usersの状態を更新する
        setUsersInfo((prevUsersInfo) =>
          prevUsersInfo.map(
            (u) =>
              u.user.id === userInfo.user.id
                ? { ...u, isFollowing: !u.isFollowing } // idが一致する場合、isFollowingを反転
                : u // 一致しない場合はそのまま
          )
        );
        setIsFollowUpdate(true);
      } else {
        console.error("フォローに失敗しました。");
      }
    } catch (error) {
      console.error("フォロー中にエラーが発生しました:", error);
    }
  };

  return (
    <div
      id="modalBackground"
      className="fixed z-30 inset-0 bg-gray-800 bg-opacity-75 flex justify-center items-center"
      onClick={handleOutsideClick}
    >
      <div
        className="bg-white w-[400px] p-4 rounded-lg shadow-lg max-h-[80vh]"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="flex justify-between items-center mb-4">
          <h3 className="font-crimson-text text-xl">
            {/*  */}
            {isFollow ? "フォロー中のユーザ" : "フォロワー"}
          </h3>
          <button
            onClick={() => onClose(isFollowUpdate)}
            className="text-sm p-2 font-crimson-text"
          >
            <i className="fa-solid fa-xmark"></i>
          </button>
        </div>
        <ul className="p-2 space-y-3 max-h-[70vh] overflow-y-auto">
          {usersInfo.map((userInfo) => (
            <li
              key={userInfo.user.id}
              className="flex items-center justify-between space-x-3"
            >
              <div
                className="flex items-center space-x-3 cursor-pointer"
                onClick={() => handleNavigate(userInfo)}
              >
                {userInfo.user.profileImageUrl ? (
                  <img
                    src={`${config.apiBaseUrl}/api/public/files/${userInfo.user.profileImageUrl}`}
                    alt={`Profile of ${userInfo.user.displayName}`}
                    className="w-10 h-10 rounded-full"
                  />
                ) : (
                  <i className="rouded-full fas fa-user fa-2x text-blue-300 mr-4"></i>
                )}
                <p className="font-roboto">{userInfo.user.displayName}</p>
              </div>
              <p className="text-sm font-crimson-text text-right pr-3">
                {userInfo.isFollowing ? (
                  <>
                    {!isOthersInfo && (
                      <button
                        className="bg-white text-black border border-gray-300 rounded-full p-1 px-2 text-xs"
                        onClick={() => clearFollow(userInfo)}
                      >
                        フォロー中
                      </button>
                    )}
                  </>
                ) : (
                  <>
                    {!isOthersInfo && (
                      <button
                        className="bg-blue-500 text-white rounded-full p-1 px-2 text-xs"
                        onClick={() => follow(userInfo)}
                      >
                        フォローする
                      </button>
                    )}
                  </>
                )}
              </p>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}
