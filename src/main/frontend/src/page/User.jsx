"use client";
import "@fortawesome/fontawesome-free/css/all.min.css";
import React, { useCallback, useEffect, useRef, useState } from "react";
import Cropper from "react-easy-crop";
import { useNavigate, useParams } from "react-router-dom";
import FollowListModal from "../component/FollowListModal";
import Header from "../component/Header";
import config from "../config/properties";
import noImage from "../images/no_image.jpeg";
import { getCroppedImg } from "../util/cropImageToCanvas";
import fetchWithAuth from "../util/fetchUtil";
import { useLoading } from "../util/LoadingContext";
import { handleSuccessToast } from "../util/toastUtil";

/**
 *
 * @param {*} isAuthenticated ユーザの認証有無を取得
 * @param {*} setIsAuthenticated ユーザの認証情報更新メソッド
 *
 * @returns ユーザプロフィール画面
 */
function User({ isAuthenticated, setIsAuthenticated }) {
  // 表示対象のプロフィールを指定するユーザID（自身のプロフィール画面の場合は未指定）
  const { targetUserId } = useParams();

  // 他ユーザのプロフィール画面の場合true
  const [isOthersPage, setIsOthersPage] = useState(false);

  const navigate = useNavigate();

  // ユーザプロフィール情報を管理するstate
  const [profile, setProfile] = useState(null);

  // 編集前のプロフィールを管理するstate
  const [initialProfile, setInitialProfile] = useState(null);

  // 下書きブログ情報を管理
  const [drafts, setDrafts] = useState([]);

  // 表示されているユーザがフォロー中のユーザリストを管理するstate
  const [followUsers, setFollowUsers] = useState(null);

  // 表示されているユーザのフォロワーリストを管理するstate
  const [followers, setFollowers] = useState(null);

  // 編集モード状態を管理するstate
  const [isEditing, setIsEditing] = useState(false);

  // 検索結果のアーティストリストを管理するstate
  const [artistSuggestions, setArtistSuggestions] = useState([]);

  // 検索に使用する入力値を管理するstate
  const [searchArtistName, setSearchArtistName] = useState("");

  // 選択されたプロフィール画像を管理するstate
  const [selectedImage, setSelectedImage] = useState(null);

  // 切り取られたプロフィール画像を管理するstate
  const [croppedImage, setCroppedImage] = useState(null);

  // 画像切り取りに使用する変数
  const [crop, setCrop] = useState({ x: 0, y: 0 });
  const [zoom, setZoom] = useState(1);
  const [croppedAreaPixels, setCroppedAreaPixels] = useState(null);

  // ローディング状態の管理
  const [isLoading, setIsLoading] = useState(true);

  // ローディング制御メソッドを取得
  const { startLoading, stopLoading } = useLoading();

  const handleFollowerLink = async (targetUserId) => {
    try {
      // APIを呼び出してフォロワー一覧を取得
      // targetUserIdが指定されているか確認してURLを切り替え
      const url = isOthersPage
        ? `${config.apiBaseUrl}/api/follow/followers/${targetUserId}`
        : `${config.apiBaseUrl}/api/follow/followers`;

      // APIを呼び出してフォロワー一覧を取得
      const response = await fetchWithAuth(url, {
        method: "GET",
      });

      if (response.ok) {
        const data = await response.json();

        // 取得したデータをフォーマットして followers にセット
        setFollowers(data);
      } else {
        console.error("Failed to fetch followers");
      }
    } catch (error) {
      console.error("Error fetching followers:", error);
    }
  };

  const clearFollowers = (isFollowUpdate) => {
    console.log(isFollowUpdate);
    if (isFollowUpdate) {
      fetchUserProfile();
    }
    setFollowers(null);
  };

  const handleFollowLink = async (targetUserId) => {
    try {
      // APIを呼び出してフォロワー一覧を取得
      // targetUserIdが指定されているか確認してURLを切り替え
      const url = isOthersPage
        ? `${config.apiBaseUrl}/api/follow/followed/${targetUserId}`
        : `${config.apiBaseUrl}/api/follow/followed`;

      // APIを呼び出してフォロワー一覧を取得
      const response = await fetchWithAuth(url, {
        method: "GET",
      });

      if (response.ok) {
        const data = await response.json();

        // 取得したデータをフォーマットして followers にセット
        setFollowUsers(data);
      } else {
        console.error("Failed to fetch followers");
      }
    } catch (error) {
      console.error("Error fetching followers:", error);
    }
  };

  const clearFollowUsers = (isFollowUpdate) => {
    console.log(isFollowUpdate);

    if (isFollowUpdate) {
      fetchUserProfile();
    }
    setFollowUsers(null);
  };

  // 初回レンダリング時にユーザ情報を取得
  useEffect(() => {
    if (targetUserId && targetUserId !== localStorage.getItem("ll_userId")) {
      setIsOthersPage(true);
      setDrafts([]);
      fetchOthersProfile(targetUserId);
    } else {
      setIsOthersPage(false);
      fetchUserProfile();
    }
  }, [targetUserId]);

  const fetchOthersProfile = async (targetUserId) => {
    startLoading(); // ローディング開始
    try {
      const response = await fetchWithAuth(
        `${config.apiBaseUrl}/api/user/profile/${targetUserId}`,
        { method: "GET" }
      );
      if (response.ok) {
        const data = await response.json();
        setProfile(data);
        // setInitialProfile(data); // 初期プロファイルを設定
      } else {
        console.error("Failed to fetch profile");
      }
    } catch (error) {
      console.error("Error fetching profile:", error);
    } finally {
      stopLoading(); // ローディング終了
      setIsLoading(false); // ローディング状態を終了
    }
  };

  const fetchUserProfile = async () => {
    startLoading(); // ローディング開始
    try {
      const response = await fetchWithAuth(
        `${config.apiBaseUrl}/api/user/profile`,
        { method: "GET" }
      );
      if (response.ok) {
        const data = await response.json();
        setProfile(data);
        setInitialProfile(data); // 初期プロファイルを設定
      } else {
        console.error("Failed to fetch profile");
      }
      fetchDrafts();
    } catch (error) {
      console.error("Error fetching profile:", error);
    } finally {
      stopLoading(); // ローディング終了
      setIsLoading(false); // ローディング状態を終了
    }
  };

  const fetchDrafts = async () => {
    try {
      const response = await fetchWithAuth(
        `${config.apiBaseUrl}/api/blog/my-drafts`,
        {
          method: "GET",
        }
      );

      // レスポンスのステータス確認
      if (response.ok) {
        const data = await response.json();
        setDrafts(data); // APIの結果をdraftsにセット
      } else {
        console.error("下書きの取得に失敗しました");
      }
    } catch (error) {
      console.error("エラーが発生しました:", error);
    }
  };

  // 編集モードに切り替えた時の処理
  const handleEdit = () => {
    setIsEditing(true);
    setInitialProfile(profile); // 編集開始時に元のプロフィールをコピーして保存
  };
  const handleCancel = () => {
    setArtistSuggestions([]);
    setSearchArtistName("");
    setProfile(initialProfile);
    setIsEditing(false);
  };
  const handleSave = async () => {
    try {
      startLoading();
      const formData = new FormData();

      // ユーザ更新情報をFormDataに追加
      formData.append("displayName", profile.displayName);
      formData.append("selfIntroduction", profile.selfIntroduction);

      // selectedArtist配列が存在する場合、ループで個別に追加
      if (profile.favoriteArtistList && profile.favoriteArtistList.length > 0) {
        profile.favoriteArtistList.forEach((artist, index) => {
          formData.append(`favoriteArtistList[${index}].id`, artist.id);
          formData.append(`favoriteArtistList[${index}].name`, artist.name);
          formData.append(
            `favoriteArtistList[${index}].imageUrl`,
            artist.imageUrl
          );
        });
      } else {
        // 空の場合はnullを設定
        formData.append("favoriteArtistList", null);
      }

      // プロフィール画像が選択されている場合に追加
      if (croppedImage) {
        const response = await fetch(croppedImage);
        const blob = await response.blob();
        formData.append("profileImage", blob, "profileImage.png");
      }

      const response = await fetchWithAuth(
        `${config.apiBaseUrl}/api/user/update`,
        {
          method: "POST",
          body: formData,
        }
      );

      if (response.ok) {
        const updatedProfile = await response.json();
        setProfile(updatedProfile); // プロフィールを更新
        setArtistSuggestions([]);
        setSearchArtistName("");
        handleSuccessToast("プロフィールが保存されました");
        setIsEditing(false); // 編集モードを終了
      } else {
        console.error("プロフィールの更新に失敗しました");
      }
    } catch (error) {
      console.error("エラーが発生しました:", error);
    } finally {
      stopLoading();
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setProfile((prev) => ({ ...prev, [name]: value }));
  };

  const handleProfileImageChange = (e) => {
    const file = e.target.files[0];
    if (!file) {
      return;
    }
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => {
      setSelectedImage(reader.result);
    };
  };

  const onCropComplete = useCallback((croppedArea, croppedAreaPixels) => {
    setCroppedAreaPixels(croppedAreaPixels);
  }, []);

  const fileInputRef = useRef(null);
  const artistInputRef = useRef(null);

  const showCroppedImage = useCallback(async () => {
    try {
      const croppedImage = await getCroppedImg(
        selectedImage,
        croppedAreaPixels
      );
      setCroppedImage(croppedImage);
      setSelectedImage(null);
      // input要素のファイル選択をリセット
      if (fileInputRef.current) {
        fileInputRef.current.value = ""; // これでinputの選択もリセットされる
      }
    } catch (e) {
      console.error(e);
    }
  }, [selectedImage, croppedAreaPixels]);

  // プロフィール画像またはアイコンをクリックして画像選択を開始
  const triggerImageSelect = () => {
    document.getElementById("profileImageInput").click();
  };

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
    stopLoading();
  };

  const addSelectedArtist = (artist) => {
    const newArtist = {
      id: artist.id,
      name: artist.name,
      imageUrl: artist.images[0].url,
    };
    setProfile((prevProfile) => ({
      ...prevProfile, // profileの他のプロパティはそのまま維持
      favoriteArtistList: [...prevProfile.favoriteArtistList, newArtist], // artistListに新しいアーティストを追加
    }));
    // アーティストを選択した後、artistSuggestionsから削除
    setArtistSuggestions((prevSuggestions) =>
      prevSuggestions.filter((suggestion) => suggestion.id !== artist.id)
    );
  };

  const removeArtist = (id) => {
    // artistListから指定されたidを持つアーティストを除外
    const updatedArtistList = profile.favoriteArtistList.filter(
      (artist) => artist.id !== id
    );

    // profileの状態を更新
    setProfile((prevProfile) => ({
      ...prevProfile, // 既存のprofileの他のプロパティを維持
      favoriteArtistList: updatedArtistList, // 更新されたartistListをセット
    }));
  };

  const handleFollow = async (targetUserId) => {
    console.log(`${targetUserId}をフォローします。`);
    try {
      const response = await fetchWithAuth(
        `${config.apiBaseUrl}/api/follow/${targetUserId}`, // APIのエンドポイント
        {
          method: "POST", // POSTメソッドを使用
        }
      );

      if (response.ok) {
        console.log(`${targetUserId}を正常にフォローしました。`);
        // profileのisFollowをtrueに更新
        setProfile((prevProfile) => ({
          ...prevProfile, // 他のプロパティを保持
          isFollow: true, // isFollowをtrueに設定
        }));
      } else {
        console.error("フォローに失敗しました。");
      }
    } catch (error) {
      console.error("エラーが発生しました:", error);
    }
  };

  const handleClearFollow = async (targetUserId) => {
    console.log(`${targetUserId}をフォロー解除します。`);
    try {
      const response = await fetchWithAuth(
        `${config.apiBaseUrl}/api/follow/cancel/${targetUserId}`, // APIのエンドポイント
        {
          method: "POST", // POSTメソッドを使用
        }
      );

      if (response.ok) {
        console.log(`${targetUserId}のフォローを正常に解除しました。`);
        // profileのisFollowをfalseに更新
        setProfile((prevProfile) => ({
          ...prevProfile, // 他のプロパティを保持
          isFollow: false, // isFollowをfalseに設定
        }));
      } else {
        console.error("フォロー解除に失敗しました。");
      }
    } catch (error) {
      console.error("エラーが発生しました:", error);
    }
  };

  if (isLoading) {
    // ローディング中はこの部分が表示される
    return (
      <div className="flex justify-center items-center h-screen">
        <div className="text-center">
          <div className="loader"></div>
        </div>
      </div>
    );
  }
  return (
    <div>
      <Header
        isAuthenticated={isAuthenticated}
        setIsAuthenticated={setIsAuthenticated}
        // userInfo={profile} // 取得したuserInfoをHeaderに渡す
      />

      <div className="container mx-auto p-4 font-sans">
        <div className="bg-white shadow-lg rounded-lg p-6">
          <div className="flex flex-col md:flex-row md:items-start mb-6">
            {isEditing ? (
              <>
                <div
                  className="relative cursor-pointer w-32 h-32 mb-4 md:mb-0 md:mr-6"
                  onClick={triggerImageSelect}
                >
                  {croppedImage ? (
                    <img
                      src={croppedImage}
                      alt="選択されたプロフィール画像"
                      className={`w-full h-full rounded-full object-cover ${isEditing ? "filter grayscale" : ""}`} // isEditingがtrueならグレーにする
                    />
                  ) : profile.profileImageUrl ? (
                    <img
                      src={`${config.apiBaseUrl}/api/public/files/${profile.profileImageUrl}`}
                      alt="ユーザーのプロフィール画像"
                      className={`w-full h-full rounded-full object-cover ${isEditing ? "filter grayscale" : ""}`} // isEditingがtrueならグレーにする
                    />
                  ) : (
                    <i className="fas fa-user fa-5x text-blue-300 w-full h-full rounded-full"></i>
                  )}

                  {/* アイコンを中央に表示 (isEditing が true の場合のみ表示) */}
                  {isEditing && (
                    <div className="absolute inset-0 flex items-center justify-center">
                      <i className="fas fa-camera fa-2x text-white"></i>{" "}
                      {/* カメラアイコン */}
                    </div>
                  )}
                </div>{" "}
                {selectedImage && (
                  <>
                    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-75 z-50">
                      <div className="relative w-full max-w-md mx-auto p-4 bg-white rounded-lg shadow-lg">
                        <div className="relative w-full h-64">
                          <Cropper
                            image={selectedImage}
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
                <input
                  id="profileImageInput"
                  type="file"
                  className="hidden"
                  onChange={handleProfileImageChange}
                />
              </>
            ) : (
              <>
                {profile.profileImageUrl ? (
                  <img
                    src={`${config.apiBaseUrl}/api/public/files/${profile.profileImageUrl}`}
                    alt="ユーザーのプロフィール画像"
                    className="w-32 h-32 rounded-full mb-4 md:mb-0 md:mr-6"
                  />
                ) : (
                  <i className="fas fa-user fa-5x text-blue-300 w-32 h-32 rounded-full pl-3 mb-4 md:mb-0 md:mr-6"></i>
                )}
              </>
            )}
            <div className="flex-1">
              <div className="flex justify-between items-center mb-2">
                {isEditing ? (
                  <input
                    type="text"
                    name="displayName"
                    value={profile.displayName || ""}
                    onChange={handleChange}
                    placeholder="ユーザ名を入力"
                    className="text-2xl font-bold w-full border rounded p-1"
                  />
                ) : (
                  <h1 className="text-2xl font-bold">{profile.displayName}</h1>
                )}
                {/* 他ユーザのプロフィール表示時は編集不可 */}
                {!isOthersPage && !isEditing ? (
                  <button
                    onClick={handleEdit}
                    className="bg-gray-500 text-white px-4 py-2 rounded-xl hover:bg-blue-600"
                  >
                    <i className="fa-solid fa-pen-to-square"></i>
                  </button>
                ) : (
                  <></>
                )}
              </div>
              {isEditing ? (
                <textarea
                  name="selfIntroduction"
                  value={profile.selfIntroduction || ""}
                  placeholder="プロフィールを入力"
                  onChange={handleChange}
                  className="w-full h-24 border rounded p-1"
                />
              ) : (
                <p className="text-gray-600">
                  {profile.selfIntroduction
                    ? profile.selfIntroduction
                    : "※プロフィールは設定されていません。"}
                </p>
              )}
            </div>
          </div>

          {!isEditing && (
            <div className="mb-4">
              <a
                href="#"
                className="text-blue-500 hover:underline"
                onClick={() => handleFollowLink(targetUserId)}
              >
                フォロー中: {profile.followedCount}
              </a>
              <a
                href="#"
                className="ml-4 text-blue-500 hover:underline"
                onClick={() => handleFollowerLink(targetUserId)}
              >
                フォロワー: {profile.followerCount}
              </a>
            </div>
          )}
          {isOthersPage && (
            <div className="mb-6">
              {profile.isFollow ? (
                <button
                  className="bg-white text-black border border-gray-300 py-2 px-4 rounded-full w-5/12 sm:w-3/12"
                  onClick={() => handleClearFollow(profile.userId)}
                >
                  フォロー中
                </button>
              ) : (
                <button
                  className="bg-blue-500 text-white py-2 px-4 rounded-full w-5/12 sm:w-3/12"
                  onClick={() => handleFollow(profile.userId)}
                >
                  フォローする
                </button>
              )}
            </div>
          )}

          <div className="mb-6 items-center">
            <div className="mb-6 items-center">
              {!isEditing && (
                <div className="mb-6">
                  <h2 className="text-xl font-semibold mb-2">公開記事</h2>

                  {/* 公開記事のグリッドレイアウト */}
                  <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                    {profile.createdBlogList.map((blog) => (
                      <div
                        key={blog.id}
                        className="bg-gray-100 p-2 rounded cursor-pointer shadow-lg hover:shadow-xl transition-shadow"
                        onClick={() => navigate(`/blog/${blog.id}`)}
                      >
                        {/* サムネイル画像 */}
                        {blog.thumbnailUrl ? (
                          <img
                            src={`${config.apiBaseUrl}/api/public/files/${blog.thumbnailUrl}`}
                            alt={`${blog.title}のサムネイル`}
                            className="w-full h-36 object-cover rounded-t-lg mb-4"
                          />
                        ) : (
                          <img
                            src={noImage} // カスタムプレースホルダー画像
                            alt="サムネイルがありません"
                            className="w-full h-36 object-cover rounded-t-lg mb-4"
                          />
                        )}
                        <p className="font-medium">{blog.title}</p>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {drafts.length > 0 && !isEditing && (
                <div className="mb-6">
                  <h2 className="text-xl font-semibold mb-2">下書き一覧</h2>

                  {/* 下書き一覧のグリッドレイアウト */}
                  <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                    {drafts.map((draft) => (
                      <div
                        key={draft.id}
                        className="bg-gray-100 p-2 rounded cursor-pointer shadow-lg hover:shadow-xl transition-shadow"
                        onClick={() => navigate(`/blog/edit/${draft.id}`)}
                      >
                        <p className="font-medium">{draft.title}</p>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              <h2 className="text-xl font-semibold mb-2">好きなアーティスト</h2>

              {/* 好きなアーティストのグリッドレイアウト */}
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-2">
                {profile.favoriteArtistList.map((artist, index) => (
                  <div
                    key={index}
                    className="bg-gray-100 p-2 rounded flex items-center shadow-lg"
                  >
                    <img
                      src={artist.imageUrl}
                      alt={artist.name}
                      className="w-12 h-12 rounded-full mr-4"
                    />
                    <span className="flex-grow">{artist.name}</span>
                    {isEditing && (
                      <button
                        type="button"
                        onClick={() => removeArtist(artist.id)}
                        className="text-red-500 hover:text-red-700 ml-auto"
                      >
                        &times;
                      </button>
                    )}
                  </div>
                ))}
              </div>
            </div>
            {isEditing && (
              <>
                <div className="flex mt-12">
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
                        onClick={() => addSelectedArtist(artist)}
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
              </>
            )}
          </div>

          {isEditing && (
            <div className="mt-24 flex justify-center space-x-4">
              <button
                onClick={handleSave}
                className="bg-green-500 text-white w-32 py-2 rounded hover:bg-green-600"
              >
                保存
              </button>
              <button
                onClick={handleCancel}
                className="bg-gray-500 text-white w-32 py-2 rounded hover:bg-gray-600"
              >
                キャンセル
              </button>
            </div>
          )}
        </div>
      </div>
      {followUsers && (
        <FollowListModal
          isFollow={true}
          usersInfo={followUsers}
          setUsersInfo={setFollowUsers}
          onClose={clearFollowUsers}
          isOthersInfo={isOthersPage ? true : false}
        />
      )}
      {followers && (
        <FollowListModal
          isFollow={false}
          usersInfo={followers}
          setUsersInfo={setFollowers}
          onClose={clearFollowers}
          isOthersInfo={isOthersPage ? true : false}
        />
      )}
    </div>
  );
}

export default User;
