import React, { useCallback, useRef, useState } from "react";
import config from "../config/properties";
import { getCroppedImg } from "../util/cropImageToCanvas";
import fetchWithAuth from "../util/fetchUtil";
import { useLoading } from "../util/LoadingContext";
import { handleSuccessToast } from "../util/toastUtil";

/**
 *
 * @param initialUsername ユーザ名の初期値
 * @param isOpen モーダルの開閉有無
 * @param onClose モーダルを閉じた時に呼ばれるメソッド
 * @param setUserInfo ユーザ情報の更新用メソッド
 *
 * @returns ユーザ情報登録用モーダル
 */
export default function UserRegistrationModal({
  initialUsername,
  isOpen,
  onClose,
  setUserInfo,
}) {
  // ユーザ名を管理するstate
  const [username, setUsername] = useState(initialUsername);

  // ユーザプロフィール画像を管理するstate
  const [profileImage, setProfileImage] = useState(null);

  // 円形に切り取られた画像を管理するstate(切り取り元はprofileImage)
  const [croppedImage, setCroppedImage] = useState(null);

  // 画像切り取りに使用する変数
  const [crop, setCrop] = useState({ x: 0, y: 0 });
  const [zoom, setZoom] = useState(1);
  const [croppedAreaPixels, setCroppedAreaPixels] = useState(null);

  // 検索バーに入力されたアーティスト名を管理するstate
  const [searchArtistName, setSearchArtistName] = useState("");

  // 検索結果のアーティストリストを管理するstate
  const [artistSuggestions, setArtistSuggestions] = useState([]);

  // 好きなアーティストとして選択されたアーティストリストを管理するstate
  const [selectedArtist, setSelectedArtist] = useState([]);

  // ローディング制御メソッドを取得
  const { startLoading, stopLoading } = useLoading();

  // ファイルinput要素への参照
  const fileInputRef = useRef(null);

  /**
   * 入力されたファイルをprofileImageにセットします。
   *
   * @param {*} e 入力ファイル変更イベント
   */
  const handleProfileImageChange = (e) => {
    const file = e.target.files[0];
    if (!file) {
      return;
    }
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => {
      setProfileImage(reader.result);
    };
  };

  /**
   * 画像切り取り時に切り取り情報（座標およびサイズ）をcroppedAreaPixelsにセットします。
   */
  const onCropComplete = useCallback((croppedArea, croppedAreaPixels) => {
    setCroppedAreaPixels(croppedAreaPixels);
  }, []);

  /**
   * 切り取りが完了した画像をcroppedImageにセットします。
   * profileImageの内容をクリアします。
   */
  const showCroppedImage = useCallback(async () => {
    try {
      const croppedImage = await getCroppedImg(profileImage, croppedAreaPixels);
      setCroppedImage(croppedImage);
      setProfileImage(null);

      // input要素のファイル選択をリセット
      if (fileInputRef.current) {
        fileInputRef.current.value = "";
      }
    } catch (e) {
      console.error(e);
    }
  }, [profileImage, croppedAreaPixels]);

  /**
   * SpotifyAPIを呼び出し、アーティスト情報を検索します。
   * 検索結果をartistSuggestionsにセットします。
   */
  const handleArtistSearch = async () => {
    startLoading();

    const searchQuery = searchArtistName;
    if (searchQuery) {
      const spotifyTokenResponse = await fetchWithAuth(
        `${config.apiBaseUrl}/api/spotify/get-token`,
        {
          method: "GET",
        }
      );
      const responseData = await spotifyTokenResponse.json();
      const spotifyAccessToken = responseData.accessToken;

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
    stopLoading();
  };

  /**
   * 検索結果から選択されたアーティストを好きなアーティストとして追加します。
   *
   * @param {*} artist 好きなアーティストとして選択されたアーティスト
   */
  const handleSelectArtist = (artist) => {
    // すでにリストに追加されているかを確認
    const isAlreadySelected = selectedArtist.some(
      (selected) => selected.id === artist.id
    );

    // すでに好きなアーティストに追加されている場合は追加処理をスキップ
    if (!isAlreadySelected) {
      setSelectedArtist((prevArtists) => [...prevArtists, artist]);
      // アーティストを選択した後、artistSuggestionsから削除
      setArtistSuggestions((prevSuggestions) =>
        prevSuggestions.filter((suggestion) => suggestion.id !== artist.id)
      );
    }
  };

  /**
   * 指定されたアーティストを好きなアーティストリストから削除します。
   * @param {*} artistId 削除対象のアーティストID
   */
  const handleRemoveArtist = (artistId) => {
    setSelectedArtist((prevArtists) =>
      prevArtists.filter((artist) => artist.id !== artistId)
    );
  };

  /**
   * ユーザ情報更新APIを呼び出します。
   *
   * @param {*} e submitイベント
   */
  const handleSubmit = async (e) => {
    e.preventDefault();

    // リクエストを作成
    const formData = new FormData();
    formData.append("userName", username);

    // selectedArtist配列が存在する場合、ループで個別に追加
    if (selectedArtist && selectedArtist.length > 0) {
      selectedArtist.forEach((artist, index) => {
        formData.append(`artistList[${index}].id`, artist.id);
        formData.append(`artistList[${index}].name`, artist.name);
        formData.append(`artistList[${index}].imageUrl`, artist.images[0].url);
      });
    } else {
      // 空の場合はnullを設定
      formData.append("artistList", null);
    }

    // プロフィール画像が選択されている場合追加
    if (croppedImage) {
      const response = await fetch(croppedImage);
      const blob = await response.blob();
      formData.append("profileImage", blob);
    }

    try {
      const response = await fetchWithAuth(
        `${config.apiBaseUrl}/api/user/update/initial`,
        {
          method: "POST",
          body: formData,
        }
      );

      if (response.ok) {
        const updatedUser = await response.json();
        onClose();
        setUserInfo(updatedUser);
        handleSuccessToast("ユーザ情報が登録されました");
      } else {
        console.error("ユーザ情報の更新に失敗しました");
      }
    } catch (error) {
      console.error("エラーが発生しました:", error);
    }
  };

  return (
    isOpen && (
      <div className="w-full fixed inset-0 z-40 flex items-center justify-center">
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

            {/* 初回ログイン時のプロフィール画像登録は実施しない */}
            {/* <div className="mb-4">
              <label className="block text-sm font-bold mb-2 font-roboto">
                プロフィール画像
              </label>
              <div className="block items-center">
                {/* input要素と切り取られた画像を横並びに配置
            <input
                  type="file"
                  ref={fileInputRef} // refでinput要素にアクセス
                  onChange={handleProfileImageChange}
                  className={croppedImage == null ? "" : "hidden"}
                />
                {croppedImage && (
                  <div className="flex items-center justify-between">
                    <img
                      src={croppedImage}
                      alt="Cropped"
                      className="rounded-full h-12 w-12 ml-4"
                    />
                    <button
                      type="button"
                      onClick={() => setCroppedImage(null)} // 画像選択を解除
                      className="ml-4 bg-red-500 hover:bg-red-700 text-white font-bold py-1 px-3 rounded-full"
                    >
                      画像をリセットする
                    </button>
                  </div>
                )}
              </div>
              {profileImage && (
                <>
                  <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-75 z-50">
                    <div className="relative w-full max-w-md mx-auto p-4 bg-white rounded-lg shadow-lg">
                      <div className="relative w-full h-64">
                        <Cropper
                          image={profileImage}
                          crop={crop}
                          zoom={zoom}
                          aspect={1}
                          onCropChange={setCrop}
                          onZoomChange={setZoom}
                          onCropComplete={onCropComplete}
                          cropShape="round" // 画像を丸型に切り取る
                        />
                      </div>
                      <div className="flex justify-center mt-4">
                        <button
                          type="button"
                          className="bg-blue-500 hover:bg-blue-700 text-white py-2 px-4 rounded"
                          onClick={showCroppedImage}
                        >
                          画像を切り取る
                        </button>
                      </div>
                    </div>
                  </div>
                </>
              )}
            </div> */}
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
                    <div className="flex items-center space-x-4">
                      {artist.images.length > 0 && (
                        <img
                          src={artist.images[0].url}
                          alt={artist.name}
                          className="w-10 h-10 rounded-full object-cover"
                        />
                      )}
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
                  value={searchArtistName}
                  onChange={(e) => setSearchArtistName(e.target.value)}
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
                      className="p-2 cursor-pointer hover:bg-gray-200 font-roboto flex items-center space-x-4"
                      onClick={() => handleSelectArtist(artist)}
                    >
                      {artist.images.length > 0 && (
                        <img
                          src={artist.images[0].url}
                          alt={artist.name}
                          className="w-12 h-12 rounded-full object-cover"
                        />
                      )}
                      <h3>{artist.name}</h3>
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
