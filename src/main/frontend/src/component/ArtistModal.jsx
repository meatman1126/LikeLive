import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import config from "../config/properties";
import { useLoading } from "../util/LoadingContext";
import fetchWithAuth from "../util/fetchUtil";

/**
 * アーティスト情報モーダルコンポーネント
 * @param {*} param0 onClose: モーダルを閉じる関数
 * @param {*} artistInfo: アーティスト情報
 * @returns　アーティスト情報モーダル
 */
export default function ArtistModal({ onClose, artistInfo }) {
  // 人気曲情報
  const [popularSongs, setPopularSongs] = useState([]);
  // お気に入り登録しているユーザ情報
  const [favoriteUsers, setFavoriteUsers] = useState([]);

  // ローディング状態
  const [loading, setLoading] = useState(true);
  // ローディング制御メソッドを取得
  const { startLoading, stopLoading } = useLoading();

  const navigate = useNavigate();
  // ログインユーザIDを取得
  const loginUserId = Number(localStorage.getItem("ll_userId"));

  useEffect(() => {
    if (!artistInfo) return;

    startLoading();
    setLoading(true);

    // Spotify APIからアーティストの人気曲を取得
    const fetchSpotifyData = async () => {
      try {
        // Spotifyのアクセストークンを取得
        const spotifyTokenResponse = await fetchWithAuth(
          `${config.apiBaseUrl}/api/spotify/get-token`,
          { method: "GET" }
        );

        if (!spotifyTokenResponse.ok) {
          throw new Error("Spotifyのアクセストークン取得に失敗しました");
        }

        const responseData = await spotifyTokenResponse.json();
        const spotifyAccessToken = responseData.accessToken;

        // SpfotifyAPI呼び出し
        const popularSongsResponse = await fetch(
          `https://api.spotify.com/v1/artists/${artistInfo.id}/top-tracks?market=JP`,
          { headers: { Authorization: `Bearer ${spotifyAccessToken}` } }
        );

        if (!popularSongsResponse.ok) {
          throw new Error("人気曲の取得に失敗しました");
        }

        const popularSongsData = await popularSongsResponse.json();

        setPopularSongs(
          Array.isArray(popularSongsData?.tracks)
            ? popularSongsData.tracks.map((track) => track.name)
            : []
        );
      } catch (error) {
        console.error("Spotify API エラー:", error);
      }
    };
    // お気に入り登録しているユーザ情報を取得
    const fetchFavoriteUsers = async () => {
      try {
        const favoriteUsersResponse = await fetchWithAuth(
          `${config.apiBaseUrl}/api/artist/${artistInfo.id}/users`,
          { method: "GET" }
        );

        if (!favoriteUsersResponse.ok) {
          throw new Error("お気に入りユーザの取得に失敗しました");
        }

        const favoriteUsersData = await favoriteUsersResponse.json();
        // ログインユーザを除いたお気に入りユーザを取得
        const filteredUsers = favoriteUsersData.filter(
          (user) => user.id !== loginUserId
        );
        setFavoriteUsers(filteredUsers);
      } catch (error) {
        console.error("API呼び出し エラー:", error);
      }
    };

    const fetchData = async () => {
      await fetchSpotifyData();
      await fetchFavoriteUsers();
      stopLoading();
      setLoading(false);
    };

    fetchData();
  }, [artistInfo]);

  return (
    <div
      id="modalBackground"
      className="fixed inset-0 bg-gray-800 bg-opacity-75 flex items-center justify-center"
      onClick={(e) => e.target.id === "modalBackground" && onClose()}
    >
      <div className="bg-white w-4/5 h-4/5 p-6 rounded-lg shadow-lg relative flex flex-col overflow-y-auto">
        <button
          className="absolute top-2 right-4 text-gray-500 hover:text-gray-700 text-2xl"
          onClick={onClose}
        >
          &times;
        </button>

        {artistInfo ? (
          <div className="flex flex-col items-center text-center">
            <h1 className="text-3xl font-extrabold mb-4">{artistInfo.name}</h1>
            <img
              src={artistInfo.imageUrl}
              alt={artistInfo.name}
              className="w-56 h-56 object-cover mb-4 rounded-xl shadow-lg"
            />
            <p className="text-lg text-gray-700 mb-6 px-4">{artistInfo.bio}</p>
            {/* 人気曲表示欄 */}
            <div className="w-full px-6 mb-6">
              <h2 className="text-left text-xl font-bold mb-3 border-b-2 border-gray-300 pb-1">
                {artistInfo.name}の人気曲
              </h2>
              {loading ? (
                <></>
              ) : popularSongs.length > 0 ? (
                <ul className="list-disc list-inside text-left text-gray-600">
                  {popularSongs.map((song, index) => (
                    <li key={index} className="py-1 list-none">
                      {song}
                    </li>
                  ))}
                </ul>
              ) : (
                <p className="text-gray-600">情報が取得できませんでした</p>
              )}
            </div>
            {/* お気に入り登録しているユーザ表示欄 */}
            <div className="text-left w-full px-6 mb-6">
              <h2 className="text-xl font-bold mb-3 border-b-2 border-gray-300 pb-1">
                {artistInfo.name}が好きなユーザ
              </h2>
              {loading ? (
                <></>
              ) : favoriteUsers.length > 0 ? (
                <div>
                  {favoriteUsers.map((user) => (
                    <div
                      key={user.id}
                      className="mb-2 user-card bg-white rounded-lg shadow-lg p-6 hover:shadow-xl transition-shadow cursor-pointer"
                      onClick={() => {
                        onClose();
                        navigate(`/user/${user.id}`);
                      }}
                    >
                      <div className="flex items-center mb-4">
                        {user.profileImageUrl ? (
                          <img
                            src={`${config.getImageUrl}/${user.profileImageUrl}`}
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
                        </div>
                      </div>
                      <p className="text-gray-500 text-sm">
                        {user.selfIntroduction}
                      </p>
                    </div>
                  ))}
                </div>
              ) : (
                <p className="text-gray-600 text-left">
                  お気に入り登録しているのはあなただけのようです。
                  <br></br>独り占め状態です!
                </p>
              )}
            </div>
          </div>
        ) : (
          <></>
        )}
      </div>
    </div>
  );
}
