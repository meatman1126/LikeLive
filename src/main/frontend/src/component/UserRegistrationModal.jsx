"use client";
import React, { useEffect, useState } from "react";
import config from "../config/properties";
import fetchWithAuth from "../util/fetchUtil";

export default function UserRegistrationModal({
  initialUsername,
  isOpen,
  onClose,
}) {
  const [username, setUsername] = useState(initialUsername);
  const [profileImage, setProfileImage] = useState(null);
  const [artistName, setArtistName] = useState("");
  const [artistSuggestions, setArtistSuggestions] = useState([]);
  const [selectedArtist, setSelectedArtist] = useState([]);

  const handleProfileImageChange = (e) => {
    setProfileImage(e.target.files[0]);
  };

  const handleArtistSearch = async () => {
    const searchQuery = artistName;
    if (searchQuery) {
      const spotifyTokenResponse = await fetchWithAuth(
        `${config.apiBaseUrl}/api/spotify/get-token`,
        {
          method: "GET",
        }
      );
      console.log(spotifyTokenResponse);
      const responseData = await spotifyTokenResponse.json();
      console.log(responseData);
      const spotifyAccessToken = responseData.accessToken;

      console.log(spotifyAccessToken);

      const response = await fetch(
        `https://api.spotify.com/v1/search?q=${encodeURIComponent(
          searchQuery
        )}&type=artist&limit=3`,
        {
          headers: {
            Authorization: `Bearer ${spotifyAccessToken}`,
          },
        }
      );
      const data = await response.json();
      setArtistSuggestions(data.artists.items);
    } else {
      setArtistSuggestions([]);
    }
  };

  const handleSelectArtist = (artist) => {
    const isAlreadySelected = selectedArtist.some(
      (selected) => selected.id === artist.id
    );
    if (!isAlreadySelected) {
      setSelectedArtist((prevArtists) => [...prevArtists, artist]);
      // アーティストを選択した後、artistSuggestionsから削除
      setArtistSuggestions((prevSuggestions) =>
        prevSuggestions.filter((suggestion) => suggestion.id !== artist.id)
      );
    }
  };

  // アーティストを selectedArtist から削除する処理
  const handleRemoveArtist = (artistId) => {
    setSelectedArtist((prevArtists) =>
      prevArtists.filter((artist) => artist.id !== artistId)
    );
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    // FormData オブジェクトを作成
    const formData = new FormData();
    formData.append("userName", username);

    // アーティストリストを追加
    selectedArtist.forEach((artist, index) => {
      formData.append(`artistList[${index}].id`, artist.id);
      formData.append(`artistList[${index}].name`, artist.name);
      formData.append(
        `artistList[${index}].imageUrl`,
        artist.images[0].length > 0 ? artist.images[0].url : null
      );
    });

    // プロフィール画像を追加
    if (profileImage) {
      formData.append("profileImage", profileImage);
    }

    console.log(formData);
    try {
      // fetchWithAuthを使用してAPI呼び出しを行う
      const response = await fetchWithAuth(
        `${config.apiBaseUrl}/api/user/update`,
        {
          method: "POST",
          body: formData,
        }
      );

      if (response.ok) {
        const updatedUser = await response.json();
        console.log("ユーザ情報が更新されました:", updatedUser);
        onClose(); // 成功時にモーダルを閉じる
      } else {
        console.error("ユーザ情報の更新に失敗しました");
      }
    } catch (error) {
      console.error("エラーが発生しました:", error);
    }
  };

  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "auto";
    }
  }, [isOpen]);

  return (
    isOpen && (
      <div className="w-full fixed inset-0 z-50 flex items-center justify-center">
        <div
          className="fixed inset-0 bg-black opacity-50"
          onClick={onClose}
        ></div>

        <div className="relative bg-white p-6 rounded-lg shadow-lg z-10 max-w-lg w-full">
          <h2 className="text-2xl font-bold mb-4 text-center">
            ユーザ情報登録
          </h2>
          <form onSubmit={handleSubmit}>
            <div className="mb-4">
              <label className="block text-sm font-bold mb-2 font-roboto">
                ユーザ名
              </label>
              <input
                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                type="text"
                placeholder="ユーザ名"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
            </div>

            <div className="mb-4">
              <label className="block text-sm font-bold mb-2 font-roboto">
                プロフィール画像
              </label>
              <input
                className="block w-full text-sm text-gray-500 file:mr-2 file:py-2 file:px-2 file:border-none file:text-sm file:font-semibold file:bg-blue-500 file:text-white hover:file:bg-blue-700"
                type="file"
                onChange={handleProfileImageChange}
              />
            </div>

            <div className="mb-4">
              <label className="block text-sm font-bold mb-2 font-roboto">
                好きなアーティスト
              </label>
              <ul className="mb-2">
                {selectedArtist.map((artist) => (
                  <li
                    key={artist.id}
                    className="p-2 bg-gray-100 rounded-md mb-1 flex justify-between items-center"
                  >
                    <div className="flex items-center">
                      <span>{artist.name}</span>
                    </div>
                    {/* 削除アイコン */}
                    <button
                      type="button"
                      onClick={() => handleRemoveArtist(artist.id)}
                      className="text-red-500 hover:text-red-700 ml-2"
                    >
                      &times; {/* × アイコン */}
                    </button>
                  </li>
                ))}
              </ul>

              <div className="flex">
                <input
                  className="shadow appearance-none border rounded-l w-10/12 py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                  type="text"
                  placeholder="アーティスト名を入力"
                  value={artistName}
                  onChange={(e) => setArtistName(e.target.value)}
                />
                <button
                  type="button"
                  onClick={handleArtistSearch}
                  className="bg-blue-500 hover:bg-blue-700 w-2/12 text-white font-bold py-2 px-4 rounded-r focus:outline-none focus:shadow-outline"
                >
                  検索
                </button>
              </div>

              {artistSuggestions.length > 0 && (
                <ul className="bg-white border border-gray-300 rounded w-full mt-1 max-h-72 overflow-y-auto">
                  {artistSuggestions.map((artist) => (
                    <li
                      key={artist.id}
                      className="p-2 cursor-pointer hover:bg-gray-200 font-roboto"
                      onClick={() => handleSelectArtist(artist)}
                    >
                      <h3>{artist.name}</h3>
                      {artist.images.length > 0 && (
                        <img
                          src={artist.images[0].url}
                          alt={artist.name}
                          className="w-3/12"
                        />
                      )}
                    </li>
                  ))}
                </ul>
              )}
            </div>

            <div className="flex justify-center mt-5 space-x-2">
              <button
                type="submit"
                className="bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded-full"
              >
                登録
              </button>
            </div>
          </form>
        </div>
      </div>
    )
  );
}
